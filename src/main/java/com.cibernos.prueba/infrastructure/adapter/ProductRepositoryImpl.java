package com.cibernos.prueba.infrastructure.adapter;

import com.cibernos.prueba.application.repository.ProductRepository;
import com.cibernos.prueba.domain.Product;
import com.cibernos.prueba.infrastructure.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

@Repository
public class ProductRepositoryImpl implements ProductRepository {


    /*  private final ProductMapper productMapper;*/

 /*   public ProductRepositoryImpl( ProductMapper productMapper) {
        this.productMapper = productMapper;
    }*/


    public Iterable<Product> getProducts(Integer Id) {
        Product oneProducto= new Product();
        oneProducto.setId(parseInt((Id.toString()+"01")));
        oneProducto.setName("Camisa");
        oneProducto.setPrice(120);
        oneProducto.setAvailability(true);
        Product dosProducto= new Product();
        dosProducto.setId(parseInt((Id.toString()+"02")));
        dosProducto.setName("Sudadera");
        dosProducto.setPrice(85);
        dosProducto.setAvailability(true);
        Product tresProducto= new Product();
        tresProducto.setId(parseInt((Id.toString()+"03")));
        tresProducto.setName("Botas");
        tresProducto.setPrice(50);
        tresProducto.setAvailability(false);
        List<Product> lstProductos = new ArrayList<>();
        lstProductos.add(oneProducto);
        lstProductos.add(dosProducto);
        lstProductos.add(tresProducto);
        return lstProductos;
        /*return productMapper.toProducts(productCrudRepository.findAll());*/
    }


    public Product getProductsById(Integer Id) {
        Product oneProducto= new Product();
        oneProducto.setId(Id);
        oneProducto.setName("Camisa");
        oneProducto.setPrice(70);
        oneProducto.setAvailability(true);
        return oneProducto;
        /*  return productMapper.toProduct(productCrudRepository.findById(Id).get());*/
    }

    public Product saveProduct(Product product) {
        Product oneProducto= new Product();
        oneProducto.setId(product.getId());
        oneProducto.setName(product.getName());
        oneProducto.setAvailability(true);
        return product;
        /* return productMapper.toProduct(productCrudRepository.save(productMapper.toProductEntity(product)));*/
    }
    public void deleteProductById(Integer Id) {
        /*productCrudRepository.deleteById(Id);*/
    }
}
