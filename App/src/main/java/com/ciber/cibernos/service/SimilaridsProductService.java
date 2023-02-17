package com.ciber.cibernos.service;
import java.util.List;
import com.ciber.cibernos.dto.ProductDTO;

/**
 * @author samir alvarado
 * @see Implementacion {@link SimilaridsProductServiceImpl}
 */
public interface SimilaridsProductService {

    List<String> consultarSimilaridsId(Long productId);

    ProductDTO consultarProducto(Long productId);

}
