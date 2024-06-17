package org.example.LRParser;

import org.example.LRParser.LR1.LR1Grammar;
import org.example.LRParser.LR1.LR1GrammarAnalyzer;

import java.util.*;

public class Java_LRParserAnalysis {

    private static final List<String> productions = Arrays.asList(
            "program' -> program",
            "program -> compoundstmt",
            "stmt -> ifstmt | whilestmt | assgstmt | compoundstmt",
            "compoundstmt -> { stmts }",
            "stmts -> stmt stmts | E",
            "ifstmt -> if ( boolexpr ) then stmt else stmt",
            "whilestmt -> while ( boolexpr ) stmt",
            "assgstmt -> ID = arithexpr ;",
            "boolexpr -> arithexpr boolop arithexpr",
            "boolop -> < | > | <= | >= | ==",
            "arithexpr -> multexpr arithexprprime",
            "arithexprprime -> + multexpr arithexprprime | - multexpr arithexprprime | E",
            "multexpr -> simpleexpr multexprprime",
            "multexprprime -> * simpleexpr multexprprime | / simpleexpr multexprprime | E",
            "simpleexpr -> ID | NUM | ( arithexpr )"
    );

    private static final List<String> terminals = Arrays.asList(
            "{", "}", "if", "(", ")", "then", "else", "while", "ID", "=", ">", "<", ">=", "<=", "==", "+", "-", "*", "/", "NUM", "E", ";", "$"
    );

    private static StringBuffer prog = new StringBuffer();
    private static final LR1GrammarAnalyzer lr = new LR1GrammarAnalyzer(productions, terminals);

    private static Stack<List<String>> ans = new Stack<>();

    private static List<List<String>> tokens = new ArrayList<>();

    private static int status = 0;

    /**
     * 读取输入的程序
     */
    private static void read_prog() {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            prog.append(sc.nextLine().trim()).append("\n");
        }
    }

    /**
     * 将输入的程序分割成token
     */
    private static void tokenize() {
        // 记录每一个token所在的行号
        int line = 1;
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < prog.length(); i++) {
            char ch = prog.charAt(i);

            if (ch == '\n') {
                if (token.length()>0) {
                    tokens.add(Arrays.asList(token.toString(), String.valueOf(line)));
                    token.setLength(0);
                }
                line++;
            } else if (ch == ' ' || ch == '\t') {
                if (!token.isEmpty()) {
                    tokens.add(Arrays.asList(token.toString(), String.valueOf(line)));
                    token.setLength(0);
                }
            } else {
                token.append(ch);
            }
        }

        // Add the last token if there is any
        if (!token.isEmpty()) {
            tokens.add(Arrays.asList(token.toString(), String.valueOf(line)));
        }
        tokens.add(Arrays.asList("$", String.valueOf(++line)));
    }

    /**
     * 语法分析，主体函数
     */
    private static void analysis() {
            read_prog();
            tokenize();
            parse();
            if(status==0) printAns();
    }

    /**
     * 解析
     */
    public static void parse(){
        try{
            Stack<Integer> stateStack = new Stack<>();
            Stack<String> symbolStack = new Stack<>();
            stateStack.push(0);
            int index = 0;
            ArrayList<String> tokenList = new ArrayList<>();
            for (List<String> tk : tokens) {
                tokenList.add(tk.get(0));
            }
            ans = new Stack<>();
            ans.add(tokenList.subList(index, tokenList.size()-1));
            while (index < tokens.size()) {
                Integer state = stateStack.peek();
                String token = tokens.get(index).get(0);
                Object action = lr.getActionTable().get(state).get(token);
                if (action == null) {
                    if(!token.equals(";")&& Objects.equals(symbolStack.peek(), "NUM")){
                        // 缺少分号
                        System.out.println("语法错误，第" + (Integer.parseInt(tokens.get(index).get(1))-1) + "行，缺少\";\"");

                        tokens.add(index, Arrays.asList(";", tokens.get(index).get(1)));
                        throw new Exception("Syntax error");
                    }else {
                        // 语法错误
                        System.out.println("语法错误，第" + (Integer.parseInt(tokens.get(index).get(1))-1) + "行，" + token + "不符合语法规则");
                        throw new Exception("other error");
                    }
                }

                if(action instanceof String){
                    String tmp = (String) action;
                    if (tmp.startsWith("S")) {  // Shift
                        int newState = Integer.parseInt(tmp.substring(1));
                        stateStack.push(newState);
                        symbolStack.push(token);
                        index++;
                    }else if (tmp.equals("Accept")) {
                        System.out.println("Parsing completed successfully.");
                        break;
                    }else {
                        System.out.println("Unexpected error");
                        break;
                    }
                }else if(action instanceof LR1Grammar){
                    LR1Grammar tmp = (LR1Grammar) action;
                    String[] production = tmp.getRightWord().toArray(new String[0]);
                    if(!tmp.getRightWord().get(0).equals("E")){
                        for (int i = 0; i < production.length; i++) {
                            symbolStack.pop();
                            stateStack.pop();
                        }
                    }
                    symbolStack.push(tmp.getLeftWord());
                    state = stateStack.peek();
                    stateStack.push(lr.getGotoTable().get(state).get(symbolStack.peek()));
                    // ansStack 中插入symbolStack+tokens.subList(index, tokens.size()-1)
                    List<String> t = new ArrayList<>(symbolStack);
                    t.addAll(tokenList.subList(index, tokenList.size()-1));
                    ans.push(t);
                } else {
                    System.out.println("Unexpected error");
                    break;
                }
            }
        } catch (Exception e) {
            // 异常处理
            if(e.getMessage().equals("Syntax error")){
                parse();
            }else{
                System.out.println("Parsing failed.");
                status = 1;
            }
        }

    }

    /**
     * 打印结果
     */
    public static void printAns(){
        while(!ans.isEmpty()){
            List<String> t = ans.pop();
            for(String s : t){
                System.out.print(s + " ");
            }
            if(ans.size() != 0){
                System.out.print("=> \n");
            }
        }

    }

    public static void main(String[] args) {
        analysis();
    }
}
