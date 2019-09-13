/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.camel.kafkaconnector.sink.jms.openwire;

import org.apache.camel.kafkaconnector.ConnectorPropertyFactory;
import org.apache.camel.kafkaconnector.TestCommon;
import org.apache.camel.kafkaconnector.clients.jms.JMSClient;
import org.apache.camel.kafkaconnector.sink.jms.CamelJMSPropertyFactory;
import org.apache.camel.kafkaconnector.sink.jms.CamelSinkJMSITCase;


/**
 * A simple test case that checks whether the timer produces the expected number of
 * messages
 */
public class CamelSinkOpenwireITCase extends CamelSinkJMSITCase {
    @Override
    protected ConnectorPropertyFactory getConnectorPropertyFactory() {
        return new CamelJMSPropertyFactory(1,
                TestCommon.DEFAULT_TEST_TOPIC, TestCommon.DEFAULT_JMS_QUEUE, artemis.getDefaultAcceptorEndpoint(),
                org.apache.activemq.ActiveMQConnectionFactory.class.getName());
    }

    @Override
    protected JMSClient getJmsClient() {
        return new JMSClient(org.apache.activemq.ActiveMQConnectionFactory::new,
                org.apache.activemq.command.ActiveMQQueue::new,
                artemis.getOpenwireEndpoint());
    }
}
