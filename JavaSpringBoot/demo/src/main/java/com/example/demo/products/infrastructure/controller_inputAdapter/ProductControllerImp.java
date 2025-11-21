package com.example.demo.products.infrastructure.controller_inputAdapter;

import com.example.demo.products.application.crud.getSimilar.GetSimilarProducts;
import com.example.demo.products.application.dto.ProductApplication;
import com.example.demo.products.infrastructure.controller_inputPort.ProductController;
import com.example.demo.shared.service.ResponseHandlerService;
import com.example.demo.shared.valueObject.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@CrossOrigin
@Slf4j
public class ProductControllerImp implements ProductController {

    private final ResponseHandlerService responseHandler;
    private final GetSimilarProducts getSimilarProducts;

    @Autowired
    public ProductControllerImp(
                                ResponseHandlerService responseHandler,
                                GetSimilarProducts getSimilarProducts){
        this.responseHandler = responseHandler;
        this.getSimilarProducts = getSimilarProducts;
    }

    @GetMapping("/{productId}/similar")
    @Override
    public ResponseDTO<List<ProductApplication>> getSimilarProducts(@PathVariable String productId) {
        log.info("Received request for similar products of productId: {}", productId);
        return responseHandler.execute("GET_SIMILAR_PRODUCTS", 
                () -> getSimilarProducts.getSimilarProductsById(productId));
    }

}
