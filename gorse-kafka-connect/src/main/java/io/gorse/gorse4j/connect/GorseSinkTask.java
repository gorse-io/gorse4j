package io.gorse.gorse4j.connect;

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
    private GorseHttpClient client;
    private RecordMapper mapper;

    @Override
    public String version() {
        return GorseVersion.version();
    }

    @Override
    public void start(Map<String, String> props) {
        GorseSinkConfig.configDef().parse(props);
        config = new GorseSinkConfig(props);
        client = new GorseHttpClient(config.endpoint(), config.apiKey());
        mapper = new RecordMapper(config);
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        Map<GorseSinkConfig.EntityType, List<Map<String, Object>>> batches =
                new EnumMap<>(GorseSinkConfig.EntityType.class);
        for (SinkRecord record : records) {
            GorseSinkConfig.EntityType entityType = config.entityForTopic(record.topic());
            List<Map<String, Object>> mappedRecords;
            try {
                mappedRecords = mapper.toGorseRecords(record);
            } catch (RuntimeException e) {
                throw new DataException("failed to map record from topic " + record.topic()
                        + " partition " + record.kafkaPartition()
                        + " offset " + record.kafkaOffset(), e);
            }
            List<Map<String, Object>> batch = batches.computeIfAbsent(entityType, ignored -> new ArrayList<>());
            for (Map<String, Object> mappedRecord : mappedRecords) {
                batch.add(mappedRecord);
                flushIfNeeded(entityType, batch);
            }
        }
        for (Map.Entry<GorseSinkConfig.EntityType, List<Map<String, Object>>> entry : batches.entrySet()) {
            flush(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void stop() {
    }

    private void flushIfNeeded(GorseSinkConfig.EntityType entityType, List<Map<String, Object>> batch) {
        if (batch.size() >= config.batchSize()) {
            flush(entityType, batch);
        }
    }

    private void flush(GorseSinkConfig.EntityType entityType, List<Map<String, Object>> batch) {
        if (batch.isEmpty()) {
            return;
        }
        try {
            switch (entityType) {
                case USER:
                    client.insertUsers(batch);
                    break;
                case ITEM:
                    client.insertItems(batch);
                    break;
                case FEEDBACK:
                    client.insertFeedback(batch);
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
}
