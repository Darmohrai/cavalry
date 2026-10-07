package org.templar.notificator.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StationEventListener {

    @Bean
    public Queue stationNotificationsQueue() {
        return new Queue("ev.station.notifications", true);
    }

    @RabbitListener(queues = "ev.station.notifications")
    public void handleStationEvent(String message) {
        log.info("🔔 [NOTIFICATION-SERVICE] Отримано повідомлення з брокера: {}", message);
        log.info("🔔 [NOTIFICATION-SERVICE] Імітація відправки Email адміністратору мережі...");
    }
}
