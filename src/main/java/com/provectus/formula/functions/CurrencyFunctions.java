// ============================================
// CurrencyFunctions.java
// ============================================
package com.provectus.formula.functions;

import com.provectus.formula.service.CurrencyService;

public class CurrencyFunctions {
    private static CurrencyService currencyService;

    /**
     * Set the currency service instance (to be called from Spring context)
     */
    public static void setCurrencyService(CurrencyService service) {
        currencyService = service;
    }

    /**
     * Convert currency with three arguments
     * @param sourceCurrency Source currency code
     * @param targetCurrency Target currency code
     * @param value Amount to convert
     * @return Converted amount
     */
    public static double convertCurrency(String sourceCurrency, String targetCurrency, double value) {
        if (currencyService == null) {
            throw new IllegalStateException("CurrencyService not initialized");
        }
        return currencyService.convert(sourceCurrency, targetCurrency, value);
    }

    /**
     * Convert currency to USD with two arguments
     * @param sourceCurrency Source currency code
     * @param value Amount to convert
     * @return Converted amount in USD
     */
    public static double convertToUSD(String sourceCurrency, double value) {
        if (currencyService == null) {
            throw new IllegalStateException("CurrencyService not initialized");
        }
        return currencyService.convertToUSD(sourceCurrency, value);
    }

    /**
     * Get exchange rate between two currencies
     */
    public static double exchangeRate(String sourceCurrency, String targetCurrency) {
        if (currencyService == null) {
            throw new IllegalStateException("CurrencyService not initialized");
        }
        return currencyService.getExchangeRate(sourceCurrency, targetCurrency);
    }
}
