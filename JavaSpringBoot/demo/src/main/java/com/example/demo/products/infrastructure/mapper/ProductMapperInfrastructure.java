package com.example.demo.products.infrastructure.mapper;

import com.example.demo.products.application.dto.ProductApplication;
import com.example.demo.products.infrastructure.entity.ProductEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductMapperInfrastructure {
    private static ProductMapperInfrastructure instance;

    private ProductMapperInfrastructure(){}

    public static ProductMapperInfrastructure getInstance() {
        if(instance == null) {
            instance = new ProductMapperInfrastructure();
        }
        return instance;
    }

    public ProductApplication entityToApplication(ProductEntity entity){
        if(entity==null) {return null;}

        ProductApplication.ProductApplicationBuilder builder = ProductApplication.builder();

        builder.id(entity.getId());
        builder.name(entity.getName());
        builder.price(entity.getPrice());
        builder.availability(entity.getAvailability());

        return builder.build();
    }

    public ProductEntity applicationToEntity(ProductApplication application){
        if(application==null) {return null;}

        ProductEntity.ProductEntityBuilder builder = ProductEntity.builder();

        builder.id(application.getId());
        builder.name(application.getName());
        builder.price(application.getPrice());
        builder.availability(application.getAvailability());

        return builder.build();
    }

    public List<ProductEntity> listApplicationToEntityList(List<ProductApplication> applicationList) {
        if(applicationList == null) return Collections.emptyList();
        return applicationList.stream().map(this::applicationToEntity).collect(Collectors.toList());
    }

    public List<ProductApplication> listEntityToApplicationList(List<ProductEntity> entityList) {
        if(entityList == null) return Collections.emptyList();
        return entityList.stream().map(this::entityToApplication).collect(Collectors.toList());
    }

    public Set<ProductEntity> setApplicationToEntitySet(Set<ProductApplication> applicationSet) {
        if(applicationSet == null) return Collections.emptySet();
        return applicationSet.stream().map(this::applicationToEntity).collect(Collectors.toSet());
    }

    public Set<ProductApplication> setEntityToApplicationSet(Set<ProductEntity> entitySet) {
        if(entitySet == null) return Collections.emptySet();
        return entitySet.stream().map(this::entityToApplication).collect(Collectors.toSet());
    }

}

