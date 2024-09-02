package tech.ecoelho.btgpactual.ordemns.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmMqConfig {

    public static final String ORDER_CREATED_QUEUE = "btg-pactual-order-created";
}
