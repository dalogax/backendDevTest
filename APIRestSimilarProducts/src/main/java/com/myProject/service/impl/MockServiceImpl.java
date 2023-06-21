package main.java.com.myProject.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import main.java.com.myProject.dto.ProductDTO;
import main.java.com.myProject.service.MockService;

public class MockServiceImpl implements MockService {
	
	private final static String URL_MOCK = "http://localhost:3001/product/";
	
	@Autowired
	private  RestTemplate restTemplate;

	@Override
	public List<String> getProductSimilarIds(String id) {
		ResponseEntity<List<String>> response = restTemplate.exchange(
				URL_MOCK.concat(id).concat("/similarids"), HttpMethod.GET, null,
			     new ParameterizedTypeReference<List<String>>(){});
		return response.getBody();
	}

	@Override
	public ProductDTO getProduct(String id) {
		ResponseEntity<ProductDTO> response = restTemplate.exchange(
				URL_MOCK.concat(String.valueOf(id)), HttpMethod.GET, null,
			     new ParameterizedTypeReference<ProductDTO>(){});
		return response.getBody();
	}

}
