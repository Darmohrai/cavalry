package org.templar.cavalry.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.templar.cavalry.config.RabbitMQConfig;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendStationCreatedEvent(UUID stationId) {
        String message = String.format("Нову зарядну станцію з ID %d було успішно створено та додано до мережі.", stationId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, message);
        log.info("Відправлено подію створення станції в RabbitMQ: {}", message);
    }
}