package com.atsistemas.bakenddevtesttnm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.atsistemas.bakenddevtesttnm.dto.ProductDto;
import com.atsistemas.bakenddevtesttnm.service.MocksService;

/**
 * @author tnavarro
 *
 */
@Service
public class MocksServiceImpl implements MocksService {
	
	private final static String URI = "http://localhost:3001/product/";
	
	@Autowired
	private  RestTemplate restTemplate;

	/**
	 * Call end point mock service similar ids
	 */
	@Override
	public List<Integer> getSimilarIds(Integer id) {
		 ResponseEntity<List<Integer>> response = restTemplate.exchange(
				 URI.concat(String.valueOf(id)).concat("/similarids"), HttpMethod.GET, null,
			     new ParameterizedTypeReference<List<Integer>>(){});
		return response.getBody();
	}

	/**
	 * Call end point mock service get product
	 */
	@Override
	public ProductDto getProduct(Integer id) {
		 ResponseEntity<ProductDto> response = restTemplate.exchange(
				 URI.concat(String.valueOf(id)), HttpMethod.GET, null,
			     new ParameterizedTypeReference<ProductDto>(){});
		return response.getBody();
	}

	

}
