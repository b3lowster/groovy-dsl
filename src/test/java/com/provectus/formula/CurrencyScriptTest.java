// ============================================
// CurrencyScriptTest.java
// ============================================
package com.provectus.formula;

import com.provectus.formula.service.CurrencyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CurrencyScriptTest {

    @Autowired
    private FormulaEngine engine;

    @Autowired
    private CurrencyService currencyService;

    private String scriptContent;

    @Before
    public void setUp() throws IOException {
        // Load the Groovy script from resources
        String scriptPath = "src/test/resources/currency-conversion-example.groovy";
        scriptContent = new String(Files.readAllBytes(Paths.get(scriptPath)));
    }

    @Test
    public void testCurrencyScriptWithoutContext() {
        // Execute the script without context variables (should use default EUR to USD conversion)
        FormulaResult result = engine.evaluate(scriptContent);

        assertTrue("Script should execute successfully", result.isSuccess());
        assertNotNull("Result should not be null", result.getValue());

        // The result should be a Double (EUR to USD conversion)
        assertTrue("Result should be a number", result.getValue() instanceof Number);

        double eurToUsd = ((Number) result.getValue()).doubleValue();
        assertTrue("EUR to USD conversion should be positive", eurToUsd > 0);

        System.out.println("100 EUR = " + eurToUsd + " USD");
    }

    @Test
    public void testCurrencyScriptWithContext() {
        // Execute the script with context variables
        FormulaContext context = new FormulaContext()
            .setVariable("price", 100.0)
            .setVariable("quantity", 5)
            .setVariable("currency", "EUR");

        FormulaResult result = engine.evaluate(scriptContent, context);

        assertTrue("Script should execute successfully", result.isSuccess());
        assertNotNull("Result should not be null", result.getValue());

        // The result should be total in USD
        assertTrue("Result should be a number", result.getValue() instanceof Number);

        double totalInUsd = ((Number) result.getValue()).doubleValue();
        assertTrue("Total in USD should be positive", totalInUsd > 0);

        // 500 EUR should be greater than 500 USD (approximate check)
        assertTrue("500 EUR should be worth more than 400 USD", totalInUsd > 400);

        System.out.println("5 items at 100 EUR each = " + totalInUsd + " USD");
    }

    @Test
    public void testCurrencyServiceDirectAccess() {
        // Test that currencyService is available in formulas
        String formula = "currencyService.convert('EUR', 'USD', 100.0)";

        FormulaResult result = engine.evaluate(formula);

        assertTrue("Formula should execute successfully", result.isSuccess());
        assertNotNull("Result should not be null", result.getValue());

        double converted = ((Number) result.getValue()).doubleValue();
        assertTrue("Conversion result should be positive", converted > 0);

        System.out.println("Direct call: 100 EUR = " + converted + " USD");
    }

    @Test
    public void testCurrencyServiceConvertToUSD() {
        // Test convertToUSD method
        String formula = "currencyService.convertToUSD('GBP', 50.0)";

        FormulaResult result = engine.evaluate(formula);

        assertTrue("Formula should execute successfully", result.isSuccess());
        assertNotNull("Result should not be null", result.getValue());

        double converted = ((Number) result.getValue()).doubleValue();
        assertTrue("Conversion result should be positive", converted > 0);

        System.out.println("50 GBP = " + converted + " USD");
    }

    @Test
    public void testCurrencyServiceGetExchangeRate() {
        // Test getExchangeRate method
        String formula = "currencyService.getExchangeRate('JPY', 'EUR')";

        FormulaResult result = engine.evaluate(formula);

        assertTrue("Formula should execute successfully", result.isSuccess());
        assertNotNull("Result should not be null", result.getValue());

        double rate = ((Number) result.getValue()).doubleValue();
        assertTrue("Exchange rate should be positive", rate > 0);

        System.out.println("JPY to EUR rate: " + rate);
    }

    @Test
    public void testComplexCurrencyFormula() {
        // Test a complex formula using currencyService
        FormulaContext context = new FormulaContext()
            .setVariable("eurPrice", 100.0)
            .setVariable("gbpPrice", 80.0)
            .setVariable("qty1", 3)
            .setVariable("qty2", 2);

        String formula = """
            def eurTotal = currencyService.convertToUSD('EUR', eurPrice * qty1)
            def gbpTotal = currencyService.convertToUSD('GBP', gbpPrice * qty2)
            eurTotal + gbpTotal
        """;

        FormulaResult result = engine.evaluate(formula, context);

        assertTrue("Formula should execute successfully", result.isSuccess());
        assertNotNull("Result should not be null", result.getValue());

        double totalUsd = ((Number) result.getValue()).doubleValue();
        assertTrue("Total should be positive", totalUsd > 0);

        System.out.println("Total in USD: " + totalUsd);
    }
}
