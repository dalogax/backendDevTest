package product.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import product.dto.ProductDetail;
import product.externalApi.ExternalApi;
import product.service.ProductService;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ExternalApi externalApi;

    @Override
    public List<ProductDetail> getProductSimilar(String productId) {
        List<ProductDetail> products = new ArrayList<>();

        //log.info("{ProductServiceImpl.getSimilarProduct params {}", productId);

        if( productId == null) return null;


        try {
            String[] similarProductsIds = externalApi.getSimilarProducts(productId);

            for (String id : similarProductsIds) {
                ProductDetail product = externalApi.getProduct(id);
                if (product != null)
                    products.add(product);
            }
        }catch (Exception ex){

        }
        return products;
    }
}
