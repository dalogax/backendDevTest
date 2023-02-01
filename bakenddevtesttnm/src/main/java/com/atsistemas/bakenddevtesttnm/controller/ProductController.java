package com.atsistemas.bakenddevtesttnm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.atsistemas.bakenddevtesttnm.dto.ProductDto;
import com.atsistemas.bakenddevtesttnm.service.ProductService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tnavarro
 *
 */
@RestController
@Slf4j
public class ProductController {
	
	@Autowired
	ProductService productService;
	
	/**End point 
	 * @param product id
	 * @return List of products
	 */
	@GetMapping(value="/product/{id}/similar")
	public ResponseEntity<List<ProductDto>> getProductsById(@PathVariable("id") Integer id) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(productService.findProductById(id)); 
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, e.getMessage());
		}
		
	}

}
