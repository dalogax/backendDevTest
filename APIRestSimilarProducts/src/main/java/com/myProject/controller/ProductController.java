package main.java.com.myProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import main.java.com.myProject.dto.ProductDTO;
import main.java.com.myProject.service.ProductService;

@RestController
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@GetMapping(value="/product/{id}/similar")
	public ResponseEntity<List<ProductDTO>> getProductsById(@PathVariable("id") String id) {
		try {
			List<ProductDTO> listProduct = productService.findProductsById(id);
			ResponseEntity<List<ProductDTO>> resp ;
			if(listProduct.isEmpty()) {
				resp = ResponseEntity.status(HttpStatus.OK).body(listProduct); 
			}else {
				resp = ResponseEntity.status(HttpStatus.NOT_FOUND).body(listProduct);
			}
			return resp;
		} catch (Exception e) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

}
