package com.example.restservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@RestController
public class ProductController {

    @GetMapping("/product/{productId}/similar")
    public ArrayList<Product> GetSimilarProducts(@PathVariable String productId) throws Exception  {
        //The final list with the similar products
        ArrayList<Product> similarProducts = new ArrayList<>();

        //The array of IDs of the similar products
        String[] similarProductsIds = GetSimilarProductIds(productId);
        //Welp
        if(similarProductsIds == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not found");
        }
        //Remove repeated products
        similarProductsIds = MakeUnique(similarProductsIds);

        for (String id : similarProductsIds)
        {
            Product product = GetProduct(id);
            if(product != null) {
                similarProducts.add(product);
            }
        }

        return similarProducts;
    }

    private String[] MakeUnique(String[] input) {
        Set<String> temp = new LinkedHashSet<>(Arrays.asList(input));
        return temp.toArray(new String[0]);
    }

    private Product GetProduct(String productId) throws Exception {
        String result = MakeGetRequest("http://localhost:3001/product/" + productId);

        //Product not found
        if(result == null) return null;

        //Object mapper to construct the final object using the string
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result, Product.class);
    }

    private String[] GetSimilarProductIds(String productId) throws Exception {
        String result = MakeGetRequest("http://localhost:3001/product/" + productId + "/similarids");

        //Product not found
        if(result == null) return null;

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result, String[].class);
    }

    private String MakeGetRequest(String URL) throws Exception {
        StringBuilder result = new StringBuilder();

        //Make url and open http connecting using it
        URL url = new URL(URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();


        //Yeet
        conn.setRequestMethod("GET");

        //Error Handling
        if(conn.getResponseCode() != 200) {
            return null;
        }

        //Read the result and append it all to the builder
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }

        return result.toString();
    }

}
