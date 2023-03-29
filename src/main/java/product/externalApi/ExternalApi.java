package product.externalApi;

import product.dto.ProductDetail;

public interface ExternalApi {

    String[] getSimilarProducts(String productId);

    ProductDetail getProduct(String productId);
}
