package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    @MockBean
    BeerOrderService beerOrderService;

    @Autowired
    MockMvc mockMvc;

    //    private BeerDto validBeer;
    private BeerOrderDto beerOrder;
    private BeerOrderPagedList beerOrderPagedList;

    @BeforeEach
    void setUp() {
        BeerDto validBeer = BeerControllerTest.createBeerDto("Beer1");

        beerOrder = BeerOrderDto.builder()
                .id(UUID.randomUUID())
                .customerRef("1234")
                .beerOrderLines(
                        Collections.singletonList(
                                BeerOrderLineDto
                                        .builder()
                                        .beerId(validBeer.getId())
                                        .build()
                        ))
                .build();

        beerOrderPagedList = new BeerOrderPagedList(Collections.singletonList(beerOrder),
                PageRequest.of(1, 1), 1L);
    }

    @AfterEach
    void tearDown() {
        reset(beerOrderService);
    }

    @Test
    void listOrders() throws Exception {
        given(beerOrderService.listOrders(any(), any())).willReturn(beerOrderPagedList);

//        mockMvc.perform(get("/api/v1/customers/85d4506-e7dd-446e-a092-5f30b98e7b26/orders"))
        mockMvc.perform(get("/api/v1/customers/{customerId}/orders", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    void getOrder() throws Exception {
        given(beerOrderService.getOrderById(any(), any())).willReturn(beerOrder);

//        mockMvc.perform(get("/api/v1/customers/85d4506-e7dd-446e-a092-5f30b98e7b26/orders/f25767d9-342a-48ac-a788-0a7a38ae6fb3"))
        mockMvc.perform(get("/api/v1/customers/{customerId}/orders/{orderId}", randomId(), randomId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    static String randomId(){
        return UUID.randomUUID().toString();
    }
}