package com.similar.domain.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SimilarProductServiceImplTest {

    @InjectMocks
    SimilarProductServiceImpl sut;

    @Test
    void when_invoke_similar_products_with_id_get_list_products(){

        final var res = sut.getSimilarProductBy(1L);

        assertNotNull(res);
    }
}