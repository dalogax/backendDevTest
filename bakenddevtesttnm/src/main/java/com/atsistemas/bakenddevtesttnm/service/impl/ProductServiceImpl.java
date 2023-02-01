package com.atsistemas.bakenddevtesttnm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atsistemas.bakenddevtesttnm.dto.ProductDto;
import com.atsistemas.bakenddevtesttnm.service.MocksService;
import com.atsistemas.bakenddevtesttnm.service.ProductService;

/**
 * @author tnavarro
 *
 */
@Service
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	MocksService mocksService;

	/**
	 * get products list
	 */
	@Override
	public List<ProductDto> findProductById(Integer id) {
		List<ProductDto> result = new ArrayList<>();
		mocksService.getSimilarIds(id).forEach(similarId-> {
			result.add(mocksService.getProduct(similarId));
		});
		return result;
	}

}
