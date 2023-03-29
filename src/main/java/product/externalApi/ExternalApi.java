package product.externalApi;

import product.dto.ProductDetail;

public interface ExternalApi {

    String[] getSimilarProducts(String productId) throws Exception;

    ProductDetail getProduct(String productId) throws Exception;
}
