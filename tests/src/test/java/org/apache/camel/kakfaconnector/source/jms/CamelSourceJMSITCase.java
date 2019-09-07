package org.apache.camel.kakfaconnector.source.jms;

import com.github.dockerjava.api.model.Bind;
import org.apache.camel.kakfaconnector.ConnectorPropertyProducer;
import org.apache.camel.kakfaconnector.KafkaConnectRunner;
import org.apache.camel.kakfaconnector.TestCommon;
import org.apache.camel.kakfaconnector.TestMessageConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.fail;

/**
 * A simple test case that checks whether the timer produces the expected number of
 * messages
 */
public class CamelSourceJMSITCase {
    @Rule
    public KafkaContainer kafka = new KafkaContainer().withEmbeddedZookeeper();

    @Rule
    public GenericContainer artemis = new GenericContainer(
            new ImageFromDockerfile()
            .withFileFromClasspath("Dockerfile",
                    "org/apache/camel/kakfaconnector/source/jms/artemis/Dockerfile"))
        .withExposedPorts(new Integer[]{1883, 5672, 61616});

    private int received = 0;
    private final int expect = 10;
    private KafkaConnectRunner kafkaConnectRunner;

    @Before
    public void setUp() {
        kafkaConnectRunner =  new KafkaConnectRunner(kafka.getBootstrapServers());

        ConnectorPropertyProducer testProperties = new CamelJMSPropertyProducer(1,
                TestCommon.DEFAULT_TEST_TOPIC, "ckc.queue", artemis.getMappedPort(61616));

        kafkaConnectRunner.getConnectorPropertyProducers().add(testProperties);

        System.out.println("Kafka bootstrap server running at address " + kafka.getBootstrapServers());
    }

    private boolean checkRecord(ConsumerRecord<String, String> record) {
        received++;

        if (received == expect) {
            return false;
        }

        return true;
    }

    @Test
    public void testLaunchConnector() {
        try {
            ExecutorService service = Executors.newCachedThreadPool();
            service.submit(() -> kafkaConnectRunner.run());

            System.out.println("Creating the consumer ...");
            TestMessageConsumer<String,String> testMessageConsumer = new TestMessageConsumer<>(kafka.getBootstrapServers());
            testMessageConsumer.consume(TestCommon.DEFAULT_TEST_TOPIC, this::checkRecord);
            System.out.println("Created the consumer ...");

            kafkaConnectRunner.stop();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            fail();
        }
    }
}
