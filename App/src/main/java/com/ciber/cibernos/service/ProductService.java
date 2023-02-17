package com.ciber.cibernos.service;

import java.util.List;

import com.ciber.cibernos.dto.ProductDTO;

/**
 * @author samir alvarado
 *          {@link ProductController}
 * @see Implementacion {@link ProductServiceImpl}
 */
public interface ProductService {


    List<ProductDTO> consultarProduct(Long idProduct);
}
