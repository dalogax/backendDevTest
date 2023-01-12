package integration.infrastructure.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.inditex.myapp.MyAppApplication;
import com.inditex.myapp.application.model.ProductDetailResponse;
import com.inditex.myapp.infrastructure.controller.model.ProductDetailDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@SpringBootTest(classes = MyAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class ProductApiImplIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SIMILAR_PRODUCTS_URL = "/product/{productId}/similar";
    private static final String EXISTING_API_SIMILAR_PRODUCTS_URL = "/product/{productId}/similarids";

    private static final String EXISTING_API_PRODUCT_URL = "/product/{productId}";

    @Test
    void getProductSimilarIntegrationTest() throws Exception {
        String existingProduct = "EXISTING_PRODUCT";
        String relatedProduct = "RELATED_PRODUCT";

        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        productDetailResponse.setId(relatedProduct);
        productDetailResponse.setName(relatedProduct);
        productDetailResponse.setPrice(BigDecimal.valueOf(0));
        productDetailResponse.setAvailability(false);

        URI existingApiSimilarProductUri = UriComponentsBuilder.fromUriString(EXISTING_API_SIMILAR_PRODUCTS_URL).build(existingProduct);
        WireMock.stubFor(WireMock.get(urlEqualTo(existingApiSimilarProductUri.getPath()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(Collections.singletonList(relatedProduct)))
                )
        );

        URI existingApiProductUri = UriComponentsBuilder.fromUriString(EXISTING_API_PRODUCT_URL).build(relatedProduct);
        WireMock.stubFor(WireMock.get(urlEqualTo(existingApiProductUri.getPath()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(productDetailResponse))
                )
        );

        URI myAppUri = UriComponentsBuilder.fromUriString(SIMILAR_PRODUCTS_URL).build(existingProduct);
        MvcResult result = mockMvc.perform(get(myAppUri))
                .andExpect(status().isOk())
                .andReturn();

        Set<ProductDetailDto> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(Set.class, ProductDetailDto.class)
        );

        Assertions.assertNotNull(response);

        for (ProductDetailDto productDetailDto : response) {
            Assertions.assertNotNull(productDetailDto);
            Assertions.assertNotNull(productDetailDto.getId());
            Assertions.assertNotNull(productDetailDto.getName());
            Assertions.assertNotNull(productDetailDto.getPrice());
            Assertions.assertNotNull(productDetailDto.getAvailability());
        }
    }

    @Test
    void getProductSimilarButProductDoesNotExistIntegrationTest() throws Exception {
        String existingProduct = "NOT_EXISTING_PRODUCT";

        URI existingApiSimilarProductUri = UriComponentsBuilder.fromUriString(EXISTING_API_SIMILAR_PRODUCTS_URL).build(existingProduct);
        WireMock.stubFor(WireMock.get(urlEqualTo(existingApiSimilarProductUri.getPath()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.NOT_FOUND.value())
                )
        );

        URI myAppUri = UriComponentsBuilder.fromUriString(SIMILAR_PRODUCTS_URL).build(existingProduct);
        mockMvc.perform(get(myAppUri))
                .andExpect(status().isNotFound())
                .andReturn();

    }

    @Test
    void getProductSimilarButSomethingHappenedIntegrationTest() throws Exception {
        String existingProduct = "SOMETHING_HAPPENED";

        URI existingApiSimilarProductUri = UriComponentsBuilder.fromUriString(EXISTING_API_SIMILAR_PRODUCTS_URL).build(existingProduct);
        WireMock.stubFor(WireMock.get(urlEqualTo(existingApiSimilarProductUri.getPath()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                )
        );

        URI myAppUri = UriComponentsBuilder.fromUriString(SIMILAR_PRODUCTS_URL).build(existingProduct);
        mockMvc.perform(get(myAppUri))
                .andExpect(status().isBadGateway())
                .andReturn();
    }

    @Test
    void getProductSimilarButSimilarProductNotFoundIntegrationTest() throws Exception {
        String existingProduct = "EXISTING_PRODUCT";
        String relatedProduct = "RELATED_PRODUCT";

        URI existingApiSimilarProductUri = UriComponentsBuilder.fromUriString(EXISTING_API_SIMILAR_PRODUCTS_URL).build(existingProduct);
        WireMock.stubFor(WireMock.get(urlEqualTo(existingApiSimilarProductUri.getPath()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(Collections.singletonList(relatedProduct)))
                )
        );

        URI existingApiProductUri = UriComponentsBuilder.fromUriString(EXISTING_API_PRODUCT_URL).build(relatedProduct);
        WireMock.stubFor(WireMock.get(urlEqualTo(existingApiProductUri.getPath()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.NOT_FOUND.value())
                )
        );

        URI myAppUri = UriComponentsBuilder.fromUriString(SIMILAR_PRODUCTS_URL).build(existingProduct);
        mockMvc.perform(get(myAppUri))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void getProductSimilarButSomethingHappenedWithSimilarProductIntegrationTest() throws Exception {
        String existingProduct = "EXISTING_PRODUCT";
        String relatedProduct = "RELATED_PRODUCT";

        URI existingApiSimilarProductUri = UriComponentsBuilder.fromUriString(EXISTING_API_SIMILAR_PRODUCTS_URL).build(existingProduct);
        WireMock.stubFor(WireMock.get(urlEqualTo(existingApiSimilarProductUri.getPath()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(Collections.singletonList(relatedProduct)))
                )
        );

        URI existingApiProductUri = UriComponentsBuilder.fromUriString(EXISTING_API_PRODUCT_URL).build(relatedProduct);
        WireMock.stubFor(WireMock.get(urlEqualTo(existingApiProductUri.getPath()))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                )
        );

        URI myAppUri = UriComponentsBuilder.fromUriString(SIMILAR_PRODUCTS_URL).build(existingProduct);
        mockMvc.perform(get(myAppUri))
                .andExpect(status().isBadGateway())
                .andReturn();
    }
}

