package com.example.proxy.service;

import com.example.proxy.dto.ProductDetailDTO;
import com.example.proxy.service.interfaces.IProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {
    @Override
    public List<ProductDetailDTO> getSimilarProducts() {
        return null;
    }
}
