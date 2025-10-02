package com.provectus.formula.functions;

public class MathFunctions {
    
    public static double square(double x) {
        return x * x;
    }
    
    public static double cube(double x) {
        return x * x * x;
    }
    
    public static double percentage(double value, double percent) {
        return (value * percent) / 100.0;
    }
    
    public static double discount(double price, double discountPercent) {
        return price - percentage(price, discountPercent);
    }
    
    public static double compound(double principal, double rate, int years) {
        return principal * Math.pow(1 + rate / 100, years);
    }
}