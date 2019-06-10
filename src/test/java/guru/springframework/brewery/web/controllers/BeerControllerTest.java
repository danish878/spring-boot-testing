package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerPagedList;
import guru.springframework.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@ExtendWith(MockitoExtension.class)
@WebMvcTest(BeerController.class)
class BeerControllerTest {

//    @Mock
    @MockBean
    BeerService beerService;

//    @InjectMocks
//    BeerController beerController;

    @Autowired
    private MockMvc mockMvc;

    private BeerDto validBeer;

    @BeforeEach
    void setUp() {
        System.out.println("setUp - OUTER class");
        validBeer = createBeerDto("Beer1");

//        mockMvc = MockMvcBuilders.standaloneSetup(beerController)
//                                .setMessageConverters(jackson2HttpMessageConverter()).build();
    }

    @AfterEach
    void tearDown() {
        reset(beerService);
    }

    @Test
    void getBeerById() throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

        given(beerService.findBeerById(any())).willReturn(validBeer);
        MvcResult result = mockMvc.perform(get("/api/v1/beer/" + validBeer.getId()))
                                    .andExpect(status().isOk())
                                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                    .andExpect(jsonPath("$.id", is(validBeer.getId().toString())))
                                    .andExpect(jsonPath("$.beerName", is("Beer1")))
                                    .andExpect(jsonPath("$.createdDate",
                                            is(dateTimeFormatter.format(validBeer.getCreatedDate()))))
                                    .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @DisplayName("List Ops - ")
    @Nested
    class TestListOperations {

        @Captor
        ArgumentCaptor<String> beerNameCaptor;

        @Captor
        ArgumentCaptor<BeerStyleEnum> beerStyleEnumCaptor;

        @Captor
        ArgumentCaptor<PageRequest> pageRequestCaptor;

        BeerPagedList beerPagedList;

        @BeforeEach
        void setUp() {
            System.out.println("setUp - INNER class");
            List<BeerDto> beers = new ArrayList<>();
            beers.add(validBeer);
            beers.add(createBeerDto("Beer4"));

            beerPagedList = new BeerPagedList(beers, PageRequest.of(1, 1), 2L);

            given(beerService.listBeers(beerNameCaptor.capture(), beerStyleEnumCaptor.capture(),
                    pageRequestCaptor.capture())).willReturn(beerPagedList);
        }

        @DisplayName("Test list beers - no parameters")
        @Test
        void testListBeers() throws Exception {
            MvcResult result = mockMvc.perform(get("/api/v1/beer")
                                            .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                                        .andExpect(jsonPath("$.content", hasSize(2)))
                                        .andExpect(jsonPath("$.content[0].id", is(validBeer.getId().toString())))
                                        .andReturn();

            System.out.println(result.getResponse().getContentAsString());
        }
    }

//    private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//        objectMapper.registerModule(new JavaTimeModule());
//        return new MappingJackson2HttpMessageConverter(objectMapper);
//    }

    static BeerDto createBeerDto(String beerName){
        return BeerDto.builder().id(UUID.randomUUID())
                .version(1)
                .beerName(beerName)
                .upc(123123123122L)
                .beerStyle(BeerStyleEnum.PALE_ALE)
                .price(new BigDecimal("12.99"))
                .quantityOnHand(77)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build();
    }
}