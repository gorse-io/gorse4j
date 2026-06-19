package io.gorse.gorse4j.connect;

import io.gorse.gorse4j.Feedback;
import io.gorse.gorse4j.Gorse;
import io.gorse.gorse4j.Item;
import io.gorse.gorse4j.User;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GorseSinkTask extends SinkTask {

    private GorseSinkConfig config;
    private Gorse client;
    private RecordMapper mapper;

    @Override
    public String version() {
        return GorseVersion.version();
    }

    @Override
    public void start(Map<String, String> props) {
        GorseSinkConfig.configDef().parse(props);
        config = new GorseSinkConfig(props);
        client = new Gorse(config.endpoint(), config.apiKey());
        mapper = new RecordMapper(config);
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        Map<GorseSinkConfig.EntityType, List<Object>> batches =
                new EnumMap<>(GorseSinkConfig.EntityType.class);
        for (SinkRecord record : records) {
            GorseSinkConfig.EntityType entityType = config.entityForTopic(record.topic());
            List<Object> mappedRecords;
            try {
                mappedRecords = mapper.toGorseRecords(record);
            } catch (RuntimeException e) {
                throw new DataException("failed to map record from topic " + record.topic()
                        + " partition " + record.kafkaPartition()
                        + " offset " + record.kafkaOffset(), e);
            }
            List<Object> batch = batches.computeIfAbsent(entityType, ignored -> new ArrayList<>());
            for (Object mappedRecord : mappedRecords) {
                batch.add(mappedRecord);
                flushIfNeeded(entityType, batch);
            }
        }
        for (Map.Entry<GorseSinkConfig.EntityType, List<Object>> entry : batches.entrySet()) {
            flush(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void stop() {
    }

    private void flushIfNeeded(GorseSinkConfig.EntityType entityType, List<Object> batch) {
        if (batch.size() >= config.batchSize()) {
            flush(entityType, batch);
        }
    }

    private void flush(GorseSinkConfig.EntityType entityType, List<Object> batch) {
        if (batch.isEmpty()) {
            return;
        }
        try {
            switch (entityType) {
                case USER:
                    client.insertUsers(castBatch(batch, User.class));
                    break;
                case ITEM:
                    client.insertItems(castBatch(batch, Item.class));
                    break;
                case FEEDBACK:
                    client.insertFeedback(castBatch(batch, Feedback.class));
                    break;
                default:
                    throw new ConnectException("unsupported Gorse entity: " + entityType);
            }
        } catch (IOException e) {
            throw new ConnectException("failed to write " + batch.size() + " " + entityType + " records to Gorse", e);
        } finally {
            batch.clear();
        }
    }

    private static <T> List<T> castBatch(List<Object> batch, Class<T> type) {
        List<T> result = new ArrayList<>(batch.size());
        for (Object value : batch) {
            result.add(type.cast(value));
        }
        return result;
    }
}
