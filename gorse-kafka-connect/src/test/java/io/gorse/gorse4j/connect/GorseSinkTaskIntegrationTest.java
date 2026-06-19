package io.gorse.gorse4j.connect;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.github.embeddedkafka.EmbeddedK;
import io.github.embeddedkafka.EmbeddedKafka$;
import io.github.embeddedkafka.EmbeddedKafkaConfig;
import io.github.embeddedkafka.EmbeddedKafkaConfig$;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scala.collection.immutable.Map$;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GorseSinkTaskIntegrationTest {

    private EmbeddedK kafka;
    private String bootstrapServers;
    private HttpServer gorseServer;
    private String gorseEndpoint;
    private final List<CapturedRequest> requests = Collections.synchronizedList(new ArrayList<>());

    @Before
    public void setUp() throws IOException {
        EmbeddedKafkaConfig kafkaConfig = EmbeddedKafkaConfig$.MODULE$.apply(
                0,
                0,
                Map$.MODULE$.empty(),
                Map$.MODULE$.empty(),
                Map$.MODULE$.empty());
        kafka = EmbeddedKafka$.MODULE$.start(kafkaConfig);
        bootstrapServers = "127.0.0.1:" + EmbeddedKafka$.MODULE$.kafkaPort(kafka.broker());

        gorseServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        gorseServer.createContext("/api/feedback", this::captureGorseRequest);
        gorseServer.start();
        gorseEndpoint = "http://127.0.0.1:" + gorseServer.getAddress().getPort();
    }

    @After
    public void tearDown() {
        if (gorseServer != null) {
            gorseServer.stop(0);
        }
        if (kafka != null) {
            EmbeddedKafka$.MODULE$.stop(kafka);
        }
    }

    @Test
    public void testSinkTaskWritesKafkaFeedbackToGorse() throws Exception {
        String topic = "feedback-" + UUID.randomUUID();
        createTopic(topic);
        publish(topic, "{\"FeedbackType\":\"click\",\"UserId\":\"u1\",\"ItemId\":\"i1\",\"Value\":1}");
        publish(topic, "{\"FeedbackType\":\"read\",\"UserId\":\"u2\",\"ItemId\":\"i2\",\"Value\":2}");

        List<SinkRecord> sinkRecords = consumeSinkRecords(topic, 2);

        GorseSinkTask task = new GorseSinkTask();
        task.start(java.util.Map.of(
                "gorse.endpoint", gorseEndpoint,
                "gorse.api.key", "api-key",
                "gorse.entity", "feedback",
                "gorse.batch.size", "2"));
        task.put(sinkRecords);
        task.stop();

        Assert.assertEquals(1, requests.size());
        CapturedRequest request = requests.get(0);
        Assert.assertEquals("POST", request.method);
        Assert.assertEquals("/api/feedback", request.path);
        Assert.assertEquals("api-key", request.apiKey);
        Assert.assertTrue(request.body.contains("\"FeedbackType\":\"click\""));
        Assert.assertTrue(request.body.contains("\"UserId\":\"u1\""));
        Assert.assertTrue(request.body.contains("\"ItemId\":\"i2\""));
    }

    private void createTopic(String topic) throws Exception {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        try (AdminClient adminClient = AdminClient.create(props)) {
            adminClient.createTopics(List.of(new NewTopic(topic, 1, (short) 1)))
                    .all()
                    .get(10, TimeUnit.SECONDS);
        }
    }

    private void publish(String topic, String value) throws Exception {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            producer.send(new ProducerRecord<>(topic, value)).get(10, TimeUnit.SECONDS);
        }
    }

    private List<SinkRecord> consumeSinkRecords(String topic, int count) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "gorse-sink-test-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        List<SinkRecord> sinkRecords = new ArrayList<>();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of(topic));
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
            while (sinkRecords.size() < count && System.nanoTime() < deadline) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
                for (ConsumerRecord<String, String> record : records) {
                    sinkRecords.add(new SinkRecord(
                            record.topic(),
                            record.partition(),
                            null,
                            record.key(),
                            null,
                            record.value(),
                            record.offset()));
                }
            }
        }
        Assert.assertEquals(count, sinkRecords.size());
        return sinkRecords;
    }

    private void captureGorseRequest(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        requests.add(new CapturedRequest(
                exchange.getRequestMethod(),
                exchange.getRequestURI().getPath(),
                exchange.getRequestHeaders().getFirst("X-API-Key"),
                body));
        byte[] response = "{\"RowAffected\":1}".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    private static final class CapturedRequest {
        private final String method;
        private final String path;
        private final String apiKey;
        private final String body;

        private CapturedRequest(String method, String path, String apiKey, String body) {
            this.method = method;
            this.path = path;
            this.apiKey = apiKey;
            this.body = body;
        }
    }
}
