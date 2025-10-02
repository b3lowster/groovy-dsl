// ============================================
// FormulaEngine.java
// ============================================
package com.provectus.formula;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.util.Map;

public class FormulaEngine {
    private final GroovyShell shell;
    private final CompilerConfiguration config;

    public FormulaEngine() {
        config = new CompilerConfiguration();
        
        // Add automatic imports
        ImportCustomizer imports = new ImportCustomizer();
        imports.addStaticStars("java.lang.Math");
        imports.addStaticStars("com.provectus.formula.functions.MathFunctions");
        imports.addStaticStars("com.provectus.formula.functions.StringFunctions");
        imports.addStaticStars("com.provectus.formula.functions.CurrencyFunctions");
        config.addCompilationCustomizers(imports);
        
        this.shell = new GroovyShell(config);
    }

    public FormulaResult evaluate(String formula, FormulaContext context) {
        try {
            Binding binding = new Binding();

            // Add all context variables to the binding
            for (Map.Entry<String, Object> entry : context.getVariables().entrySet()) {
                binding.setVariable(entry.getKey(), entry.getValue());
            }

            // Create a new shell with the binding
            GroovyShell boundShell = new GroovyShell(binding, config);

            Object result = boundShell.evaluate(formula);

            return FormulaResult.success(result);
        } catch (Exception e) {
            return FormulaResult.error(e.getMessage());
        }
    }
    
    public FormulaResult evaluate(String formula) {
        return evaluate(formula, new FormulaContext());
    }
    
    // Compile formula for reuse
    public Script compile(String formula) {
        return shell.parse(formula);
    }
    
    public FormulaResult evaluateCompiled(Script script, FormulaContext context) {
        try {
            Binding binding = new Binding();
            
            for (Map.Entry<String, Object> entry : context.getVariables().entrySet()) {
                binding.setVariable(entry.getKey(), entry.getValue());
            }
            
            script.setBinding(binding);
            Object result = script.run();
            
            return FormulaResult.success(result);
        } catch (Exception e) {
            return FormulaResult.error(e.getMessage());
        }
    }
}