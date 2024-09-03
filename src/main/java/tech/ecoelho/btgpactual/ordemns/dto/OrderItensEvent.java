package tech.ecoelho.btgpactual.ordemns.dto;

import java.math.BigDecimal;

public record OrderItensEvent(String product, Integer quantity, BigDecimal price) {
}
