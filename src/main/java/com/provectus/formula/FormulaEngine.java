// ============================================
// FormulaEngine.java
// ============================================
package com.provectus.formula;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.provectus.formula.service.CurrencyService;
import com.provectus.formula.repository.UserRepository;

import java.util.Map;

@Component
public class FormulaEngine {
    private final GroovyShell shell;
    private final CompilerConfiguration config;
    private final CurrencyService currencyService;

    @Autowired(required = false)
    private UserRepository userRepository;

    @Value("${magicval}")
    private Integer magicval;

    public FormulaEngine(CurrencyService currencyService) {
        this.currencyService = currencyService;
        config = new CompilerConfiguration();
        
        // Add automatic imports
        ImportCustomizer imports = new ImportCustomizer();
        imports.addStaticStars("java.lang.Math");
        imports.addStaticStars("com.provectus.formula.functions.MathFunctions");
        imports.addStaticStars("com.provectus.formula.functions.StringFunctions");
        imports.addStaticStars("com.provectus.formula.functions.CurrencyFunctions");
        imports.addStaticStars("org.apache.commons.lang3.StringUtils");
        imports.addStarImports("java.util.stream");
        config.addCompilationCustomizers(imports);
        
        this.shell = new GroovyShell(config);
    }

    public FormulaResult evaluate(String formula, FormulaContext context) {
        try {
            Binding binding = new Binding();

            // Add CurrencyService to the binding
            binding.setVariable("currencyService", currencyService);

            // Add UserRepository to the binding
            if (userRepository != null) {
                binding.setVariable("userRepository", userRepository);
            }

            // Add magicval to the binding
            binding.setVariable("magicval", magicval);

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

            // Add CurrencyService to the binding
            binding.setVariable("currencyService", currencyService);

            // Add UserRepository to the binding
            if (userRepository != null) {
                binding.setVariable("userRepository", userRepository);
            }

            // Add magicval to the binding
            binding.setVariable("magicval", magicval);

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