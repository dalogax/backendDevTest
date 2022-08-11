package com.challenge.ams.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;


import com.challenge.ams.dto.ProductDTO;
import com.challenge.ams.service.impl.ProductServiceImpl;

import feign.FeignException.FeignClientException;

@RestController
public class ProductController {

	@Autowired
	ProductServiceImpl productService;
	
	@RequestMapping(value={"/product/{productId}"}, method= RequestMethod.GET)
	public ResponseEntity<ProductDTO> getProduct(@PathVariable ("productId") String id) throws FeignClientException{
		try{
			return new ResponseEntity<ProductDTO>(productService.getProductById(id), HttpStatus.OK) ;
		}catch(FeignClientException e) {
			throw  e;
		}
	}
	
	@RequestMapping(value={"/product/{productId}/similar"}, method= RequestMethod.GET)
	public ResponseEntity<List<ProductDTO>> getProductosSimilares(@PathVariable ("productId") String id) throws FeignClientException{
		try{
			return new ResponseEntity<>(productService.getSimilarProducts(id), HttpStatus.OK) ;
		}catch(FeignClientException e) {
			throw  e;
		}
	}
	
	
	
}
