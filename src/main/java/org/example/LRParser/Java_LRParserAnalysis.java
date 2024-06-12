package org.example.LRParser;

import java.util.*;

public class Java_LRParserAnalysis {

    private static StringBuffer prog = new StringBuffer();
    private static LRGrammarAnalyzer lr = new LRGrammarAnalyzer();

    /**
     *  this method is to read the standard input
     */
    private static void read_prog() {
//        Scanner sc = new Scanner(System.in);
//        while (sc.hasNextLine()) {
//            prog.append(sc.nextLine());
//        }
        prog.append("{\n");
        prog.append("while ( ID == NUM )\n");
        prog.append("{\n");
        prog.append("ID = NUM;\n");
        prog.append("}\n");
        prog.append("}");
    }

    /**
     *  This method splits the input string into tokens
     */
    private static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>(Arrays.asList(input.split(" ")));
        tokens.add("$");  // End of input symbol
        return tokens;
    }

    /**
     *  This method performs the LR parsing and outputs the results
     */
    private static void analysis() {
        read_prog();
        String input = prog.toString().replaceAll("\\s+", " ").trim();
        List<String> tokens = tokenize(input);

        Stack<Integer> stateStack = new Stack<>();
        Stack<String> symbolStack = new Stack<>();
        stateStack.push(0);

        int index = 0;
        while (index < tokens.size()) {
            System.out.println(stateStack);
            System.out.println(symbolStack);
            int currentState = stateStack.peek();
            String currentToken = tokens.get(index);
            Map<String, Object> actionRow = lr.actionTable.get(currentState);

            if (actionRow == null || !actionRow.containsKey(currentToken)) {
                System.out.println("Syntax error at token: " + actionRow);
                System.out.println("Syntax error at token: " + currentToken);
                index++;
                continue; // Skip this token and try to recover
            }

            Object action = actionRow.get(currentToken);
            System.out.println(action);
            if (action instanceof String) {
                String actionStr = (String) action;
                if (actionStr.startsWith("S")) {
                    // Shift action
                    int nextState = Integer.parseInt(actionStr.substring(1));
                    stateStack.push(nextState);
                    symbolStack.push(currentToken);
                    index++;
                } else if (actionStr.equals("ACC")) {
                    System.out.println("Accepted");
                    return;
                }
            }else if(action instanceof LR_Grammar){
                // Reduce action
                LR_Grammar lr_grammar = (LR_Grammar) action;

                for (int i = 0; i < lr_grammar.getRightWord().size(); i++) {
                    stateStack.pop();
                    symbolStack.pop();
                }

                symbolStack.push(lr_grammar.getLeftWord());
                int gotoState = (int) lr.gotoTable.get(stateStack.peek()).get(lr_grammar.getLeftWord());
                stateStack.push(gotoState);

//                System.out.println(String.join(" ", symbolStack));
            }
            System.out.println();
        }
        System.out.println("Syntax analysis completed.");
    }

    /**
     * this is the main method
     * @param args
     */
    public static void main(String[] args) {
        analysis();
    }
}
