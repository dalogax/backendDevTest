package com.similar.domain.impl;

import com.similar.domain.SimilarProductService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SimilarProductServiceImpl implements SimilarProductService {

    @Override
    public List<String> getSimilarProductBy(Long productId) {
        return Collections.emptyList();
    }
}
