package com.sngular.similarproducts.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sngular.similarproducts.application.outbound.ProductsPort;
import com.sngular.similarproducts.domain.ProductDetail;
import com.sngular.similarproducts.domain.exception.ProductNotFoundException;
import com.sngular.similarproducts.domain.exception.SimilarProductsNotFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
class DetailProductsUseCaseTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        validatorFactory.close();
    }

    @Mock
    private ProductsPort productsPort;

    @InjectMocks
    private DetailProductsUseCase detailProductsUseCase;

    @Test
    void shouldReturnSimilarProducts() {
        String productId = "1";
        when(productsPort.getSimilarProductIds(productId)).thenReturn(List.of("2", "3"));
        when(productsPort.getProduct("2")).thenReturn(new ProductDetail("2", "BMW i3", 50000D, true));
        when(productsPort.getProduct("3")).thenReturn(new ProductDetail("3", "Byd Seal", 43000D, false));

        List<ProductDetail> result = detailProductsUseCase.getSimilarProducts(productId);

        assertNotNull(result);
        assertEquals(2, result.size());

        var product2 = result.get(0);
        assertEquals("2", product2.id());
        assertEquals("BMW i3", product2.name());
        assertEquals(50000D, product2.price());
        assertEquals(true, product2.availability());

        var product3 = result.get(1);
        assertEquals("3", product3.id());
        assertEquals("Byd Seal", product3.name());
        assertEquals(43000D, product3.price());
        assertEquals(false, product3.availability());
    }

    @Test
    void shouldThrowExceptionWhenNotFoundSimilarProducts() {
        String productId = "1";
        when(productsPort.getSimilarProductIds(productId)).thenReturn(Set.of());

        SimilarProductsNotFoundException thrown = assertThrows(
                SimilarProductsNotFoundException.class,
                () -> detailProductsUseCase.getSimilarProducts(productId));
        assertEquals("Similar products not found for productId: 1", thrown.getMessage());
    }

    @Test
    void shouldPropagateExceptionWhenFetchingSimilarIdsFails() {
        String productId = "1";
        RuntimeException expected = new RuntimeException("Similar IDs API failed");
        when(productsPort.getSimilarProductIds(productId)).thenThrow(expected);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> detailProductsUseCase.getSimilarProducts(productId));

        assertEquals(expected, thrown);
        verify(productsPort, never()).getProduct(anyString());
    }

    @Test
    void shouldSkipProductWhenRuntimeExceptionIsThrown() {
        String productId = "1";
        when(productsPort.getSimilarProductIds(productId)).thenReturn(List.of("2", "3"));
        when(productsPort.getProduct("2")).thenThrow(new RuntimeException("Product API failed"));
        when(productsPort.getProduct("3")).thenReturn(new ProductDetail("3", "Byd Seal", 43000D, false));

        List<ProductDetail> result = detailProductsUseCase.getSimilarProducts(productId);

        assertEquals(1, result.size());
        assertEquals("3", result.get(0).id());
        verify(productsPort).getProduct("2");
        verify(productsPort).getProduct("3");
    }

    @Test
    void shouldSkipProductWhenProductNotFoundExceptionIsThrown() {
        String productId = "1";
        when(productsPort.getSimilarProductIds(productId)).thenReturn(List.of("2", "3"));
        when(productsPort.getProduct("2")).thenThrow(new ProductNotFoundException("2"));
        when(productsPort.getProduct("3")).thenReturn(new ProductDetail("3", "Byd Seal", 43000D, false));

        List<ProductDetail> result = detailProductsUseCase.getSimilarProducts(productId);

        assertEquals(1, result.size());
        assertEquals("3", result.get(0).id());
        verify(productsPort).getProduct("2");
        verify(productsPort).getProduct("3");
    }

    @Test
    void shouldThrowNoSimilarProductsFoundWhenAllProductsNotFound() {
        String productId = "1";
        when(productsPort.getSimilarProductIds(productId)).thenReturn(List.of("2", "3"));
        when(productsPort.getProduct("2")).thenThrow(new ProductNotFoundException("2"));
        when(productsPort.getProduct("3")).thenThrow(new ProductNotFoundException("3"));

        assertThrows(SimilarProductsNotFoundException.class,
                () -> detailProductsUseCase.getSimilarProducts(productId));
    }

    @Test
    void shouldCallGetProductOncePerSimilarId() {
        String productId = "42";
        when(productsPort.getSimilarProductIds(productId)).thenReturn(List.of("10", "20", "30"));
        when(productsPort.getProduct("10")).thenReturn(new ProductDetail("10", "P10", 10.0, true));
        when(productsPort.getProduct("20")).thenReturn(new ProductDetail("20", "P20", 20.0, true));
        when(productsPort.getProduct("30")).thenReturn(new ProductDetail("30", "P30", 30.0, false));

        List<ProductDetail> result = detailProductsUseCase.getSimilarProducts(productId);

        assertEquals(3, result.size());
        verify(productsPort).getSimilarProductIds(productId);
        verify(productsPort).getProduct("10");
        verify(productsPort).getProduct("20");
        verify(productsPort).getProduct("30");
        verifyNoMoreInteractions(productsPort);
    }

    // ProductDetail validation tests

    @Test
    void productDetail_shouldPassWithValidData() {
        Set<ConstraintViolation<ProductDetail>> violations = validator
                .validate(new ProductDetail("1", "Tesla Model 3", 44000.0, true));

        assertTrue(violations.isEmpty());
    }

    @Test
    void productDetail_shouldFailWhenIdIsNull() {
        Set<ConstraintViolation<ProductDetail>> violations = validator
                .validate(new ProductDetail(null, "Tesla Model 3", 44000.0, true));

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }

    @Test
    void productDetail_shouldFailWhenIdIsBlank() {
        Set<ConstraintViolation<ProductDetail>> violations = validator
                .validate(new ProductDetail("   ", "Tesla Model 3", 44000.0, true));

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }

    @Test
    void productDetail_shouldFailWhenIdIsEmpty() {
        Set<ConstraintViolation<ProductDetail>> violations = validator
                .validate(new ProductDetail("", "Tesla Model 3", 44000.0, true));

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }

    @Test
    void productDetail_shouldFailWhenNameIsNull() {
        Set<ConstraintViolation<ProductDetail>> violations = validator
                .validate(new ProductDetail("1", null, 44000.0, true));

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void productDetail_shouldFailWhenNameIsBlank() {
        Set<ConstraintViolation<ProductDetail>> violations = validator
                .validate(new ProductDetail("1", "   ", 44000.0, true));

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void productDetail_shouldFailWhenNameIsEmpty() {
        Set<ConstraintViolation<ProductDetail>> violations = validator
                .validate(new ProductDetail("1", "", 44000.0, true));

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void productDetail_shouldFailWhenPriceIsNull() {
        Set<ConstraintViolation<ProductDetail>> violations = validator
                .validate(new ProductDetail("1", "Tesla Model 3", null, true));

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    void productDetail_shouldFailWhenAvailabilityIsNull() {
        Set<ConstraintViolation<ProductDetail>> violations = validator
                .validate(new ProductDetail("1", "Tesla Model 3", 44000.0, null));

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("availability")));
    }

    @Test
    void productDetail_shouldReportAllViolationsWhenAllFieldsAreInvalid() {
        Set<ConstraintViolation<ProductDetail>> violations = validator
                .validate(new ProductDetail(null, null, null, null));

        // id (@NotBlank), name (@NotBlank), price (@NotNull), availability (@NotNull)
        assertEquals(4, violations.size());
    }

}
