package org.example.lexical;

import java.io.InputStream;
import java.util.*;

public class Java_LexAnalysis
{
    enum TokenType
    {
        START,
        ERR,
        KEYWORD,
        IDENTIFIER,
        COMMENT,
        COMMENT_STAR,
        COMMENT_END,
        NUM,
        NUM_POINT,
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
        OP_XOR,
        OP_XOR_EQUAL,
        OP_AND,
        OP_AND_AND,
        OP_AND_EQUAL,
        OP_OR,
        OP_OR_OR,
        OP_OR_EQUAL,
        OP_EXCLAMATION,
        OP_EXCLAMATION_EQUAL,
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
        OP_COMMA,
        OP_SEMICOLON,
        OP_DOT,
        OP_COLON,
        OP_QUESTION,
        OP_TILDE,
        OP_QUOTATION,
        OP_SINGLE_QUOTATION,
    }

    enum BracketType
    {
        START,
        LEFT_BRACKET,
        RIGHT_BRACKET,
        LEFT_BRACE,
        RIGHT_BRACE,
        LEFT_PARENTHESIS,
        RIGHT_PARENTHESIS,
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
        prog.append("12345.2234");
        prog.append("int main");
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
        BracketType currentBracket = BracketType.START;
        while (currentIndex < n) {
            char c = prog.charAt(currentIndex);
            switch (c){
                case '.':
                    if(currentType == TokenType.START||currentType == TokenType.NUM){
                        currentType = TokenType.NUM_POINT;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }else{
                        currentType = TokenType.ERR;
                    }
                    break;
                case '+':
                    if(currentType == TokenType.START){
                        currentType = TokenType.OP_PLUS;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_PLUS){
                        currentType = TokenType.OP_PLUS_PLUS;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }else {
                        currentType = TokenType.ERR;
                    }
                    break;
                case '-':
                    if(currentType == TokenType.START){
                        currentType = TokenType.OP_MINUS;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_MINUS){
                        currentType = TokenType.OP_MINUS_MINUS;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }else {
                        currentType = TokenType.ERR;
                    }
                    break;
                case '*':
                    if(currentType == TokenType.START){
                        currentType = TokenType.OP_MULTIPLY;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_DIVIDE){
                        currentType = TokenType.COMMENT;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT||currentType == TokenType.COMMENT_STAR){
                        currentType = TokenType.COMMENT_STAR;
                        stack.append(c);
                    }else {
                        currentType = TokenType.ERR;
                    }
                    break;
                case '/':
                    if(currentType == TokenType.START){
                        currentType = TokenType.OP_DIVIDE;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR) {
                        currentType = TokenType.COMMENT_END;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else {
                        currentType = TokenType.ERR;
                    }
                    break;
                case '%':
                    if(currentType == TokenType.START){
                        currentType = TokenType.OP_MOD;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }else {
                        currentType = TokenType.ERR;
                    }
                    break;
                case '^':
                    if(currentType == TokenType.START){
                        currentType = TokenType.OP_XOR;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }else {
                        currentType = TokenType.ERR;
                    }
                    break;
                case '&':
                    if(currentType == TokenType.START){
                        currentType = TokenType.OP_AND;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_AND){
                        currentType = TokenType.OP_AND_AND;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }else {
                        currentType = TokenType.ERR;
                    }
                    break;
                case '|':
                    if(currentType == TokenType.START){
                        currentType = TokenType.OP_OR;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_OR){
                        currentType = TokenType.OP_OR_OR;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }else{
                        currentType = TokenType.ERR;
                    }
                    break;
                case '!':
                    if(currentType == TokenType.START){
                        currentType = TokenType.OP_EXCLAMATION;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }else{
                        currentType = TokenType.ERR;
                    }
                    break;
                case '>':
                    if(currentType == TokenType.OP_MINUS){
                        currentType = TokenType.OP_MINUS_GREATER;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }else{
                        currentType = TokenType.ERR;
                    }
                    break;
                case '=':
                    if(currentType == TokenType.OP_DIVIDE){
                        currentType = TokenType.OP_DIVIDE_EQUAL;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_MINUS){
                        currentType = TokenType.OP_MINUS_EQUAL;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_PLUS){
                        currentType = TokenType.OP_PLUS_EQUAL;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_MULTIPLY){
                        currentType = TokenType.OP_MULTIPLY_EQUAL;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_MOD){
                        currentType = TokenType.OP_MOD_EQUAL;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_AND){
                        currentType = TokenType.OP_AND_EQUAL;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_OR){
                        currentType = TokenType.OP_OR_EQUAL;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_EXCLAMATION){
                        currentType = TokenType.OP_EXCLAMATION_EQUAL;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_LESS){
                        currentType = TokenType.OP_LESS_EQUAL;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_GREATER){
                        currentType = TokenType.OP_GREATER_EQUAL;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_EQUAL){
                        currentType = TokenType.OP_EQUAL_EQUAL;
                        stack.append(c);
                    }else if(currentType == TokenType.OP_XOR){
                        currentType = TokenType.OP_XOR_EQUAL;
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }else {
                        currentType = TokenType.ERR;
                    }
                    break;
                case '0':case '1':case '2':case '3':case '4':case '5':case '6':case '7':case '8':case '9':
                    if(currentType == TokenType.START){
                        currentType = TokenType.NUM;
                        stack.append(c);
                    }else if(currentType == TokenType.NUM){
                        stack.append(c);
                    }else if(currentType == TokenType.NUM_POINT){
                        stack.append(c);
                    }else if(currentType == TokenType.IDENTIFIER){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }else {
                        currentType = TokenType.ERR;
                    }
                    break;
                case 'a':case 'b':case 'c':case 'd':case 'e':case 'f':case 'g':case 'h':case 'i':case 'j':case 'k':case 'l':case 'm':case 'n':case 'o':case 'p':case 'q':case 'r':case 's':case 't':case 'u':case 'v':case 'w':case 'x':case 'y':case 'z':
                case 'A':case 'B':case 'C':case 'D':case 'E':case 'F':case 'G':case 'H':case 'I':case 'J':case 'K':case 'L':case 'M':case 'N':case 'O':case 'P':case 'Q':case 'R':case 'S':case 'T':case 'U':case 'V':case 'W':case 'X':case 'Y':case 'Z':
                case '_':
                    if(currentType == TokenType.START){
                        currentType = TokenType.IDENTIFIER;
                        stack.append(c);
                    }else if(currentType == TokenType.IDENTIFIER){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT){
                        stack.append(c);
                    }else if(currentType == TokenType.COMMENT_STAR){
                        stack.append(c);
                        currentType = TokenType.COMMENT;
                    }else{
                        currentType = TokenType.ERR;
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
                    }else{
                        currentType = TokenType.ERR;
                    }
            }
            currentIndex++;
            if(currentType==TokenType.NUM||currentType==TokenType.NUM_POINT){
                if(currentIndex<n){
                    char next = prog.charAt(currentIndex);
                    if((next>='0'&&next<='9')||next=='.'){
                        continue;
                    }
                }
                tokens.add(stack.toString());
                stack = new StringBuilder();
                currentType = TokenType.START;
            }else if(currentType==TokenType.COMMENT_END){
                tokens.add("/*");
                tokens.add(stack.substring(2,stack.length()-2));
                tokens.add("*/");
                stack = new StringBuilder();
                currentType = TokenType.START;
            }else if(currentType==TokenType.IDENTIFIER){
                if(currentIndex<n){
                    char next = prog.charAt(currentIndex);
                    if(next>='a'&&next<='z'||next>='A'&&next<='Z'||next>='0'&&next<='9'||next=='_'){
                        continue;
                    }
                }
                tokens.add(stack.toString());
                stack = new StringBuilder();
                currentType = TokenType.START;
            }

        }

    }

    private static void printTokens() {
        for (String token : tokens) {
//            System.out.println(token);
            if(map.containsKey(token)) {
                System.out.println("<"+token + "," + map.get(token)+">");
            } else {
                if(token.matches("[0-9.]+"))
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
//        System.out.println(tokens.size());
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