package com.dev.test.mock;

import com.dev.test.mock.response.MockClientResponse;

import java.util.List;

public interface MockClient {
  MockClientResponse findProductDetail(String productId);

  List<Integer> findSimilarIds(String productId);
}
