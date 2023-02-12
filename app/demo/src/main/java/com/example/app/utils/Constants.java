package com.example.app.utils;

public final class Constants {
    public static final String PRODUCT = "product"; 
    public static final String PRODUCT_SIMILARS = "/{productId}/similars"; 
    
    public static final String HOST_MOCKS = "http://localhost:3001/";
	public static final String GET_PRODUCT_SIMILAR_IDS = HOST_MOCKS + PRODUCT + "/{productId}/similarids";
	public static final String GET_PRODUCT_DETAIL = HOST_MOCKS + PRODUCT + "/{productId}";
}
