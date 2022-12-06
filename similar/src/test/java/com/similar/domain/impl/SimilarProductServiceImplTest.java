package com.similar.domain.impl;

import com.similar.infrastructure.client.SimulatedClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SimilarProductServiceImplTest {

    @InjectMocks
    SimilarProductServiceImpl sut;

    @Mock
    SimulatedClient client;

    @Test
    void when_invoke_similar_products_with_id_get_list_products(){


        final var res = sut.getSimilarProductBy(1L);

        assertNotNull(res);
        assertNotNull(client);
        verify(client).getSimilar(any(), any());
    }
}