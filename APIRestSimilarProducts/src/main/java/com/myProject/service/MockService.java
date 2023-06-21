package main.java.com.myProject.service;

import java.util.List;

import main.java.com.myProject.dto.ProductDTO;

public interface MockService {
	
	List<String> getProductSimilarIds(String id);
	ProductDTO getProduct(String id);

}
