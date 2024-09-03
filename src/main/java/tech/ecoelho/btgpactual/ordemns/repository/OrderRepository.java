package tech.ecoelho.btgpactual.ordemns.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.ecoelho.btgpactual.ordemns.domain.entity.OrderEntity;
import tech.ecoelho.btgpactual.ordemns.dto.OrderCreatedEvent;
import tech.ecoelho.btgpactual.ordemns.dto.OrderResponse;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<OrderEntity, Long> {

    Page<OrderEntity> findAllByCustomerId(Long customerId, PageRequest pageRequest);
}