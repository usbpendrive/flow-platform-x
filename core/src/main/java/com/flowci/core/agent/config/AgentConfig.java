/*
 * Copyright 2018 flow.ci
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flowci.core.agent.config;

import com.flowci.core.config.ConfigProperties;
import com.flowci.domain.Settings;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yang
 */
@Configuration
public class AgentConfig {

    @Autowired
    private ConfigProperties.Zookeeper zkProperties;

    @Autowired
    private ConfigProperties.Job jobProperties;

    @Autowired
    private RabbitProperties rabbitProperties;

    @Autowired
    private FanoutExchange logsExchange;

    @Bean("baseSettings")
    public Settings baseSettings() {
        Settings.Zookeeper zk = new Settings.Zookeeper();
        zk.setRoot(zkProperties.getAgentRoot());
        zk.setHost(zkProperties.getHost());

        Settings.RabbitMQ mq = new Settings.RabbitMQ();
        mq.setHost(rabbitProperties.getHost());
        mq.setPort(rabbitProperties.getPort());
        mq.setPassword(rabbitProperties.getPassword());
        mq.setUsername(rabbitProperties.getUsername());

        Settings settings = new Settings();
        settings.setCallbackQueueName(jobProperties.getCallbackQueueName());
        settings.setZookeeper(zk);
        settings.setQueue(mq);
        settings.setLogsExchangeName(logsExchange.getName());

        return settings;
    }
}
