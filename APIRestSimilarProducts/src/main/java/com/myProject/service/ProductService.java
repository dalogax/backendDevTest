package main.java.com.myProject.service;

import java.util.List;

import main.java.com.myProject.dto.ProductDTO;

public interface ProductService {
	
	List<ProductDTO> findProductsById(String id);

}
