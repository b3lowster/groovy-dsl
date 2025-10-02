# Groovy Formula Engine

A Groovy-based formula evaluation engine built with Spring Boot. Dynamically execute mathematical, string, and currency conversion formulas using Groovy DSL with custom function libraries.

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Build

```bash
mvn clean package
```

## Running the Application

### Basic Formula Evaluation

```bash
# Simple arithmetic
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "2 + 2 * 3"

# Using custom math functions
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "square(5) + cube(3)"

# Percentage and discount calculations
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "discount(100, 15)"
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "percentage(250, 20)"
```

### Using Variables

Pass variables as `key=value` pairs after the formula:

```bash
# Calculate total price
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "price * qty" price=10 qty=5

# Apply discount
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "discount(price, rate)" price=100 rate=15

# Complex formula
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "compound(principal, rate, years)" principal=1000 rate=5 years=10
```

### String Functions

```bash
# Count letters in strings
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "countLetters(['hello', 'world'])"

# Count vowels
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "countVowels(['Spring', 'Boot', 'Application'])"

# Average word length
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "averageWordLength(['Java', 'Groovy', 'Spring'])"
```

### Currency Conversion

```bash
# Convert between currencies
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --convert EUR USD 100

# Convert to USD (shorthand)
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --convert-usd EUR 100

# Use currency functions in formulas
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "convertCurrency('EUR', 'USD', 100)"
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "convertToUSD('GBP', 50)"
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "exchangeRate('EUR', 'USD')"
```

### Alternative: Using Maven

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--eval \"2 + 2 * 3\""
mvn spring-boot:run -Dspring-boot.run.arguments="--convert EUR USD 100"
```

## Available Functions

### Math Functions
- `square(x)` - Square of a number
- `cube(x)` - Cube of a number
- `percentage(value, percent)` - Calculate percentage of value
- `discount(price, percent)` - Apply discount to price
- `compound(principal, rate, years)` - Compound interest calculation
- All `java.lang.Math` functions: `sin()`, `cos()`, `sqrt()`, `pow()`, etc.

### String Functions
- `countLetters(strings)` - Count total letters in string array
- `countWords(strings)` - Count total words in string array
- `countVowels(strings)` - Count vowels in string array
- `countConsonants(strings)` - Count consonants in string array
- `averageWordLength(strings)` - Calculate average word length

### Currency Functions
- `convertCurrency(from, to, amount)` - Convert between currencies
- `convertToUSD(from, amount)` - Convert to USD
- `exchangeRate(from, to)` - Get exchange rate

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=FormulaEngineTest

# Run specific test method
mvn test -Dtest=FormulaEngineTest#testSimpleArithmetic
```

## Command-Line Options

- `--eval`, `-e` - Evaluate a formula (followed by formula string)
- `--convert`, `-c` - Convert currency (followed by: from to amount)
- `--convert-usd`, `-u` - Convert to USD (followed by: from amount)

Variables can be passed after the formula as `key=value` pairs.

## Examples

```bash
# Calculate loan payment
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "principal * (rate / 100) / 12" principal=10000 rate=5

# Apply multiple discounts
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "discount(discount(price, 10), 5)" price=100

# Convert and calculate
java -jar target/groovy-formula-engine-1.0-SNAPSHOT.jar --eval "convertCurrency('EUR', 'USD', price * qty)" price=50 qty=3
```

## Architecture

- **FormulaEngine** - Core evaluation engine using Groovy DSL
- **FormulaContext** - Variable storage for formula execution
- **FormulaResult** - Result wrapper with success/error states
- **Function Libraries** - Math, String, and Currency function collections
- **CurrencyService** - Live currency conversion via REST API

## License

This project is part of the Provectus investigation into Groovy DSL capabilities.
