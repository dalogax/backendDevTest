package com.challenge.ams.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.challenge.ams.dto.ProductDTO;

import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name= "SIMILAR-PRODUCT-SERVICE", url = "localhost:3001")
public interface ProductRestConsumer {
	
	@RequestMapping(value={"/product/{productId}"}, method= RequestMethod.GET)
	ProductDTO getProductById(@PathVariable ("productId") String id);
	
	
	@RequestMapping(value={"/product/{productId}/similarids"}, method= RequestMethod.GET)
	List<String> getSimilarProductsById(@PathVariable ("productId") String id);

	
	
	
}
