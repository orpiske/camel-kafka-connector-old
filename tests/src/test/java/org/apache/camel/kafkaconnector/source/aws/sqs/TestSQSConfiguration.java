/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.camel.kafkaconnector.source.aws.sqs;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.apache.camel.component.aws.sqs.SqsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQSConfiguration extends SqsConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(TestSQSConfiguration.class);

    @Override
    public AmazonSQS getAmazonSQSClient() {
        LOG.debug("Creating a new SQS custom SQS client");
        AmazonSQSClientBuilder clientBuilder = AmazonSQSClientBuilder
                .standard();

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTP);

        clientBuilder
                .withClientConfiguration(clientConfiguration)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(getAmazonAWSHost(), getRegion()))
                .withCredentials(new TestAWSCredentialsProvider());

        return clientBuilder.build();
    }
}
