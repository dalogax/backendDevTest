package com.sngular.similarproducts.infrastructure.inbound;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.sngular.similarproducts.application.DetailProductsUseCase;
import com.sngular.similarproducts.application.outbound.ProductsPort;
import com.sngular.similarproducts.domain.ProductDetail;
import com.sngular.similarproducts.domain.exception.ProductNotFoundException;
import com.sngular.similarproducts.domain.exception.SimilarProductsNotFoundException;

@WebMvcTest(ProductController.class)
@Import(DetailProductsUseCase.class)
class ProductControllerTest {

  @Autowired
  MockMvc mockMvc;

  // ProductsPort mock third party API
  @MockitoBean
  ProductsPort productsPort;

  /**
   * Happy path:
   * GET /product/1/similar → 200 with 2 similar products in the body.
   */
  @Test
  void shouldReturnSimilarProducts() throws Exception {
    when(productsPort.getSimilarProductIds("1")).thenReturn(List.of("2", "3"));
    when(productsPort.getProduct("2")).thenReturn(new ProductDetail("2", "BMW i3", 50000D, true));
    when(productsPort.getProduct("3")).thenReturn(new ProductDetail("3", "Byd Seal", 43000D, false));

    mockMvc.perform(get("/product/1/similar").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[*].id", containsInAnyOrder("2", "3")))
        .andExpect(jsonPath("$[*].name", containsInAnyOrder("BMW i3", "Byd Seal")))
        .andExpect(jsonPath("$[*].price", containsInAnyOrder(50000D, 43000D)))
        .andExpect(jsonPath("$[*].availability", containsInAnyOrder(true, false)));
  }

  @Test
  void shouldReturn404WhenNoSimilarIds() throws Exception {
    when(productsPort.getSimilarProductIds("1")).thenReturn(List.of());

    mockMvc.perform(get("/product/1/similar").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn404WhenProductNotFound() throws Exception {
    when(productsPort.getSimilarProductIds("99"))
        .thenThrow(new SimilarProductsNotFoundException("99"));

    mockMvc.perform(get("/product/99/similar").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldSkipNotFoundProductAndReturnRemaining() throws Exception {
    when(productsPort.getSimilarProductIds("1")).thenReturn(List.of("2", "3"));
    when(productsPort.getProduct("2")).thenThrow(new ProductNotFoundException("2"));
    when(productsPort.getProduct("3")).thenReturn(new ProductDetail("3", "Byd Seal", 43000D, false));

    mockMvc.perform(get("/product/1/similar").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value("3"))
        .andExpect(jsonPath("$[0].name").value("Byd Seal"));
  }
}
