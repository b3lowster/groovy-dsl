// ============================================
// currency-conversion-example.groovy
// ============================================
// This script demonstrates using CurrencyService in Groovy formulas
// The currencyService variable is automatically available in the binding

// Convert 100 EUR to USD
def eurToUsd = currencyService.convert("EUR", "USD", 100.0)
println "100 EUR = ${eurToUsd} USD"

// Convert 50 GBP to USD
def gbpToUsd = currencyService.convertToUSD("GBP", 50.0)
println "50 GBP = ${gbpToUsd} USD"

// Get exchange rate from JPY to EUR
def jyToEurRate = currencyService.getExchangeRate("JPY", "EUR")
println "JPY to EUR rate: ${jyToEurRate}"

// Example with variables - calculate total price in USD
// Assuming these variables are set in the context:
// price = 100, quantity = 5, currency = "EUR"
if (binding.hasVariable("price") && binding.hasVariable("quantity") && binding.hasVariable("currency")) {
    def totalInOriginalCurrency = price * quantity
    def totalInUsd = currencyService.convertToUSD(currency, totalInOriginalCurrency)
    println "${quantity} items at ${price} ${currency} each = ${totalInUsd} USD"
    totalInUsd
} else {
    // Return simple conversion if no context variables
    eurToUsd
}
