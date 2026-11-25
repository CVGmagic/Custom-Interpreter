package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
    final Environment enclosing; // The enclosing environment (for scopes)
    private final Map<String, Object> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    void define(String name, Object value) {
        // Define a new variable
        values.put(name, value);
    }

    Object get(Token name) {
        // Get the value of a variable
        if (values.containsKey(name.lexeme)) {
        return values.get(name.lexeme);
        }

        // Try to get the variable from the outer scope
        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name,
            "Undefined variable '" + name.lexeme + "'.");
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
        return;
        }
        
        // Assign to outer scope
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name,
            "Undefined variable '" + name.lexeme + "'.");
    }

}