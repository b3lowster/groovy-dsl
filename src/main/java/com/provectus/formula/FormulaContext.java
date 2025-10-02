package com.provectus.formula;

import java.util.HashMap;
import java.util.Map;

public class FormulaContext {
    private final Map<String, Object> variables;
    
    public FormulaContext() {
        this.variables = new HashMap<>();
    }
    
    public FormulaContext setVariable(String name, Object value) {
        variables.put(name, value);
        return this;
    }
    
    public Object getVariable(String name) {
        return variables.get(name);
    }
    
    public Map<String, Object> getVariables() {
        return new HashMap<>(variables);
    }
    
    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }
}