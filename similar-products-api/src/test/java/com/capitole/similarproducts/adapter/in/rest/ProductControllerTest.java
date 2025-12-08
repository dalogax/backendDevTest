package com.capitole.similarproducts.adapter.in.rest;

import static org.mockito.BDDMockito.given;

import com.capitole.similarproducts.adapter.in.rest.dto.ProductDto;
import com.capitole.similarproducts.adapter.in.rest.mapper.ProductMapper;
import com.capitole.similarproducts.domain.model.Product;
import com.capitole.similarproducts.domain.port.in.GetSimilarProductsUseCase;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @Mock
    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @Mock
    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        ProductController controller = new ProductController(getSimilarProductsUseCase, productMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getSimilarProducts_ShouldReturn200_WhenDataIsValid() throws Exception {
        String productId = "1";
        Product p2 = new Product("2", "Product 2", BigDecimal.TEN, true);
        ProductDto d2 = new ProductDto("2", "Product 2", BigDecimal.TEN, true);
        
        given(getSimilarProductsUseCase.getSimilarProducts(productId)).willReturn(List.of(p2));
        given(productMapper.toDtoList(List.of(p2))).willReturn(List.of(d2));

        mockMvc.perform(MockMvcRequestBuilders.get("/product/{productId}/similar", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Product 2"));
    }
}
