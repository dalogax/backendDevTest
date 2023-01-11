package unit.com.inditex.myapp.domain.model;

import com.inditex.myapp.domain.model.ProductDetail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class ProductDetailUnitTest {

    @Test
    void creationTest() {
        ProductDetail productDetail = new ProductDetail();
        productDetail.setId("TEST");
        productDetail.setName("TEST");
        productDetail.setPrice(BigDecimal.valueOf(0));
        productDetail.setAvailability(false);

        Assertions.assertNotNull(productDetail);
        Assertions.assertNotNull(productDetail.getId());
        Assertions.assertNotNull(productDetail.getName());
        Assertions.assertNotNull(productDetail.getPrice());
        Assertions.assertNotNull(productDetail.getAvailability());
    }
}
