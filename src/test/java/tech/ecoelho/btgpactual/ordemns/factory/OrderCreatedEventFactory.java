package tech.ecoelho.btgpactual.ordemns.factory;

import tech.ecoelho.btgpactual.ordemns.dto.OrderCreatedEvent;
import tech.ecoelho.btgpactual.ordemns.dto.OrderItensEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderCreatedEventFactory {
    public static OrderCreatedEvent build(){
        var itens = new OrderItensEvent("notebook", 10, BigDecimal.valueOf(20.50));
        var event = new OrderCreatedEvent(1L, 2L, List.of(itens));

        return event;
    }

    public static OrderCreatedEvent buildWithTwoItens(){
        var item1 = new OrderItensEvent("notebook", 2, BigDecimal.valueOf(20.50));
        var item2 = new OrderItensEvent("pc", 1, BigDecimal.valueOf(10.10));

        var event = new OrderCreatedEvent(1L, 2L, List.of(item1, item2));

        return event;
    }

}
