package org.apache.camel.kakfaconnector.source.jms;

import org.apache.camel.kakfaconnector.ConnectorPropertyProducer;
import org.apache.kafka.connect.runtime.ConnectorConfig;

import java.util.Properties;

public class CamelJMSPropertyProducer implements ConnectorPropertyProducer {
    private final int tasksMax;
    private final String topic;
    private final String queue;

    private final int port;

    public CamelJMSPropertyProducer(int tasksMax, String topic, String queue, int port) {
        this.tasksMax = tasksMax;
        this.topic = topic;
        this.queue = queue;
        this.port = port;
    }

    @Override
    public Properties getProperties() {
        Properties connectorProps = new Properties();
        connectorProps.put(ConnectorConfig.NAME_CONFIG, "CamelJMSSourceConnector");
        connectorProps.put("tasks.max", String.valueOf(tasksMax));

        connectorProps.put(ConnectorConfig.CONNECTOR_CLASS_CONFIG, "org.apache.camel.kafkaconnector.CamelSourceConnector");
        connectorProps.put(ConnectorConfig.KEY_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.storage.StringConverter");
        connectorProps.put(ConnectorConfig.VALUE_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.storage.StringConverter");
        connectorProps.put("camel.source.url", "sjms2://queue:" + queue);
        connectorProps.put("camel.source.kafka.topic", topic);
        connectorProps.put("camel.component.sjms2.connection-factory", "#class:org.apache.activemq.ActiveMQConnectionFactory");
        connectorProps.put("camel.component.sjms2.connection-factory.brokerURL", "tcp://localhost:" + port);

        return connectorProps;
    }
}
