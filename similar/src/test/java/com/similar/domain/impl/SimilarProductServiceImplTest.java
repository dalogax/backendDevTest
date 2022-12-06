package com.similar.domain.impl;

import com.similar.domain.model.Product;
import com.similar.infrastructure.client.SimulatedClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimilarProductServiceImplTest {

    @InjectMocks
    SimilarProductServiceImpl sut;

    @Mock
    SimulatedClient client;

    @Test
    void when_invoke_similar_products_with_id_get_list_products(){
        when(client.getSimilar(any(),any())).thenReturn(List.of(1L, 2L));
        when(client.getDetailOfProduct(anyLong())).thenReturn(new Product("1", "test", BigDecimal.TEN, true));

        final var res = sut.getSimilarProductBy(1L);

        assertNotNull(res);
        assertNotNull(client);
        assertEquals(2, res.size());
        verify(client).getSimilar(any(), any());
        verify(client, times(2)).getDetailOfProduct(any());
    }

    @Test
    void when_invoke_similar_products_with_id_get_list_products_and_filtering_by_availability(){
        when(client.getSimilar(any(),any())).thenReturn(List.of(1L, 2L, 3L));
        when(client.getDetailOfProduct(anyLong())).thenReturn(new Product("1", "test", BigDecimal.TEN, true));
        when(client.getDetailOfProduct(2L)).thenReturn(new Product("2", "test", BigDecimal.TEN, false));

        final var res = sut.getSimilarProductBy(1L);

        assertNotNull(res);
        assertNotNull(client);
        assertEquals(2, res.size());
        verify(client).getSimilar(any(), any());
        verify(client, times(3)).getDetailOfProduct(any());
    }
}