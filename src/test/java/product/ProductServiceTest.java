package product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import product.dto.ProductDetail;
import product.service.ProductService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    ProductService productService;

    @Test
    public void serviceProductTest() throws Exception {
        List<ProductDetail> productDetailList = new ArrayList<>();
        ProductDetail product2 = new ProductDetail("2", "Dress", 19.99, true);
        ProductDetail product3 = new ProductDetail("3", "Blazer", 29.99, false);
        ProductDetail product4 = new ProductDetail("4", "Boots", 39.99, true);
        productDetailList.add(product2);
        productDetailList.add(product3);
        productDetailList.add(product4);

        assertThat(productService.getProductSimilar("1")).isEqualTo(productDetailList);
    }
}
