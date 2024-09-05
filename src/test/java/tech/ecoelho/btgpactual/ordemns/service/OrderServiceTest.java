package tech.ecoelho.btgpactual.ordemns.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import tech.ecoelho.btgpactual.ordemns.domain.entity.OrderEntity;
import tech.ecoelho.btgpactual.ordemns.factory.OrderCreatedEventFactory;
import tech.ecoelho.btgpactual.ordemns.repository.OrderRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderService orderService;

    @Captor
    ArgumentCaptor<OrderEntity> orderEntityCaptor;

    @Nested
    class save {

        @Test
        void shouldCallRepositorySave() {
            var event = OrderCreatedEventFactory.buildWithOneItem();

            // ACT
            orderService.save(event);

            // ASSERT
            verify(orderRepository, times(1)).save(any());
        }

        @Test
        void shouldMapEventToEntityWithSuccess() {
            var event = OrderCreatedEventFactory.buildWithOneItem();

            // ACT
            orderService.save(event);

            // ASSERT
            verify(orderRepository, times(1)).save(orderEntityCaptor.capture());
            var entity = orderEntityCaptor.getValue();

            assertEquals(event.codigoPedido(), entity.getOrderId());
            assertEquals(event.codigoCliente(), entity.getCustomerId());
            assertNotNull(entity.getTotal());
            assertEquals(event.itens().get(0).product(), entity.getItems().get(0).getProduct());
            assertEquals(event.itens().get(0).quantity(), entity.getItems().get(0).getQuantity());
            assertEquals(event.itens().get(0).price(), entity.getItems().get(0).getPrice());

        }

        @Test
        void shouldCalculateOrderTotalWithSuccess() {
            var event = OrderCreatedEventFactory.buildWithTwoItens();
            var totalItem1 = event.itens().get(0).price().multiply(BigDecimal.valueOf(event.itens().get(0).quantity()));
            var totalItem2 = event.itens().get(1).price().multiply(BigDecimal.valueOf(event.itens().get(1).quantity()));
            var orderTotal = totalItem1.add(totalItem2);

            // ACT
            orderService.save(event);

            // ASSERT
            verify(orderRepository, times(1)).save(orderEntityCaptor.capture());
            var entity = orderEntityCaptor.getValue();

            assertNotNull(entity.getTotal());
            assertEquals(orderTotal, entity.getTotal());
        }

    }
}