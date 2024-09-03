package tech.ecoelho.btgpactual.ordemns.service;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Service;
import tech.ecoelho.btgpactual.ordemns.domain.entity.OrderEntity;
import tech.ecoelho.btgpactual.ordemns.domain.entity.OrderItem;
import tech.ecoelho.btgpactual.ordemns.dto.OrderCreatedEvent;
import tech.ecoelho.btgpactual.ordemns.dto.OrderResponse;
import tech.ecoelho.btgpactual.ordemns.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class OrderService {

    private final MongoTemplate mongoTemplate;
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository, MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void save(OrderCreatedEvent event){
        var entity = new OrderEntity();
        entity.setOrderId(event.codigoPedido());
        entity.setCustomerId(event.codigoCliente());
        entity.setItems(getOrderItens(event));
        entity.setTotal(getTotal(event));

        orderRepository.save(entity);
    }

    public Page<OrderResponse> findAllByCustomerId(Long customerId, PageRequest pageRequest) {
        var orders = orderRepository.findAllByCustomerId(customerId, pageRequest);

        return orders.map(OrderResponse::fromEntity);
    }

    public BigDecimal findTotalOnOrdersByCustomerId(Long customerId) {
        var aggregations = newAggregation(
                match(Criteria.where("customerId").is(customerId)),
                group().sum("total").as("total")
        );

        var response = mongoTemplate.aggregate(aggregations, "tb_orders", Document.class);

        return new BigDecimal(response.getUniqueMappedResult().get("total").toString());
    }


    private BigDecimal getTotal(OrderCreatedEvent event){
        return event.itens()
                .stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity()))) // multiplica o preço pela quantidade
                .reduce(BigDecimal::add) // reduz para um valor só
                .orElse(BigDecimal.ZERO); // caso nao tenho exiba 0
    }

    private List<OrderItem> getOrderItens(OrderCreatedEvent event){
        return event.itens()
                .stream()
                .map(i -> new OrderItem(i.product(), i.quantity(), i.price()))
                .collect(Collectors.toList());
    }
}
