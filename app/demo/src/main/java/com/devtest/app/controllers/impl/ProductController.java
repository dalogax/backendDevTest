package com.devtest.app.controllers.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import com.devtest.app.controllers.IProductController;
import com.devtest.app.entities.Product;
import com.devtest.app.exceptions.GenericException;
import com.devtest.app.services.impl.ProductService;
import com.devtest.app.utils.Constants;

@RestController
@RequestMapping(Constants.PRODUCT)
public class ProductController implements IProductController{
	
	@Autowired
	private ProductService productService;

	@GetMapping(Constants.PRODUCT_SIMILAR)
	public ResponseEntity<List<Product>> getSimilarProductsById(@PathVariable("productId") String id) throws GenericException {
		try {
			return new ResponseEntity<List<Product>>(this.productService.getSimilarProductsById(id), HttpStatus.OK);
		} 
        catch (GenericException e) {
			throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
		} 
        catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}