package tech.ecoelho.btgpactual.ordemns.factory;

import tech.ecoelho.btgpactual.ordemns.dto.OrderCreatedEvent;
import tech.ecoelho.btgpactual.ordemns.dto.OrderItensEvent;

import java.math.BigDecimal;
import java.util.List;

public class OrderCreatedEventFactory {

    public static OrderCreatedEvent buildWithOneItem() {

        var itens = new OrderItensEvent("notebook", 1, BigDecimal.valueOf(20.50));
        var event = new OrderCreatedEvent(1L, 2L, List.of(itens));

        return event;
    }

    public static OrderCreatedEvent buildWithTwoItens() {

        var item1 = new OrderItensEvent("notebook", 1, BigDecimal.valueOf(20.50));
        var item2 = new OrderItensEvent("mouse", 1, BigDecimal.valueOf(35.25));

        var event = new OrderCreatedEvent(1L, 2L, List.of(item1, item2));

        return event;
    }
}