package org.example.lexical;

import java.io.InputStream;
import java.util.*;

public class Java_LexAnalysis
{
    enum TokenType
    {
        START,
        ERR,
        ACC,
        KEYWORD,
        IDENTIFIER,
        COMMENT,
        COMMENT_STAR,
        NUM,
        OP_MINUS,
        OP_MINUS_MINUS,
        OP_MINUS_EQUAL,
        OP_MINUS_GREATER,
        OP_PLUS,
        OP_PLUS_PLUS,
        OP_PLUS_EQUAL,
        OP_MULTIPLY,
        OP_MULTIPLY_EQUAL,
        OP_DIVIDE,
        OP_DIVIDE_EQUAL,
        OP_MOD,
        OP_MOD_EQUAL,
        OP_AND,
        OP_AND_AND,
        OP_AND_EQUAL,
        OP_OR,
        OP_OR_OR,
        OP_OR_EQUAL,
        OP_EXCLAMATION,
        OP_EXCLAMATION_EQUAL,
        OP_LEFT_BRACKET,
        OP_RIGHT_BRACKET,
        OP_LEFT_BRACE,
        OP_RIGHT_BRACE,
        OP_LEFT_PARENTHESIS,
        OP_RIGHT_PARENTHESIS,
        OP_LESS,
        OP_LESS_EQUAL,
        OP_LESS_LESS,
        OP_LESS_LESS_EQUAL,
        OP_GREATER,
        OP_GREATER_EQUAL,
        OP_GREATER_GREATER,
        OP_GREATER_GREATER_EQUAL,
        OP_EQUAL,
        OP_EQUAL_EQUAL,
        OP_XOR,
        OP_XOR_EQUAL,
        OP_COMMA,
        OP_SEMICOLON,
        OP_DOT,
        OP_COLON,
        OP_QUESTION,
        OP_TILDE,
        OP_QUOTATION,
        OP_SINGLE_QUOTATION,
    }

    private static StringBuffer prog = new StringBuffer();
    private static Map<String,Integer> map = new HashMap<>();
    private static List<String> tokens = new ArrayList<>();

    /**
     * This method is to read the standard input
     */
    private static void read_prog()
    {
        // Scanner sc = new Scanner(System.in);
        // while(sc.hasNextLine())
        // {
        //     prog.append(sc.nextLine());
        // }
        prog.append("/*HelloWorld!*!asjldjgaer;rgvjea*/");
    }

    // add your method here!!
    /**
     * This method is to read the c_keys.txt file in the same directory
     */
    private static void read_c_keys()
    {
        // Read file
        InputStream inputStream = Java_LexAnalysis.class.getResourceAsStream("/org/example/lexical/c_keys.txt");
        if (inputStream == null) {
            System.err.println("File not found: c_keys.txt");
            return;
        }

        Scanner sc = new Scanner(inputStream);
        while(sc.hasNextLine())
        {
            String line = sc.nextLine();
            String[] words = line.split(" {4}");
            // 如果words[0]包含中文字符，跳过
            if (words[0].matches(".*[\\u4E00-\\u9FA5]+.*")) {
                continue;
            }
            map.put(words[0], Integer.parseInt(words[1]));
        }
        map.put("/*",79);
        map.put("*/",79);
        sc.close();
        System.out.println("Loaded " + map.size() + " keys from c_keys.txt");
    }

    /**
     * This method is to analyze the program
     */
    private static void analyzeProg()
    {
        int currentIndex = 0;
        int n = prog.length();
        StringBuilder stack = new StringBuilder();
        TokenType currentType = TokenType.START;
        while (currentIndex < n) {
            char c = prog.charAt(currentIndex);
            switch (c){
                case '/':
                    if(currentType == TokenType.START){
                        currentType = TokenType.OP_DIVIDE;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR) {
                        currentType = TokenType.ACC;
                        stack.append(c);
                    }
                    break;
                case '*':
                    if(currentType == TokenType.OP_DIVIDE){
                        currentType = TokenType.COMMENT;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        currentType = TokenType.COMMENT_STAR;
                        stack.append(c);
                    }
                    break;
                default:
                    if(currentType == TokenType.OP_DIVIDE){
                        currentType = TokenType.ERR;
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }
            }
            if(currentType == TokenType.ACC){
                String tmp = stack.toString();
                if(tmp.startsWith("/*") && tmp.endsWith("*/")) {
                    tokens.add("/*");
                    tokens.add(tmp.substring(2, tmp.length() - 2));
                    tokens.add("*/");
                }
                stack = new StringBuilder();
                currentType = TokenType.START;
            }
            currentIndex++;
        }

    }

    private static void printTokens() {
        for (String token : tokens) {
            if(map.containsKey(token)) {
                System.out.println("<"+token + "," + map.get(token)+">");
            } else {
                if(token.matches("[0-9]+"))
                    System.out.println("<"+token + ",80>");
                else System.out.println("<"+token + ",81>");
            }
        }
    }


    /**
     * You should add some code in this method to achieve this lab
     */
    private static void analysis()
    {
        read_prog();
        read_c_keys();
        analyzeProg();
        System.out.println(tokens.size());
        printTokens();
    }

    /**
     * This is the main method
     * @param args
     */
    public static void main(String[] args) {
        analysis();
    }
}