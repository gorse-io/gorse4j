package io.gorse.gorse4j.connect;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GorseSinkConnector extends SinkConnector {

    static final String VERSION = "0.5.1";

    private Map<String, String> props;

    @Override
    public void start(Map<String, String> props) {
        GorseSinkConfig.configDef().parse(props);
        this.props = new HashMap<>(props);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return GorseSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> configs = new ArrayList<>(maxTasks);
        for (int i = 0; i < maxTasks; i++) {
            configs.add(new HashMap<>(props));
        }
        return configs;
    }

    @Override
    public void stop() {
    }

    @Override
    public ConfigDef config() {
        return GorseSinkConfig.configDef();
    }

    @Override
    public String version() {
        return VERSION;
    }
}
