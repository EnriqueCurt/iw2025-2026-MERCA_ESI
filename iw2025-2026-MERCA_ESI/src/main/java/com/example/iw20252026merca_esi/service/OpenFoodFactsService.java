package com.example.iw20252026merca_esi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Service
public class OpenFoodFactsService {

    private final RestTemplate restTemplate;
    private static final String SEARCH_URL = "https://world.openfoodfacts.org/cgi/search.pl";

    public OpenFoodFactsService() {
        this.restTemplate = new RestTemplate();
    }

    public FoodInfo buscarProductoPorNombre(String nombre) {
        try {
            String url = SEARCH_URL + "?search_terms=" + nombre +
                        "&search_simple=1&action=process&json=1";

            SearchResponse response = restTemplate.getForObject(url, SearchResponse.class);

            if (response != null && response.getProducts() != null && !response.getProducts().isEmpty()) {
                return response.getProducts().get(0);
            }

            return null;
        } catch (RestClientException e) {
            System.err.println("Error al consultar Open Food Facts: " + e.getMessage());
            return null;
        }
    }

    // Clases para mapear la respuesta JSON
    public static class SearchResponse {
        private java.util.List<FoodInfo> products;

        public java.util.List<FoodInfo> getProducts() { return products; }
        public void setProducts(java.util.List<FoodInfo> products) { this.products = products; }
    }

    public static class FoodInfo {
        private String product_name;
        private String brands;
        private String image_url;
        private Nutriments nutriments;

        public String getProduct_name() { return product_name; }
        public void setProduct_name(String product_name) { this.product_name = product_name; }

        public String getBrands() { return brands; }
        public void setBrands(String brands) { this.brands = brands; }

        public String getImage_url() { return image_url; }
        public void setImage_url(String image_url) { this.image_url = image_url; }

        public Nutriments getNutriments() { return nutriments; }
        public void setNutriments(Nutriments nutriments) { this.nutriments = nutriments; }
    }

    public static class Nutriments {
        private Double energy_100g;
        private Double proteins_100g;

        public Double getEnergy_100g() { return energy_100g; }
        public void setEnergy_100g(Double energy_100g) { this.energy_100g = energy_100g; }

        public Double getProteins_100g() { return proteins_100g; }
        public void setProteins_100g(Double proteins_100g) { this.proteins_100g = proteins_100g; }
    }
}
