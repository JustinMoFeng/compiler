package org.example.LLParser;

import java.util.*;

class TreeNode {
    String value;
    List<TreeNode> children;

    public TreeNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        this.children.add(child);
    }

    public void addChildFromHead(TreeNode child) {
        this.children.add(0,child);
    }
}

public class Java_LLParserAnalysis {
    private static StringBuffer prog = new StringBuffer();
    private static List<List<String>> tokens = new ArrayList<>();
    private static TreeNode syntaxTreeRoot;

    /**
     * this method is to read the standard input
     */
    private static void read_prog() {
        // Sample input for testing
//        Scanner sc = new Scanner(System.in);
//        while (sc.hasNextLine()) {
//            prog.append(sc.nextLine().trim()).append(" ");
//        }
        prog.append("{\n");
        prog.append("while ( ID == NUM )\n");
        prog.append("{\n");
        prog.append("ID = NUM\n");
        prog.append("}\n");
        prog.append("}");
    }

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
    }

    private static void parse(LLGrammerAnalyzer analyzer) {
        Stack<TreeNode> stack = new Stack<>();
        syntaxTreeRoot = new TreeNode("program");  // Assuming "program" is the start symbol
        stack.push(new TreeNode("$"));
        stack.push(syntaxTreeRoot);

        int index = 0;

        while (!stack.isEmpty()) {
            TreeNode topNode = stack.peek();
            String top = topNode.value;
            String currentToken = index < tokens.size() ? tokens.get(index).get(0) : "$";
            String currentLine = index < tokens.size() ? Integer.parseInt(tokens.get(index).get(1))-1 + "" : "-1";

            if (analyzer.getTerminals().contains(top) || top.equals("$")) {
//                System.out.println("top: " + top + ", currentToken: " + currentToken);
                if (top.equals(currentToken)) {
                    stack.pop();
                    index++;
                } else {
                    System.out.println("语法错误,第" + currentLine + "行,缺少" + currentToken);
                    return;
                }
            } else if (analyzer.getNonterminals().contains(top)) {
                String production = analyzer.getLLParseTable().get(top).get(currentToken);
//                System.out.println("top: " + top + ", currentToken: " + currentToken + ", production: " + production);
                if (production != null) {
                    stack.pop();
                    if (!production.equals("E")) {
                        String[] productionTokens = production.split(" ");
                        for (int i = productionTokens.length - 1; i >= 0; i--) {
                            TreeNode childNode = new TreeNode(productionTokens[i]);
                            topNode.addChildFromHead(childNode);
                            stack.push(childNode);
                        }
                    } else {
                        topNode.addChild(new TreeNode("E"));
                    }
                } else {
                    // 找到stack中第下一个终结符
                    Stack<TreeNode> tempStack = new Stack<>();
                    while (!stack.isEmpty()) {
                        TreeNode tempNode = stack.pop();
                        tempStack.push(tempNode);
                        if (analyzer.getTerminals().contains(tempNode.value)) {
                            break;
                        }
                    }
                    System.out.println("语法错误,第" + currentLine + "行,缺少\"" + tempStack.peek().value + "\"");
                    tokens.add(index, Arrays.asList(tempStack.peek().value, currentLine));
                    // 复原
                    while (!tempStack.isEmpty()) {
                        stack.push(tempStack.pop());
                    }
                    continue;
                }
            } else {
                System.out.println("语法错误，第" + currentLine + "行，缺少\"" + top+"\"");
                return;
            }
        }

        printSyntaxTree(syntaxTreeRoot, 0);
    }

    private static void printSyntaxTree(TreeNode node, int indent) {
        for (int i = 0; i < indent; i++) {
            System.out.print("\t");
        }
        System.out.println(node.value);
        for (TreeNode child : node.children) {
            printSyntaxTree(child, indent + 1);
        }
    }

    private static void printError(int index) {
        String line = index < tokens.size() ? tokens.get(index).get(1) : "unknown";
        String token = index < tokens.size() ? tokens.get(index).get(0) : "unknown";
        System.out.println("Syntax error at token " + (index + 1) + ": " + token + " on line " + line);
    }

    /**
     * you should add some code in this method to achieve this lab
     */
    public static void analysis(LLGrammerAnalyzer analyzer) {
        read_prog();
        tokenize();
        parse(analyzer);
    }

    /**
     * this is the main method
     *
     * @param args
     */
    public static void main(String[] args) {
        List<String> productions = new ArrayList<>();
        productions.add("program -> compoundstmt");
        productions.add("stmt ->  ifstmt  |  whilestmt  |  assgstmt  |  compoundstmt");
        productions.add("compoundstmt ->  { stmts }");
        productions.add("stmts ->  stmt stmts   |   E");
        productions.add("ifstmt ->  if ( boolexpr ) then stmt else stmt");
        productions.add("whilestmt ->  while ( boolexpr ) stmt");
        productions.add("assgstmt ->  ID = arithexpr ;");
        productions.add("boolexpr  ->  arithexpr boolop arithexpr");
        productions.add("boolop ->   <  |  >  |  <=  |  >=  | ==");
        productions.add("arithexpr  ->  multexpr arithexprprime");
        productions.add("arithexprprime ->  + multexpr arithexprprime  |  - multexpr arithexprprime  |   E");
        productions.add("multexpr ->  simpleexpr  multexprprime");
        productions.add("multexprprime ->  * simpleexpr multexprprime  |  / simpleexpr multexprprime  |   E");
        productions.add("simpleexpr ->  ID  |  NUM  |  ( arithexpr )");

        List<String> terminals = new ArrayList<>();
        // 终结符初始化
        terminals.add("{");
        terminals.add("}");
        terminals.add("if");
        terminals.add("(");
        terminals.add(")");
        terminals.add("then");
        terminals.add("else");
        terminals.add("while");
        terminals.add("ID");
        terminals.add("=");
        terminals.add(">");
        terminals.add("<");
        terminals.add(">=");
        terminals.add("<=");
        terminals.add("==");
        terminals.add("+");
        terminals.add("-");
        terminals.add("*");
        terminals.add("/");
        terminals.add("NUM");
        terminals.add("E");
        terminals.add(";");
        terminals.add("$");
        LLGrammerAnalyzer analyzer = new LLGrammerAnalyzer(productions, terminals);
        analysis(analyzer);
    }
}