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

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing; 
        }

        return environment;
    }

    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
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