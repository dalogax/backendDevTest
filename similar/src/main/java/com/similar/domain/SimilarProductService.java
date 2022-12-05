package com.similar.domain;

import java.util.List;

public interface SimilarProductService {
    List<String> getSimilarProductBy(Long productId);
}
