package com.atsistemas.bakenddevtesttnm.service;

import java.util.List;

import com.atsistemas.bakenddevtesttnm.dto.ProductDto;

/**
 * @author tnavarro
 *
 */
public interface ProductService {
	
	List<ProductDto> findProductById(Integer id);

}
