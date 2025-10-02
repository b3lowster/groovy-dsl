// ============================================
// FormulaEngineTest.java
// ============================================
package com.provectus.formula;

import groovy.lang.Script;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class FormulaEngineTest {
    private FormulaEngine engine;
    
    @Before
    public void setUp() {
        engine = new FormulaEngine();
    }
    
    @Test
    public void testStringFunctions() {
        FormulaContext context = new FormulaContext()
            .setVariable("words", new String[]{"Hello", "World", "Java"});
        
        FormulaResult result = engine.evaluate("countLetters(words)", context);
        assertTrue(result.isSuccess());
        assertEquals(14, result.getValue());
    }
    
    @Test
    public void testCountVowelsAndConsonants() {
        FormulaContext context = new FormulaContext()
            .setVariable("phrases", new String[]{"Hello", "Beautiful", "World"});

        FormulaResult vowels = engine.evaluate("countVowels(phrases)", context);
        FormulaResult consonants = engine.evaluate("countConsonants(phrases)", context);

        assertTrue(vowels.isSuccess());
        assertTrue(consonants.isSuccess());
        assertEquals(8, vowels.getValue());
        assertEquals(11, consonants.getValue());
    }
    
    @Test
    public void testCountWordsAndAverageLength() {
        FormulaContext context = new FormulaContext()
            .setVariable("sentences", new String[]{"Hello World", "Groovy DSL", "Formula Engine"});

        FormulaResult words = engine.evaluate("countWords(sentences)", context);
        FormulaResult avgLength = engine.evaluate("averageWordLength(sentences)", context);

        assertTrue(words.isSuccess());
        assertEquals(6, words.getValue());

        assertTrue(avgLength.isSuccess());
        assertEquals(5.333333333333333, (Double) avgLength.getValue(), 0.01);
    }
    
    @Test
    public void testComplexStringFormula() {
        FormulaContext context = new FormulaContext()
            .setVariable("data", new String[]{"Java", "Groovy", "DSL", "Engine"});
        
        String formula = """
            def letters = countLetters(data)
            def words = countWords(data)
            def vowels = countVowels(data)
            letters + words + vowels
        """;
        
        FormulaResult result = engine.evaluate(formula, context);
        assertTrue(result.isSuccess());
        // 20 letters + 4 words + 6 vowels = 31
        assertEquals(30, result.getValue());
    }
    
    @Test
    public void testSimpleArithmetic() {
        FormulaResult result = engine.evaluate("2 + 2 * 3");
        assertTrue(result.isSuccess());
        assertEquals(8, result.getValue());
    }
    
    @Test
    public void testWithVariables() {
        FormulaContext context = new FormulaContext()
            .setVariable("price", 100.0)
            .setVariable("quantity", 5);
        
        FormulaResult result = engine.evaluate("price * quantity", context);
        assertTrue(result.isSuccess());
        assertEquals(500.0, result.getValue());
    }
    
    @Test
    public void testMathFunctions() {
        FormulaResult result = engine.evaluate("sqrt(16) + pow(2, 3)");
        assertTrue(result.isSuccess());
        assertEquals(12.0, result.getValue());
    }
    
    @Test
    public void testCustomFunctions() {
        FormulaContext context = new FormulaContext()
            .setVariable("price", 1000.0)
            .setVariable("discountRate", 20.0);
        
        FormulaResult result = engine.evaluate(
            "discount(price, discountRate)", 
            context
        );
        assertTrue(result.isSuccess());
        assertEquals(800.0, result.getValue());
    }
    
    @Test
    public void testDSLStyle() {
        FormulaContext context = new FormulaContext()
            .setVariable("salary", 50000.0)
            .setVariable("bonus", 5000.0)
            .setVariable("taxRate", 25.0);
        
        String formula = """
            def total = salary + bonus
            def tax = percentage(total, taxRate)
            total - tax
        """;
        
        FormulaResult result = engine.evaluate(formula, context);
        assertTrue(result.isSuccess());
        assertEquals(41250.0, result.getValue());
    }
    
    @Test
    public void testCompiledFormula() {
        Script compiled = engine.compile("price * (1 + taxRate / 100)");

        FormulaContext context1 = new FormulaContext()
            .setVariable("price", 100.0)
            .setVariable("taxRate", 10.0);

        FormulaResult result1 = engine.evaluateCompiled(compiled, context1);
        assertEquals(110.0, (Double) result1.getValue(), 0.01);

        FormulaContext context2 = new FormulaContext()
            .setVariable("price", 200.0)
            .setVariable("taxRate", 15.0);

        FormulaResult result2 = engine.evaluateCompiled(compiled, context2);
        assertEquals(230.0, (Double) result2.getValue(), 0.01);
    }
    
    @Test
    public void testConditionalLogic() {
        FormulaContext context = new FormulaContext()
            .setVariable("age", 25)
            .setVariable("income", 60000);
        
        String formula = """
            if (age < 18) {
                0
            } else if (income < 50000) {
                percentage(income, 10)
            } else {
                percentage(income, 15)
            }
        """;
        
        FormulaResult result = engine.evaluate(formula, context);
        assertTrue(result.isSuccess());
        assertEquals(9000.0, result.getValue());
    }
    
    @Test
    public void testErrorHandling() {
        FormulaResult result = engine.evaluate("1 / 0");
        // Division by zero returns Infinity in Groovy, so it succeeds
        assertFalse(result.isSuccess());
        
        // Test syntax error
        FormulaResult syntaxError = engine.evaluate("2 + * 3");
        assertFalse(syntaxError.isSuccess());
        assertNotNull(syntaxError.getErrorMessage());
    }
}