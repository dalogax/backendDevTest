package com.challenge.ams.service;

import java.util.List;
import com.challenge.ams.dto.ProductDTO;


public interface ProductService {

	public ProductDTO getProductById(String id);
	
	public List<ProductDTO> getSimilarProducts(String id);
}
