package tech.ecoelho.btgpactual.ordemns.listener;


import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import tech.ecoelho.btgpactual.ordemns.dto.OrderCreatedEvent;
import tech.ecoelho.btgpactual.ordemns.service.OrderService;

import static tech.ecoelho.btgpactual.ordemns.config.RabbitmMqConfig.ORDER_CREATED_QUEUE;

@Component
public class OrderCreatedListener {

    private final OrderService orderService;

    public OrderCreatedListener(OrderService orderService) {
        this.orderService = orderService;
    }

    private final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    @RabbitListener(queues = ORDER_CREATED_QUEUE)
    public void listen(Message<OrderCreatedEvent> message){
        log.info("Message consumed: {}", message);


        orderService.save(message.getPayload()); // corpo da mensagem
    }
}
