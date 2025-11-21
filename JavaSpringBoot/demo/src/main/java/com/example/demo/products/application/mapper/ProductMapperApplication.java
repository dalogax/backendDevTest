package com.example.demo.products.application.mapper;

import com.example.demo.products.application.dto.ProductApplication;
import com.example.demo.products.domain.dto.ProductDomain;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductMapperApplication {

    private static ProductMapperApplication instance;

    private ProductMapperApplication(){}

    public static ProductMapperApplication getInstance() {
        if(instance == null) {
            instance = new ProductMapperApplication();
        }
        return instance;
    }

    public ProductDomain applicationToDomain(ProductApplication application){
        if(application==null) {return null;}

        ProductDomain.ProductDomainBuilder builder = ProductDomain.builder();

        builder.id(application.getId());
        builder.name(application.getName());
        builder.price(application.getPrice());
        builder.availability(application.getAvailability());

        return builder.build();
    }

    public ProductApplication domainToApplication(ProductDomain domain){
        if(domain==null) {return null;}

        ProductApplication.ProductApplicationBuilder builder = ProductApplication.builder();

        builder.id(domain.getId());
        builder.name(domain.getName());
        builder.price(domain.getPrice());
        builder.availability(domain.getAvailability());

        return builder.build();
    }

    public List<ProductApplication> listDomainToApplicationList(List<ProductDomain> domainList) {
        if(domainList == null) return Collections.emptyList();
        return domainList.stream().map(this::domainToApplication).collect(Collectors.toList());
    }

    public List<ProductDomain> listApplicationToDomainList(List<ProductApplication> applicationList) {
        if(applicationList == null) return Collections.emptyList();
        return applicationList.stream().map(this::applicationToDomain).collect(Collectors.toList());
    }

    public Set<ProductApplication> setDomainToApplicationSet(Set<ProductDomain> domainSet) {
        if(domainSet == null) return Collections.emptySet();
        return domainSet.stream().map(this::domainToApplication).collect(Collectors.toSet());
    }

    public Set<ProductDomain> setApplicationToDomainSet(Set<ProductApplication> applicationSet) {
        if(applicationSet == null) return Collections.emptySet();
        return applicationSet.stream().map(this::applicationToDomain).collect(Collectors.toSet());
    }
}
