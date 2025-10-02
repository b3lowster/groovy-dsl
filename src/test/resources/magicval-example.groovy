// ============================================
// magicval-example.groovy
// ============================================
// This script demonstrates using the magicval variable from application.properties
// The magicval variable is automatically injected and available in the binding

// Simple usage - multiply by magic value
def result1 = 10 * magicval
println "10 * magicval = ${result1}"

// Use magicval in calculations
def result2 = square(magicval) + cube(3)
println "square(magicval) + cube(3) = ${result2}"

// Apply discount using magicval as percentage
def result3 = discount(1000, magicval)
println "discount(1000, magicval) = ${result3}"

// Combine with context variables
// Assuming these variables are set in the context:
// price = 100, quantity = 2
if (binding.hasVariable("price") && binding.hasVariable("quantity")) {
    def total = price * quantity
    def withMagic = total + magicval
    println "${quantity} items at ${price} each + magicval = ${withMagic}"
    withMagic
} else {
    // Return simple calculation if no context variables
    result1
}
