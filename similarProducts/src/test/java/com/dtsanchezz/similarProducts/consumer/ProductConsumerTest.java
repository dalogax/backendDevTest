package com.dtsanchezz.similarProducts.consumer;

import com.dtsanchezz.similarProducts.consumer.api.ProductConsumer;
import com.dtsanchezz.similarProducts.exception.custom.ConsumerException;
import com.dtsanchezz.similarProducts.exception.custom.NotFoundException;
import com.dtsanchezz.similarProducts.model.ProductDetail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductConsumerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    ProductConsumer underTest;

    private static final String PRODUCT_ID_TEST = "1";
    private static final String TEST_URI = "test-uri:8080/resource";
    private String[] similarIdList;
    private ProductDetail productDetail;

    @BeforeEach
    public void setUp() {
        this.similarIdList = new String[]{"1", "2", "3", "4"};
        this.productDetail = new ProductDetail("1", "Dress", 19.99, true);
    }

    // CONSUMER
    @Test
    public void consumerProductIdNullError() {

        Assertions.assertThrows(ConsumerException.class,
                () -> this.underTest.consume(any(), any(Class.class), (String) null)
        );
    }

    @Test
    public void consumerReturnsNotFound() {

        when(this.restTemplate.getForEntity(eq(TEST_URI), any(), any(String.class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        Assertions.assertThrows(NotFoundException.class,
                () -> this.underTest.consume(TEST_URI, Object.class, PRODUCT_ID_TEST)
        );
    }

    @Test
    public void consumerReturnsNullBody() {
        when(this.restTemplate.getForEntity(eq(TEST_URI), any(), any(String.class)))
                .thenReturn(ResponseEntity.ok(null));

        Assertions.assertThrows(ConsumerException.class,
                () -> this.underTest.consume(TEST_URI, Object.class, PRODUCT_ID_TEST)
        );
    }

    @Test
    public void consumerReturnsNotOkStatus() {
        when(this.restTemplate.getForEntity(eq(TEST_URI), any(), any(String.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).body(""));

        Assertions.assertThrows(ConsumerException.class,
                () -> this.underTest.consume(TEST_URI, Object.class, PRODUCT_ID_TEST)
        );
    }


    // GET SIMILAR ID
    @Test
    public void getSimilarIdsReturnsEmptyList() {
        when(this.restTemplate.getForEntity(any(String.class), any(), eq(PRODUCT_ID_TEST)))
                .thenReturn(ResponseEntity.ok().body(new String[]{}));

        final List<String> similarIds = this.underTest.getSimilarIds(PRODUCT_ID_TEST);
        Assertions.assertNotNull(similarIds);
        Assertions.assertTrue(similarIds.isEmpty());
    }

    @Test
    public void getSimilarIdsReturnCorrect() {
        when(this.restTemplate.getForEntity(any(String.class), any(), eq(PRODUCT_ID_TEST)))
                .thenReturn(ResponseEntity.ok().body(this.similarIdList));

        final List<String> similarIds = this.underTest.getSimilarIds(PRODUCT_ID_TEST);
        Assertions.assertNotNull(similarIds);
        Assertions.assertEquals(similarIds, Arrays.asList(this.similarIdList));
    }

    // GET PRODUCT DETAIL
    @Test
    public void getProductDetailReturnsCorrect() {
        when(this.restTemplate.getForEntity(any(String.class), any(), eq(PRODUCT_ID_TEST)))
                .thenReturn(ResponseEntity.ok().body(this.productDetail));

        final ProductDetail productDetail = this.underTest.getProductDetail(PRODUCT_ID_TEST);

        Assertions.assertNotNull(productDetail);
        Assertions.assertEquals(productDetail.getId(), "1");
        Assertions.assertEquals(productDetail.getName(), "Dress");
        Assertions.assertEquals(productDetail.getPrice(), 19.99);
        Assertions.assertEquals(productDetail.getAvailability(), Boolean.TRUE);
    }
}