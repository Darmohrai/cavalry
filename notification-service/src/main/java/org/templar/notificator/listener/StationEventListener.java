package org.templar.notificator.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StationEventListener {

    @RabbitListener(queues = "ev.station.notifications")
    public void handleStationEvent(String message) {
        log.info("🔔 [NOTIFICATION-SERVICE] Отримано повідомлення з брокера: {}", message);
        log.info("🔔 [NOTIFICATION-SERVICE] Імітація відправки Email адміністратору мережі...");
    }
}
