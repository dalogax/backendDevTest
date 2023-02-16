package com.ciber.cibernos.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ciber.cibernos.service.SimilaridsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import com.ciber.cibernos.controller.ProductController;
import com.ciber.cibernos.dto.ProductDTO;
import com.ciber.cibernos.service.ProductService;


/**
 * @author samir alvarado
 *          {@link ProductController}
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    SimilaridsProductService similaridsProductService;

    /**
     * @param productId
     * consultar productos de la lista por id
     */
    @Override
    public List<ProductDTO> consultarProduct(Long productId) {

        List<ProductDTO> products = new ArrayList<>();
        similaridsProductService.consultarSimilaridsId(productId).forEach(
                pro -> {
                    products.add(similaridsProductService.consultarProducto(Long.valueOf(pro)));
                }
        );
        return products;

    }


}
