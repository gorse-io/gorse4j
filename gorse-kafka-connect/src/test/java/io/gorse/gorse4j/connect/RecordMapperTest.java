package io.gorse.gorse4j.connect;

import io.gorse.gorse4j.Feedback;
import io.gorse.gorse4j.Item;
import io.gorse.gorse4j.User;
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

        List<Object> users = mapper.toGorseRecords(record);

        Assert.assertEquals(1, users.size());
        User user = (User) users.get(0);
        Assert.assertEquals("u1", user.getUserId());
        Assert.assertEquals(Map.of("role", "admin"), user.getLabels());
        Assert.assertEquals("test user", user.getComment());
    }

    @Test
    public void testMapFeedbackWithFieldOverrides() {
        RecordMapper mapper = new RecordMapper(new GorseSinkConfig(Map.of(
                "gorse.endpoint", "http://127.0.0.1:8088",
                "gorse.api.key", "api-key",
                "topic.events.entity", "feedback",
                "topic.events.field.feedback_type", "event.type",
                "topic.events.field.user_id", "user.id",
                "topic.events.field.item_id", "item.id",
                "topic.events.field.value", "score"
        )));
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("event", Map.of("type", "read"));
        value.put("user", Map.of("id", "u1"));
        value.put("item", Map.of("id", "i1"));
        value.put("score", 1.0);
        SinkRecord record = new SinkRecord("events", 0, null, null, null, value, 1L);

        List<Object> feedback = mapper.toGorseRecords(record);

        Assert.assertEquals(1, feedback.size());
        Feedback event = (Feedback) feedback.get(0);
        Assert.assertEquals("read", event.getFeedbackType());
        Assert.assertEquals("u1", event.getUserId());
        Assert.assertEquals("i1", event.getItemId());
        Assert.assertEquals(1.0, event.getValue(), 0.0);
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

        List<Object> items = mapper.toGorseRecords(record);

        Assert.assertEquals(2, items.size());
        Assert.assertEquals("i1", ((Item) items.get(0)).getItemId());
        Assert.assertEquals("i2", ((Item) items.get(1)).getItemId());
        Assert.assertEquals("second", ((Item) items.get(1)).getComment());
    }
}
