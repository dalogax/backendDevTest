package com.backendDevTest.myApp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import com.backendDevTest.myApp.exceptions.MyAppGenericException;
import com.backendDevTest.myApp.model.ProductDetail;
import com.backendDevTest.myApp.services.ProductService;

@RestController
public class ProductController {
	
	@Autowired
	private ProductService productService;

	@GetMapping("/product/{productId}/similars")
	public ResponseEntity<List<ProductDetail>> getSimilarProductsById(@PathVariable("productId") String id) throws MyAppGenericException{
		try {
			return new ResponseEntity<List<ProductDetail>>(this.productService.getSimilarProductsById(id), HttpStatus.OK);
		} catch (MyAppGenericException e) {
			throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}
