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

package com.flowci.core.job.service;

import com.flowci.domain.LogItem;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author yang
 */
@Log4j2
@Service
public class LoggingServiceImpl implements LoggingService {

    @Autowired
    private String topicForLogs;

    @Autowired
    private Queue logsQueue;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    @RabbitListener(queues = "#{logsQueue.getName()}", containerFactory = "logsContainerFactory")
    public void processLogItem(Message message) {
        String logItemAsString = new String(message.getBody());
        log.debug(logItemAsString);

        // find cmd id from log item string
        int firstIndex = logItemAsString.indexOf(LogItem.SPLITTER);
        String cmdId = logItemAsString.substring(0, firstIndex);
        String destination = topicForLogs + "/" + cmdId;

        // send string message without cmd id
        String body = logItemAsString.substring(firstIndex + 1);
        simpMessagingTemplate.convertAndSend(destination, body);
    }
}
