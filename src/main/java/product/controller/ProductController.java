package product.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import product.dto.ProductDetail;
import product.service.ProductService;

import java.util.List;

@Controller
@Slf4j
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/product/{productId}/similar")
    public ResponseEntity<List<ProductDetail>> getSimilarProduct(@PathVariable(value="productId") String productId) throws Exception{
        //log.info("ProductController.getSimilarProduct params {}", productId);

        List<ProductDetail> productList = productService.getProductSimilar(productId);

        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}
