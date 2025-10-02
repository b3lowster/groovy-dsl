// ============================================
// FormulaEngineApplication.java
// ============================================
package com.provectus.formula;

import com.provectus.formula.functions.CurrencyFunctions;
import com.provectus.formula.service.CurrencyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FormulaEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormulaEngineApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(FormulaEngine engine, CurrencyService currencyService) {
        return args -> {
            // Initialize CurrencyFunctions with the Spring-managed CurrencyService
            CurrencyFunctions.setCurrencyService(currencyService);

            if (args.length == 0) {
                printUsage();
                return;
            }

            String command = args[0];

            if ("--help".equals(command) || "-h".equals(command)) {
                printUsage();
                return;
            }

            if ("--eval".equals(command) || "-e".equals(command)) {
                if (args.length < 2) {
                    System.err.println("Error: Formula required for --eval");
                    printUsage();
                    return;
                }

                String formula = args[1];
                evaluateFormula(engine, formula, args);
            } else if ("--convert".equals(command) || "-c".equals(command)) {
                if (args.length < 4) {
                    System.err.println("Error: --convert requires <from> <to> <amount>");
                    printUsage();
                    return;
                }

                String from = args[1];
                String to = args[2];
                double amount = Double.parseDouble(args[3]);

                convertCurrency(currencyService, from, to, amount);
            } else if ("--convert-usd".equals(command) || "-u".equals(command)) {
                if (args.length < 3) {
                    System.err.println("Error: --convert-usd requires <from> <amount>");
                    printUsage();
                    return;
                }

                String from = args[1];
                double amount = Double.parseDouble(args[2]);

                convertToUSD(currencyService, from, amount);
            } else {
                // Treat entire argument as formula
                String formula = String.join(" ", args);
                evaluateFormula(engine, formula, new String[0]);
            }
        };
    }

    private void evaluateFormula(FormulaEngine engine, String formula, String[] args) {
        try {
            FormulaContext context = new FormulaContext();

            // Parse additional variables from args (format: var=value)
            for (int i = 2; i < args.length; i++) {
                String arg = args[i];
                if (arg.contains("=")) {
                    String[] parts = arg.split("=", 2);
                    String key = parts[0];
                    String value = parts[1];

                    // Try to parse as number, otherwise use as string
                    try {
                        if (value.contains(".")) {
                            context.setVariable(key, Double.parseDouble(value));
                        } else {
                            context.setVariable(key, Integer.parseInt(value));
                        }
                    } catch (NumberFormatException e) {
                        context.setVariable(key, value);
                    }
                }
            }

            FormulaResult result = engine.evaluate(formula, context);

            if (result.isSuccess()) {
                System.out.println("Result: " + result.getValue());
            } else {
                System.err.println("Error: " + result.getErrorMessage());
            }
        } catch (Exception e) {
            System.err.println("Error evaluating formula: " + e.getMessage());
        }
    }

    private static void convertToUSD(CurrencyService service, String from, double amount) {
        try {
            double result = service.convertToUSD(from, amount);
            System.out.printf("%.2f %s = %.2f USD%n", amount, from.toUpperCase(), result);
        } catch (Exception e) {
            System.err.println("Error converting to USD: " + e.getMessage());
        }
    }

    private static void convertCurrency(CurrencyService service, String from, String to, double amount) {
        try {
            double result = service.convert(from, to, amount);
            System.out.printf("%.2f %s = %.2f %s%n", amount, from.toUpperCase(), result, to.toUpperCase());
        } catch (Exception e) {
            System.err.println("Error converting currency: " + e.getMessage());
        }
    }

    private void printUsage() {
        System.out.println("Formula Engine CLI - Usage:");
        System.out.println();
        System.out.println("Evaluate a formula:");
        System.out.println("  java -jar formula-engine.jar --eval \"<formula>\" [var=value ...]");
        System.out.println("  java -jar formula-engine.jar -e \"<formula>\" [var=value ...]");
        System.out.println();
        System.out.println("Convert currency:");
        System.out.println("  java -jar formula-engine.jar --convert <from> <to> <amount>");
        System.out.println("  java -jar formula-engine.jar -c EUR USD 100");
        System.out.println();
        System.out.println("Convert to USD:");
        System.out.println("  java -jar formula-engine.jar --convert-usd <from> <amount>");
        System.out.println("  java -jar formula-engine.jar -u EUR 100");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar formula-engine.jar --eval \"2 + 2 * 3\"");
        System.out.println("  java -jar formula-engine.jar --eval \"price * quantity\" price=10.5 quantity=3");
        System.out.println("  java -jar formula-engine.jar --eval \"convertCurrency('EUR', 'USD', 100)\"");
        System.out.println("  java -jar formula-engine.jar --eval \"convertToUSD('EUR', 100)\"");
        System.out.println("  java -jar formula-engine.jar --convert EUR USD 100");
        System.out.println("  java -jar formula-engine.jar --convert-usd EUR 100");
        System.out.println();
        System.out.println("Available functions:");
        System.out.println("  Math: sqrt, pow, abs, sin, cos, etc.");
        System.out.println("  String: countLetters, countWords, countVowels, etc.");
        System.out.println("  Currency: convertCurrency, convertToUSD, exchangeRate");
    }
}
