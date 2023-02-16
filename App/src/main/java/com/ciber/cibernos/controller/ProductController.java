package com.ciber.cibernos.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciber.cibernos.dto.ProductDTO;
import com.ciber.cibernos.service.ProductService;
import org.springframework.web.server.ResponseStatusException;



/**
 * Clase que permite consultar productos por id.
 * @author samir alvarado
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("product")
@Validated
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * @param productid
     * @return lista de productos por id.
     */
    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDTO>> consultarProductById(
            @PathVariable("productId") Long idProduct) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(productService.consultarProduct(idProduct));
        } catch (Exception e) {
        log.error(e.getMessage());
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, e.getMessage());
       }
    }


}
