package io.gorse.gorse4j.connect;

import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecordMapperTest {

    @Test
    public void testMapUserWithDefaults() {
        RecordMapper mapper = new RecordMapper(new GorseSinkConfig(Map.of(
                "gorse.endpoint", "http://127.0.0.1:8088",
                "gorse.api.key", "api-key",
                "gorse.entity", "user"
        )));
        SinkRecord record = new SinkRecord("users", 0, null, null, null, Map.of(
                "UserId", "u1",
                "Labels", Map.of("role", "admin"),
                "Comment", "test user"
        ), 1L);

        List<Map<String, Object>> users = mapper.toGorseRecords(record);

        Assert.assertEquals(1, users.size());
        Assert.assertEquals("u1", users.get(0).get("UserId"));
        Assert.assertEquals(Map.of("role", "admin"), users.get(0).get("Labels"));
        Assert.assertEquals("test user", users.get(0).get("Comment"));
    }

    @Test
    public void testMapFeedbackWithFieldOverridesAndDefaultType() {
        RecordMapper mapper = new RecordMapper(new GorseSinkConfig(Map.of(
                "gorse.endpoint", "http://127.0.0.1:8088",
                "gorse.api.key", "api-key",
                "topic.events.entity", "feedback",
                "topic.events.field.user_id", "user.id",
                "topic.events.field.item_id", "item.id",
                "topic.events.field.value", "score",
                "default.feedback_type", "read"
        )));
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("user", Map.of("id", "u1"));
        value.put("item", Map.of("id", "i1"));
        value.put("score", 1.0);
        SinkRecord record = new SinkRecord("events", 0, null, null, null, value, 1L);

        List<Map<String, Object>> feedback = mapper.toGorseRecords(record);

        Assert.assertEquals(1, feedback.size());
        Assert.assertEquals("read", feedback.get(0).get("FeedbackType"));
        Assert.assertEquals("u1", feedback.get(0).get("UserId"));
        Assert.assertEquals("i1", feedback.get(0).get("ItemId"));
        Assert.assertEquals(1.0, feedback.get(0).get("Value"));
    }

    @Test
    public void testMapJsonItemArray() {
        RecordMapper mapper = new RecordMapper(new GorseSinkConfig(Map.of(
                "gorse.endpoint", "http://127.0.0.1:8088",
                "gorse.api.key", "api-key",
                "gorse.entity", "item"
        )));
        SinkRecord record = new SinkRecord("items", 0, null, null, null,
                "[{\"ItemId\":\"i1\"},{\"ItemId\":\"i2\",\"Comment\":\"second\"}]",
                1L);

        List<Map<String, Object>> items = mapper.toGorseRecords(record);

        Assert.assertEquals(2, items.size());
        Assert.assertEquals("i1", items.get(0).get("ItemId"));
        Assert.assertEquals("i2", items.get(1).get("ItemId"));
        Assert.assertEquals("second", items.get(1).get("Comment"));
    }
}
