package tech.ecoelho.btgpactual.ordemns.dto;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;
import tech.ecoelho.btgpactual.ordemns.factory.OrderEntityFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderResponseTest {

    @Nested
    class fromEntity{

        @Test
        void shouldMapCorrectly(){
            // ARRANGE
            var input = OrderEntityFactory.build();

            // ACT
            var output = OrderResponse.fromEntity(input);

            // ASSERT
            assertEquals(input.getOrderId(), output.orderId());
            assertEquals(input.getCustomerId(), output.customerId());
            assertEquals(input.getTotal(), output.total());
        }

    }
}
