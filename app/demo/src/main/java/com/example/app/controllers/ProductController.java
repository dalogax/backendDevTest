package com.example.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import com.example.app.entities.Product;
import com.example.app.services.ProductService;
import com.example.app.exceptions.GenericException;
import com.example.app.utils.Constants;

@RestController
@RequestMapping(Constants.PRODUCT)
public class ProductController {
	
	@Autowired
	private ProductService productService;

	@GetMapping(Constants.PRODUCT_SIMILARS)
	public ResponseEntity<List<Product>> getSimilarProductsById(@PathVariable("productId") String id) throws GenericException{
		try {
			return new ResponseEntity<List<Product>>(this.productService.getSimilarProductsById(id), HttpStatus.OK);
		} 
        catch (GenericException e) {
			throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Product " + id + " not found");
		} 
        catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}