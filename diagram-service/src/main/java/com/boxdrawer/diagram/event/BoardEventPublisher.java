package com.boxdrawer.diagram.event;

import com.boxdrawer.diagram.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Async communication: publishes events to RabbitMQ, notification-service consumes them. */
@Component
public class BoardEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public BoardEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishBoardShared(BoardSharedEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE, event);
    }
}
