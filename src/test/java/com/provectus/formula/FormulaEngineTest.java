// ============================================
// FormulaEngineTest.java
// ============================================
package com.provectus.formula;

import groovy.lang.Script;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FormulaEngineTest {

    @Autowired
    private FormulaEngine engine;
    
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

    @Test
    public void testMagicvalVariable() {
        // Test simple usage of magicval from application.properties
        FormulaResult result = engine.evaluate("10 * magicval");
        assertTrue(result.isSuccess());
        assertEquals(420, result.getValue());
    }

    @Test
    public void testMagicvalInFormula() {
        // Test magicval in complex formula
        FormulaResult result = engine.evaluate("square(magicval) + cube(3)");
        assertTrue(result.isSuccess());
        // square(42) + cube(3) = 1764 + 27 = 1791
        assertEquals(1791.0d, result.getValue());
    }

    @Test
    public void testMagicvalWithContext() {
        FormulaContext context = new FormulaContext()
            .setVariable("price", 100.0)
            .setVariable("quantity", 2);

        FormulaResult result = engine.evaluate("price * quantity + magicval", context);
        assertTrue(result.isSuccess());
        // 100 * 2 + 42 = 242
        assertEquals(242.0, result.getValue());
    }

    @Test
    public void testMagicvalAsDiscountRate() {
        FormulaContext context = new FormulaContext()
            .setVariable("price", 1000.0);

        // Use magicval as discount percentage
        FormulaResult result = engine.evaluate("discount(price, magicval)", context);
        assertTrue(result.isSuccess());
        // discount(1000, 42) = 1000 - (1000 * 0.42) = 580
        assertEquals(580.0, result.getValue());
    }

    @Test
    public void testStringUtilsCapitalize() {
        FormulaContext context = new FormulaContext()
            .setVariable("text", "hello world");

        FormulaResult result = engine.evaluate("capitalize(text)", context);
        assertTrue(result.isSuccess());
        assertEquals("Hello world", result.getValue());
    }

    @Test
    public void testStringUtilsReverse() {
        FormulaResult result = engine.evaluate("reverse('hello')");
        assertTrue(result.isSuccess());
        assertEquals("olleh", result.getValue());
    }

    @Test
    public void testStringUtilsIsBlank() {
        FormulaContext context = new FormulaContext()
            .setVariable("empty", "")
            .setVariable("whitespace", "   ")
            .setVariable("text", "hello");

        FormulaResult emptyResult = engine.evaluate("isBlank(empty)", context);
        assertTrue(emptyResult.isSuccess());
        assertEquals(true, emptyResult.getValue());

        FormulaResult whitespaceResult = engine.evaluate("isBlank(whitespace)", context);
        assertTrue(whitespaceResult.isSuccess());
        assertEquals(true, whitespaceResult.getValue());

        FormulaResult textResult = engine.evaluate("isBlank(text)", context);
        assertTrue(textResult.isSuccess());
        assertEquals(false, textResult.getValue());
    }

    @Test
    public void testGroovyCollectionsApi() {
        String formula = """
            def numbers = [1, 2, 3, 4, 5]
            numbers.findAll { it > 2 }.sum()
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());
        assertEquals(12, result.getValue());
    }

    @Test
    public void testStreamApiWithFilter() {
        String formula = """
            [1, 2, 3, 4, 5].stream()
                .filter { it > 2 }
                .mapToInt { it }
                .sum()
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());
        assertEquals(12, result.getValue());
    }

    @Test
    public void testParallelStreamSum() {
        String formula = """
            [1, 2, 3, 4, 5].parallelStream()
                .filter { it > 2 }
                .mapToInt { it }
                .sum()
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());
        assertEquals(12, result.getValue());
    }

    @Test
    public void testParallelStreamWithCollectors() {
        String formula = """
            [1, 2, 3, 4, 5].parallelStream()
                .map { it * 2 }
                .collect(Collectors.toList())
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());
        assertEquals(java.util.Arrays.asList(2, 4, 6, 8, 10), result.getValue());
    }

    @Test
    public void testParallelStreamReduce() {
        String formula = """
            [1, 2, 3, 4, 5].parallelStream()
                .reduce(0, { a, b -> a + b })
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());
        assertEquals(15, result.getValue());
    }

    @Test
    public void testParallelStreamCount() {
        String formula = """
            [1, 2, 3, 4, 5, 6].parallelStream()
                .filter { it % 2 == 0 }
                .count()
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());
        assertEquals(3L, result.getValue());
    }

    @Test
    public void testParallelStreamWithStrings() {
        String formula = """
            ['hello', 'world', 'groovy'].parallelStream()
                .map { it.toUpperCase() }
                .collect(Collectors.joining(', '))
        """;

        FormulaResult result = engine.evaluate(formula);
        assertTrue(result.isSuccess());
        assertEquals("HELLO, WORLD, GROOVY", result.getValue());
    }

    @Test
    public void testStreamApiWithContext() {
        FormulaContext context = new FormulaContext()
            .setVariable("threshold", 10);

        String formula = """
            [5, 10, 15, 20, 25].parallelStream()
                .filter { it > threshold }
                .mapToInt { it }
                .average()
                .orElse(0)
        """;

        FormulaResult result = engine.evaluate(formula, context);
        assertTrue(result.isSuccess());
        assertEquals(20.0, (Double) result.getValue(), 0.01);
    }
}