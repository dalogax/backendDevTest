package org.mybranch.apps.shop.controller.products;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybranch.apps.shop.ShopApplication;
import org.mybranch.apps.shop.api.dto.ProductDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ShopApplication.class)
@AutoConfigureMockMvc
class SimilarProductsGetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private List<ProductDetailDto> similarProducts;

    @SneakyThrows
    @BeforeEach
    void setup() {
        similarProducts = loadFromPackageAsList("similarProducts.json", ProductDetailDto.class);
    }

    @SneakyThrows
    @Test
    void return_not_found() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/product/{productId}/similar", 6))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @SneakyThrows
    @Test
    void return_similar_products() {
        String expectedResponseAsStr = objectMapper.writeValueAsString(similarProducts);
        mockMvc
            .perform(MockMvcRequestBuilders.get("/product/{productId}/similar", 1))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(expectedResponseAsStr));

    }

    protected <T> List<T> loadFromPackageAsList(String fileName, Class<T> clazz) throws IOException {
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        return objectMapper.readValue(getInputStream(fileName), type);
    }

    private InputStream getInputStream(String fileName) {
        String resourceName = this.getClass().getPackageName().replaceAll("\\.", "/") + "/" + fileName;
        return this.getClass().getClassLoader().getResourceAsStream(resourceName);
    }

}