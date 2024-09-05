package tech.ecoelho.btgpactual.ordemns.service;

import org.bson.Document;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import tech.ecoelho.btgpactual.ordemns.domain.entity.OrderEntity;
import tech.ecoelho.btgpactual.ordemns.factory.OrderCreatedEventFactory;
import tech.ecoelho.btgpactual.ordemns.factory.OrderEntityFactory;
import tech.ecoelho.btgpactual.ordemns.repository.OrderRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
            // ARRANGE
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

    @Nested
    class findAllByCustomerId {

        @Test
        void shouldCallRepository(){
            // ARRANGE
            var customerId = 1L;
            var pageRequest = PageRequest.of(1, 10);
            doReturn(OrderEntityFactory.buildWithPage())
                    .when(orderRepository).findAllByCustomerId(customerId, pageRequest);

            // ACT
            var response = orderService.findAllByCustomerId(customerId, pageRequest);

            // ASSERT
            verify(orderRepository, times(1)).findAllByCustomerId(eq(customerId), eq(pageRequest));
        }

        @Test
        void shouldMapResponse(){
            // ARRANGE
            var customerId = 1L;
            var pageRequest = PageRequest.of(1, 10);
            var page = OrderEntityFactory.buildWithPage();
            doReturn(page).when(orderRepository).findAllByCustomerId(anyLong(), any());

            // ACT
            var response = orderService.findAllByCustomerId(customerId, pageRequest);

            // ASSERT
            assertEquals(page.getTotalPages(), response.getTotalPages());
            assertEquals(page.getTotalElements(), response.getTotalElements());
            assertEquals(page.getSize(), response.getSize());
            assertEquals(page.getNumber(), response.getNumber());

            assertEquals(page.getContent().get(0).getOrderId(), response.getContent().get(0).orderId());
            assertEquals(page.getContent().get(0).getCustomerId(), response.getContent().get(0).customerId());
            assertEquals(page.getContent().get(0).getTotal(), response.getContent().get(0).total());

        }
    }

    @Nested
    class findTotalOnOrdersByCustomerId {

        @Test
        void shouldCallMongoTemplate() {
            // ARRANGE
            var customerId = 1L;
            var totalExpected = BigDecimal.valueOf(1);
            var aggregationResult = mock(AggregationResults.class);
            doReturn(new Document("total",  totalExpected)).when(aggregationResult).getUniqueMappedResult();
            doReturn(aggregationResult).when(mongoTemplate).aggregate(any(Aggregation.class), eq("tb_orders"), eq(Document.class));

            // ACT
            orderService.findTotalOnOrdersByCustomerId(customerId);

            // ASSERT
            verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), eq("tb_orders"), eq(Document.class));
        }
    }
}