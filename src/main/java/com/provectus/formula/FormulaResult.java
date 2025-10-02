package com.provectus.formula;

public class FormulaResult {
    private final Object value;
    private final boolean success;
    private final String errorMessage;
    
    private FormulaResult(Object value, boolean success, String errorMessage) {
        this.value = value;
        this.success = success;
        this.errorMessage = errorMessage;
    }
    
    public static FormulaResult success(Object value) {
        return new FormulaResult(value, true, null);
    }
    
    public static FormulaResult error(String message) {
        return new FormulaResult(null, false, message);
    }
    
    public Object getValue() {
        return value;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    @Override
    public String toString() {
        if (success) {
            return "Success: " + value;
        } else {
            return "Error: " + errorMessage;
        }
    }
}