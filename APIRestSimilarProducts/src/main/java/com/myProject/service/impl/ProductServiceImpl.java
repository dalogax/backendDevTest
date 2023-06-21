package main.java.com.myProject.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import main.java.com.myProject.dto.ProductDTO;
import main.java.com.myProject.service.MockService;
import main.java.com.myProject.service.ProductService;


@Service
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	MockService mockService;

	@Override
	public List<ProductDTO> findProductsById(String id) {
		// TODO Auto-generated method stub
		List<ProductDTO> listProduct = new ArrayList<>();
		
		List<String> listProductSimilars = mockService.getProductSimilarIds(id);
		for (String idProductSimilar : listProductSimilars) {
			listProduct.add(mockService.getProduct(idProductSimilar));
		}
		return listProduct;
	}

}
