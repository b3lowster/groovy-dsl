// ============================================
// CurrencyService.java
// ============================================
package com.provectus.formula.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class CurrencyService {
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CurrencyService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Convert currency with three arguments: source currency, target currency, and value
     * @param sourceCurrency Source currency code (e.g., "EUR")
     * @param targetCurrency Target currency code (e.g., "USD")
     * @param value Amount to convert
     * @return Converted amount
     */
    public double convert(String sourceCurrency, String targetCurrency, double value) {
        try {
            String url = API_URL + sourceCurrency.toUpperCase();
            String response = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode rates = root.get("rates");

            if (rates.has(targetCurrency.toUpperCase())) {
                double rate = rates.get(targetCurrency.toUpperCase()).asDouble();
                return value * rate;
            } else {
                throw new IllegalArgumentException("Target currency not found: " + targetCurrency);
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch exchange rates: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error converting currency: " + e.getMessage(), e);
        }
    }

    /**
     * Convert currency to USD with two arguments: source currency and value
     * @param sourceCurrency Source currency code (e.g., "EUR")
     * @param value Amount to convert
     * @return Converted amount in USD
     */
    public double convertToUSD(String sourceCurrency, double value) {
        return convert(sourceCurrency, "USD", value);
    }

    /**
     * Get exchange rate between two currencies
     * @param sourceCurrency Source currency code
     * @param targetCurrency Target currency code
     * @return Exchange rate
     */
    public double getExchangeRate(String sourceCurrency, String targetCurrency) {
        return convert(sourceCurrency, targetCurrency, 1.0);
    }
}
