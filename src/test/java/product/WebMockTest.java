package product;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import product.dto.ProductDetail;
import product.service.ProductService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WebMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    public void shouldReturnProductFromServiceTest() throws Exception {

        List<ProductDetail> productDetailList = new ArrayList<>();
        ProductDetail product3 = new ProductDetail("3", "Blazer", 29.99, false);
        productDetailList.add(product3);

        when(productService.getProductSimilar("1")).thenReturn(productDetailList);

        this.mockMvc.perform(get("/product/1/similar"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Blazer")));
    }

    @Test
    public void shouldReturnNotFoundFromServiceTest() throws Exception {
        ArrayList<?> arrayListReturned = new ArrayList();
        List<ProductDetail> productDetailList = new ArrayList<>();

        when(productService.getProductSimilar("5")).thenReturn(productDetailList);

        this.mockMvc.perform(get("/product/5/similar"))
                .andExpect(status().isOk())
                .andExpect(content().string(arrayListReturned.toString()));
    }
}
