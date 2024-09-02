package tech.ecoelho.btgpactual.ordemns.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "tb_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItens {

    private String product;
    private Integer quantity;
    private BigDecimal price;

}
