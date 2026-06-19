package io.gorse.gorse4j.connect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gorse.gorse4j.Feedback;
import io.gorse.gorse4j.Item;
import io.gorse.gorse4j.User;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class RecordMapper {

    private static final String FIELD_FEEDBACK_TYPE = "feedback_type";
    private static final String FIELD_USER_ID = "user_id";
    private static final String FIELD_ITEM_ID = "item_id";
    private static final String FIELD_VALUE = "value";
    private static final String FIELD_TIMESTAMP = "timestamp";
    private static final String FIELD_LABELS = "labels";
    private static final String FIELD_COMMENT = "comment";
    private static final String FIELD_IS_HIDDEN = "is_hidden";
    private static final String FIELD_CATEGORIES = "categories";

    private static final String DEFAULT_FEEDBACK_TYPE_PATHS = "FeedbackType,feedback_type,feedbackType,type";
    private static final String DEFAULT_USER_ID_PATHS = "UserId,user_id,userId,uid";
    private static final String DEFAULT_ITEM_ID_PATHS = "ItemId,item_id,itemId,iid";
    private static final String DEFAULT_VALUE_PATHS = "Value,value,score";
    private static final String DEFAULT_TIMESTAMP_PATHS = "Timestamp,timestamp,time,created_at,createdAt";
    private static final String DEFAULT_LABELS_PATHS = "Labels,labels";
    private static final String DEFAULT_COMMENT_PATHS = "Comment,comment";
    private static final String DEFAULT_IS_HIDDEN_PATHS = "IsHidden,is_hidden,isHidden,hidden";
    private static final String DEFAULT_CATEGORIES_PATHS = "Categories,categories";

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };
    private static final TypeReference<List<Map<String, Object>>> LIST_OF_MAPS_TYPE = new TypeReference<>() {
    };

    private final GorseSinkConfig config;
    private final ObjectMapper mapper = new ObjectMapper();

    RecordMapper(GorseSinkConfig config) {
        this.config = config;
    }

    List<Object> toGorseRecords(SinkRecord record) {
        GorseSinkConfig.EntityType entityType = config.entityForTopic(record.topic());
        List<Map<String, Object>> values = valuesAsMaps(record.value());
        List<Object> result = new ArrayList<>(values.size());
        for (Map<String, Object> value : values) {
            switch (entityType) {
                case USER:
                    result.add(toUser(record.topic(), value));
                    break;
                case ITEM:
                    result.add(toItem(record.topic(), value));
                    break;
                case FEEDBACK:
                    result.add(toFeedback(record.topic(), value));
                    break;
                default:
                    throw new DataException("unsupported Gorse entity: " + entityType);
            }
        }
        return result;
    }

    private User toUser(String topic, Map<String, Object> value) {
        return new User(
                stringValue(required(value, topic, FIELD_USER_ID, DEFAULT_USER_ID_PATHS)),
                find(value, topic, FIELD_LABELS, DEFAULT_LABELS_PATHS),
                stringValue(find(value, topic, FIELD_COMMENT, DEFAULT_COMMENT_PATHS)));
    }

    private Item toItem(String topic, Map<String, Object> value) {
        return new Item(
                stringValue(required(value, topic, FIELD_ITEM_ID, DEFAULT_ITEM_ID_PATHS)),
                booleanValue(find(value, topic, FIELD_IS_HIDDEN, DEFAULT_IS_HIDDEN_PATHS)),
                find(value, topic, FIELD_LABELS, DEFAULT_LABELS_PATHS),
                stringList(find(value, topic, FIELD_CATEGORIES, DEFAULT_CATEGORIES_PATHS)),
                stringValue(find(value, topic, FIELD_TIMESTAMP, DEFAULT_TIMESTAMP_PATHS)),
                stringValue(find(value, topic, FIELD_COMMENT, DEFAULT_COMMENT_PATHS)));
    }

    private Feedback toFeedback(String topic, Map<String, Object> value) {
        Object feedbackType = find(value, topic, FIELD_FEEDBACK_TYPE, DEFAULT_FEEDBACK_TYPE_PATHS);
        if (feedbackType == null && !config.defaultFeedbackType().isBlank()) {
            feedbackType = config.defaultFeedbackType();
        }
        if (feedbackType == null) {
            throw new DataException("missing required field: " + FIELD_FEEDBACK_TYPE);
        }
        return new Feedback(
                stringValue(feedbackType),
                stringValue(required(value, topic, FIELD_USER_ID, DEFAULT_USER_ID_PATHS)),
                stringValue(required(value, topic, FIELD_ITEM_ID, DEFAULT_ITEM_ID_PATHS)),
                doubleValue(find(value, topic, FIELD_VALUE, DEFAULT_VALUE_PATHS)),
                stringValue(find(value, topic, FIELD_TIMESTAMP, DEFAULT_TIMESTAMP_PATHS)),
                find(value, topic, FIELD_LABELS, DEFAULT_LABELS_PATHS),
                stringValue(find(value, topic, FIELD_COMMENT, DEFAULT_COMMENT_PATHS)));
    }

    private Object required(
            Map<String, Object> value,
            String topic,
            String fieldName,
            String defaultPaths) {
        Object fieldValue = find(value, topic, fieldName, defaultPaths);
        if (fieldValue == null) {
            throw new DataException("missing required field: " + fieldName);
        }
        return fieldValue;
    }

    private Object find(
            Map<String, Object> value,
            String topic,
            String fieldName,
            String defaultPaths) {
        String paths = config.fieldPaths(topic, fieldName, defaultPaths);
        for (String path : paths.split(",")) {
            Object fieldValue = getPath(value, path.trim());
            if (fieldValue != null) {
                return fieldValue;
            }
        }
        return null;
    }

    private Object getPath(Map<String, Object> value, String path) {
        if (path.isEmpty()) {
            return null;
        }
        Object current = value;
        for (String segment : path.split("\\.")) {
            if (!(current instanceof Map)) {
                return null;
            }
            current = ((Map<?, ?>) current).get(segment);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private static String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Boolean booleanValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.valueOf(String.valueOf(value));
    }

    private static double doubleValue(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }

    private static List<String> stringList(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Collection) {
            List<String> result = new ArrayList<>();
            for (Object element : (Collection<?>) value) {
                result.add(stringValue(element));
            }
            return result;
        }
        return List.of(stringValue(value));
    }

    private List<Map<String, Object>> valuesAsMaps(Object value) {
        if (value == null) {
            return List.of();
        }
        if (value instanceof String) {
            return jsonAsMaps(((String) value).getBytes(StandardCharsets.UTF_8));
        }
        if (value instanceof byte[]) {
            return jsonAsMaps((byte[]) value);
        }
        if (value instanceof ByteBuffer) {
            ByteBuffer buffer = ((ByteBuffer) value).slice();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return jsonAsMaps(bytes);
        }
        if (value instanceof Collection) {
            List<Map<String, Object>> values = new ArrayList<>();
            for (Object element : (Collection<?>) value) {
                values.add(valueAsMap(element));
            }
            return values;
        }
        if (value.getClass().isArray() && !(value instanceof byte[])) {
            return mapper.convertValue(value, LIST_OF_MAPS_TYPE);
        }
        return List.of(valueAsMap(value));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> valueAsMap(Object value) {
        if (value instanceof Map) {
            Map<String, Object> result = new LinkedHashMap<>();
            ((Map<?, ?>) value).forEach((key, fieldValue) -> result.put(String.valueOf(key), fieldValue));
            return result;
        }
        if (value instanceof Struct) {
            return structAsMap((Struct) value);
        }
        return mapper.convertValue(value, MAP_TYPE);
    }

    private Map<String, Object> structAsMap(Struct struct) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Field field : struct.schema().fields()) {
            Object value = struct.get(field);
            if (value instanceof Struct) {
                value = structAsMap((Struct) value);
            }
            result.put(field.name(), value);
        }
        return result;
    }

    private List<Map<String, Object>> jsonAsMaps(byte[] bytes) {
        try {
            String json = new String(bytes, StandardCharsets.UTF_8).trim();
            if (json.startsWith("[")) {
                return mapper.readValue(json, LIST_OF_MAPS_TYPE);
            }
            return List.of(mapper.readValue(json, MAP_TYPE));
        } catch (IOException e) {
            throw new DataException("failed to parse JSON Kafka record value", e);
        }
    }
}
