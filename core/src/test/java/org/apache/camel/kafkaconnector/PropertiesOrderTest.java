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
package org.apache.camel.kafkaconnector;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

public class PropertiesOrderTest {

    @Test
    public void testOneOrder() throws JsonProcessingException, InterruptedException {
        Map<String, String> props = new HashMap<>();
        props.put("camel.source.url", "seda://test");
        props.put("camel.source.kafka.topic", "mytopic");
        props.put("camel.component.seda.defaultQueueFactory", "#class:org.apache.camel.kafkaconnector.test.TestBlockingQueueFactory");
        props.put("camel.component.seda.defaultQueueFactory.counter", "1");

        CamelSinkTask camelsinkTask = new CamelSinkTask();
        camelsinkTask.start(props);
        camelsinkTask.stop();
    }

    @Test
    public void testOppositOrder() throws JsonProcessingException, InterruptedException {
        Map<String, String> props = new HashMap<>();
        props.put("camel.source.url", "seda://test");
        props.put("camel.source.kafka.topic", "mytopic");
        props.put("camel.component.seda.defaultQueueFactory.counter", "1");
        props.put("camel.component.seda.defaultQueueFactory", "#class:org.apache.camel.kafkaconnector.test.TestBlockingQueueFactory");

        CamelSinkTask camelsinkTask = new CamelSinkTask();
        camelsinkTask.start(props);
        camelsinkTask.stop();
    }
}
