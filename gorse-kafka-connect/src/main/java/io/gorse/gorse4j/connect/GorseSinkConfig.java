package io.gorse.gorse4j.connect;

import org.apache.kafka.common.config.ConfigDef;

import java.util.Locale;
import java.util.Map;

final class GorseSinkConfig {

    static final String ENDPOINT_CONFIG = "gorse.endpoint";
    static final String API_KEY_CONFIG = "gorse.api.key";
    static final String ENTITY_CONFIG = "gorse.entity";
    static final String BATCH_SIZE_CONFIG = "gorse.batch.size";
    static final String DEFAULT_FEEDBACK_TYPE_CONFIG = "default.feedback_type";

    private static final int DEFAULT_BATCH_SIZE = 500;

    private final Map<String, String> props;

    GorseSinkConfig(Map<String, String> props) {
        this.props = props;
    }

    static ConfigDef configDef() {
        return new ConfigDef()
                .define(
                        ENDPOINT_CONFIG,
                        ConfigDef.Type.STRING,
                        ConfigDef.Importance.HIGH,
                        "Gorse endpoint, for example http://gorse-server:8088.")
                .define(
                        API_KEY_CONFIG,
                        ConfigDef.Type.PASSWORD,
                        ConfigDef.Importance.HIGH,
                        "Gorse API key.")
                .define(
                        ENTITY_CONFIG,
                        ConfigDef.Type.STRING,
                        "",
                        ConfigDef.Importance.MEDIUM,
                        "Default entity type for all topics: user, item, or feedback. "
                                + "Can be overridden with topic.<topic>.entity.")
                .define(
                        BATCH_SIZE_CONFIG,
                        ConfigDef.Type.INT,
                        DEFAULT_BATCH_SIZE,
                        ConfigDef.Range.atLeast(1),
                        ConfigDef.Importance.MEDIUM,
                        "Maximum number of records to send to Gorse in one HTTP request.")
                .define(
                        DEFAULT_FEEDBACK_TYPE_CONFIG,
                        ConfigDef.Type.STRING,
                        "",
                        ConfigDef.Importance.LOW,
                        "Default FeedbackType used when feedback records do not contain one.");
    }

    String endpoint() {
        return required(ENDPOINT_CONFIG);
    }

    String apiKey() {
        return required(API_KEY_CONFIG);
    }

    int batchSize() {
        String value = props.get(BATCH_SIZE_CONFIG);
        if (value == null || value.isBlank()) {
            return DEFAULT_BATCH_SIZE;
        }
        return Integer.parseInt(value);
    }

    String defaultFeedbackType() {
        return props.getOrDefault(DEFAULT_FEEDBACK_TYPE_CONFIG, "");
    }

    EntityType entityForTopic(String topic) {
        String configured = firstNonBlank(
                props.get("topic." + topic + ".entity"),
                props.get(ENTITY_CONFIG));
        if (configured != null) {
            return EntityType.from(configured);
        }
        return EntityType.infer(topic);
    }

    String fieldPaths(String topic, String fieldName, String defaults) {
        return firstNonBlank(
                props.get("topic." + topic + ".field." + fieldName),
                props.get("field." + fieldName),
                defaults);
    }

    private String required(String key) {
        String value = props.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("missing required config: " + key);
        }
        return value;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    enum EntityType {
        USER,
        ITEM,
        FEEDBACK;

        static EntityType from(String value) {
            String normalized = value.trim().toLowerCase(Locale.ROOT);
            switch (normalized) {
                case "user":
                case "users":
                    return USER;
                case "item":
                case "items":
                    return ITEM;
                case "feedback":
                case "feedbacks":
                    return FEEDBACK;
                default:
                    throw new IllegalArgumentException("unsupported Gorse entity: " + value);
            }
        }

        static EntityType infer(String topic) {
            String normalized = topic.toLowerCase(Locale.ROOT);
            if (normalized.contains("feedback")) {
                return FEEDBACK;
            }
            if (normalized.contains("user")) {
                return USER;
            }
            if (normalized.contains("item")) {
                return ITEM;
            }
            throw new IllegalArgumentException(
                    "cannot infer Gorse entity from topic " + topic
                            + "; set " + ENTITY_CONFIG + " or topic." + topic + ".entity");
        }
    }
}
