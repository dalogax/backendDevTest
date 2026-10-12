package com.backendtest.similar.port;

import com.backendtest.similar.dto.ProductDetail;
import java.util.List;
import java.util.Optional;

/**
 * Punto de extensión del servicio (ver docs/01-decisiones-tecnicas.md §6.1).
 * Hoy con una sola implementación REST; si aparece una segunda fuente
 * se desdobla en adapters sin tocar el servicio.
 */
public interface ProductCatalogPort {

    List<String> getSimilarIds(String productId);

    Optional<ProductDetail> getProduct(String productId);
}
