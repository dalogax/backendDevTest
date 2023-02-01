package com.atsistemas.bakenddevtesttnm.service;

import java.util.List;

import com.atsistemas.bakenddevtesttnm.dto.ProductDto;

/**
 * @author tnavarro
 *
 */
public interface MocksService {
	
	List<Integer> getSimilarIds(Integer id);
	ProductDto getProduct(Integer id);

}
