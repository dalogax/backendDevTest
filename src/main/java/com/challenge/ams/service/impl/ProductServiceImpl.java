package com.challenge.ams.service.impl;


import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.challenge.ams.service.ProductRestConsumer;
import com.challenge.ams.service.ProductService;
import com.challenge.ams.dto.ProductDTO;
import java.util.List;
import java.util.stream.Collectors;



@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	ProductRestConsumer productRestConsumer;

	@Override
	public ProductDTO getProductById(String id){
		return productRestConsumer.getProductById(id);
	}
	
	@Override
	public List<ProductDTO> getSimilarProducts(String id) {
		return productRestConsumer.getSimilarProductsById(id).stream().map(productRestConsumer::getProductById).collect(Collectors.toList()); 
		
	}
}
