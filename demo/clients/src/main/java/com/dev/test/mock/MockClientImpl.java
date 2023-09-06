package com.dev.test.mock;

import com.dev.test.mock.response.MockClientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MockClientImpl implements MockClient {
  private final RestTemplate restTemplate;
  
  private final static String BASE_URL = "http://localhost:3001/product/%s";
  private final static String SIMILAR_ID_URL = BASE_URL.concat("/similarids");
  
  @Override
  public MockClientResponse findProductDetail(String productId){
    final String url = String.format(BASE_URL, productId);
    log.info("Calling {}", url);
    return restTemplate.getForObject(url, MockClientResponse.class);
  }
  @Override
  public List<Integer> findSimilarIds(String productId){
    final String url = String.format(SIMILAR_ID_URL, productId);
    log.info("Calling {}", url);
    return restTemplate.getForObject(url, List.class);
  }
}
