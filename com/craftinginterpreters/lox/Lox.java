package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
// import java.util.Scanner;

public class Lox {
    private static final Interpreter interpreter = new Interpreter(); // Needs to be persistent, to keep track of global variables throughout sessions
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64); 
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) { 
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
                run(line);
                // Reset error state to false, to avoid killing session on error
                hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) return;

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        // Stop if there was a resolution error.
        if (hadError) return;

        interpreter.interpret(statements);
    }

    static void error(int line, String message) {
        report_error(line, "", message);
    }

    private static void report_error(int line, String where, String message) {
        // Reports an error
        System.err.println(
            "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    private static void report_warning(int line, String where, String message) {
        // Reports a warning
        System.err.println(
            "[line " + line + "] Warning" + where + ": " + message);
    }
    
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report_error(token.line, " at end", message);
        } else {
            report_error(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    static void warning(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report_warning(token.line, " at end", message);
        } else {
            report_warning(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    static void runtimeError(RuntimeError error) {

        System.err.println(error.getMessage() +
            "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
    
}
