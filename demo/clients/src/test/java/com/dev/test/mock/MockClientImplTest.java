package com.dev.test.mock;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.dev.test.mock.response.MockClientResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class MockClientImplTest {
  final private static String BASE_URL = "http://localhost:3001/product/%s";
  final private static String SIMILAR_ID_URL = BASE_URL.concat("/similarids");
  final private static String PRODUCT_ID = "1";
  @Mock
  private RestTemplate restTemplate;
  
  @InjectMocks
  private MockClientImpl mockClient;
  
  @Nested
  class FindProductDetail{
    
    @Test
    void when_callFindProductDetail_then_returnResponse(){
      final String url = String.format(BASE_URL, PRODUCT_ID);
      final MockClientResponse expectedResponse = new MockClientResponse();
      expectedResponse.setId("10");
      expectedResponse.setName("name");
      expectedResponse.setPrice(100);
      expectedResponse.setAvailability(true);
      when(restTemplate.getForObject(url, MockClientResponse.class)).thenReturn(expectedResponse);
      final MockClientResponse result = mockClient.findProductDetail(PRODUCT_ID);
      
      assertThat(result).isEqualTo(expectedResponse);
      verify(restTemplate).getForObject(url, MockClientResponse.class);
    }
  }

  @Nested
  class FindSimilarIds{

    @Test
    void when_callFindSimilarIds_then_returnListOfIds(){
      final String url = String.format(SIMILAR_ID_URL, PRODUCT_ID);
      final List<Integer> expectedResponse = List.of(1,2,3,4);
      when(restTemplate.getForObject(url, List.class)).thenReturn(expectedResponse);
      final List<Integer> result = mockClient.findSimilarIds(PRODUCT_ID);

      assertThat(result).isEqualTo(expectedResponse);
      verify(restTemplate).getForObject(url, List.class);
    }
  }
  
}