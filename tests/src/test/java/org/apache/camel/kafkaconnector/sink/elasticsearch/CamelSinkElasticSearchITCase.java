/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.camel.kafkaconnector.sink.elasticsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.camel.kafkaconnector.ContainerUtil;
import org.apache.camel.kafkaconnector.KafkaConnectRunner;
import org.apache.camel.kafkaconnector.TestCommon;
import org.apache.camel.kafkaconnector.clients.kafka.KafkaClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import static org.junit.Assert.fail;

public class CamelSinkElasticSearchITCase {
    private static final Logger LOG = LoggerFactory.getLogger(CamelElasticSearchPropertyFactory.class);

    private static final int ELASTIC_SEARCH_PORT = 9200;

    @Rule
    public KafkaContainer kafka = new KafkaContainer().withEmbeddedZookeeper();

    @Rule
    public ElasticsearchContainer elasticsearch = new ElasticsearchContainer();

    private KafkaConnectRunner kafkaConnectRunner;

    private final int expect = 1;


    @Before
    public void setUp() {
        ContainerUtil.waitForInitialization(kafka);
        LOG.info("Kafka bootstrap server running at address {}", kafka.getBootstrapServers());

        ContainerUtil.waitForHttpInitialization(elasticsearch, elasticsearch.getMappedPort(ELASTIC_SEARCH_PORT));

        final String elasticSearchInstance = elasticsearch
                .getHttpHostAddress();

        LOG.info("ElasticSearch instance running at {}", elasticSearchInstance);

        CamelElasticSearchPropertyFactory testProperties = new CamelElasticSearchPropertyFactory(1,
                TestCommon.DEFAULT_TEST_TOPIC, TestCommon.DEFAULT_ELASTICSEARCH_CLUSTER, elasticSearchInstance);

        kafkaConnectRunner = new KafkaConnectRunner(kafka.getBootstrapServers());
        kafkaConnectRunner.getConnectorPropertyProducers().add(testProperties);
    }

    private void putRecords() {

        KafkaClient<String, String> kafkaClient = new KafkaClient<>(kafka.getBootstrapServers());

        for (int i = 0; i < expect; i++) {
            try {
                kafkaClient.produce(TestCommon.DEFAULT_TEST_TOPIC, "test");
            } catch (ExecutionException e) {
                LOG.error("Unable to produce messages: {}", e.getMessage(), e);
            } catch (InterruptedException e) {
                break;
            }
        }

    }

    @Test(timeout = 90000)
    public void testBasicSendReceive() {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            ExecutorService service = Executors.newCachedThreadPool();
            service.submit(() -> kafkaConnectRunner.run(latch));

            latch.await();
            putRecords();

            LOG.debug("Created the consumer ... About to receive messages");
        } catch (Exception e) {
            LOG.error("HTTP test failed: {}", e.getMessage(), e);
            fail(e.getMessage());
        } finally {
            kafkaConnectRunner.stop();
        }
    }
}
