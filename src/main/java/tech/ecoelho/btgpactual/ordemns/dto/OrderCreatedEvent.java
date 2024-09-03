package tech.ecoelho.btgpactual.ordemns.dto;

import java.util.List;

public record  OrderCreatedEvent(Long codigoPedido, Long codigoCliente, List<OrderItensEvent> itens) {
}
