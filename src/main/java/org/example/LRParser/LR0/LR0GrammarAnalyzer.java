package org.example.LRParser.LR0;

import org.example.LLParser.LLGrammerAnalyzer;
import org.example.LLParser.LL_Grammer;

import java.util.*;

/**
 * LR0文法分析器
 */
public class LR0GrammarAnalyzer {
    private final List<String> productions;

    private final List<String> terminals;

    private final LLGrammerAnalyzer ll_grammerAnalyzer;

    private final Map<String,List<LR0Grammar>> lr_grammars = new HashMap<>();

    private final Map<Integer,List<LR0Grammar>> canonicalCollection = new HashMap<>();

    public LLGrammerAnalyzer getLl_grammerAnalyzer() {
        return ll_grammerAnalyzer;
    }

    public Map<String, List<LR0Grammar>> getLr_grammars() {
        return lr_grammars;
    }

    public Map<Integer, List<LR0Grammar>> getCanonicalCollection() {
        return canonicalCollection;
    }


    public Map<Integer, Map<String, Object>> getActionTable() {
        return actionTable;
    }

    public Map<Integer, Map<String, Object>> getGotoTable() {
        return gotoTable;
    }

    public LR0GrammarAnalyzer(List<String> productions, List<String> terminals) {
        this.productions = productions;
        this.terminals = terminals;
        ll_grammerAnalyzer = new LLGrammerAnalyzer(productions, terminals);
        parseLRGrammar(ll_grammerAnalyzer.getGrammerList());
        calculateCanonical();
        generateLRConstructionTable();
    }

    // 动作表和状态转换表
    Map<Integer, Map<String, Object>> actionTable = new HashMap<>();
    Map<Integer, Map<String, Object>> gotoTable = new HashMap<>();

    /**
     * main function
     * @param args
     */
    public static void main(String[] args) {
        final List<String> productions = Arrays.asList(
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

        final List<String> terminals = Arrays.asList(
                "{", "}", "if", "(", ")", "then", "else", "while", "ID", "=", ">", "<", ">=", "<=", "==", "+", "-", "*", "/", "NUM", "E", ";", "$"
        );

        LR0GrammarAnalyzer lr = new LR0GrammarAnalyzer(productions, terminals);
        lr.calculateCanonical();
        lr.generateLRConstructionTable();
    }

    /**
     * This method is used to parse the LL grammar to LR grammar
     * @param ll_grammars
     */
    public void parseLRGrammar(List<LL_Grammer> ll_grammars){
        for (LL_Grammer ll_grammer : ll_grammars) {
            List<List<String>> rightWord = ll_grammer.getRightWord();
            for (List<String> strings : rightWord) {
                List<String> right = new ArrayList<>(strings);
                LR0Grammar lr_0_grammar = new LR0Grammar(ll_grammer.getLeftWord(),right,0);
                if(lr_grammars.containsKey(lr_0_grammar.getLeftWord())){
                    lr_grammars.get(lr_0_grammar.getLeftWord()).add(lr_0_grammar);
                }else {
                    List<LR0Grammar> list = new ArrayList<>();
                    list.add(lr_0_grammar);
                    lr_grammars.put(lr_0_grammar.getLeftWord(),list);
                }
            }
        }
    }

    /**
     * 计算项目集规范族
     */
    public void calculateCanonical(){
        // 初始化I0
        List<LR0Grammar> I0 = new ArrayList<>();
        I0.add(new LR0Grammar("program'", List.of("program"), 0));
        I0 = closureHelper(I0);
        canonicalCollection.put(0,I0);
        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);
        int i = 1;

        while (!queue.isEmpty()){
            Integer index = queue.poll();
            System.out.println(index);
            List<LR0Grammar> I = canonicalCollection.get(index);
            List<String> nextWords = new ArrayList<>();
            for (LR0Grammar lr_0_grammar : I) {
                if(lr_0_grammar.getDotIndex() < lr_0_grammar.getRightWord().size()){
                    String nextWord = lr_0_grammar.getRightWord().get(lr_0_grammar.getDotIndex());
                    if(Objects.equals(nextWord, "E")){
                        continue;
                    }
                    nextWords.add(nextWord);
                }
            }

            for (String nextWord : nextWords) {
                List<LR0Grammar> nextI = new ArrayList<>();
                for (LR0Grammar lr_0_grammar : I) {
                    if (lr_0_grammar.getDotIndex() < lr_0_grammar.getRightWord().size() && lr_0_grammar.getRightWord().get(lr_0_grammar.getDotIndex()).equals(nextWord)) {
                        nextI.add(new LR0Grammar(lr_0_grammar.getLeftWord(), lr_0_grammar.getRightWord(), lr_0_grammar.getDotIndex() + 1));
                    }
                }
                nextI = closureHelper(nextI);
                boolean isNew = true;
                for (List<LR0Grammar> grammars : canonicalCollection.values()) {
                    if (areGrammarsEqual(grammars, nextI)) {
                        isNew = false;
                        break;
                    }
                }
                if (isNew) {
                    canonicalCollection.put(i, nextI);
                    queue.add(i);
                    i++;
                }
            }
        }
//         输出项目集规范族
//        for (Map.Entry<Integer, List<LR0Grammar>> entry : canonicalCollection.entrySet()) {
//            System.out.println("I" + entry.getKey() + ":");
//            for (LR0Grammar lr_0_grammar : entry.getValue()) {
//                System.out.println("    " + lr_0_grammar.getLeftWord() + " -> " + lr_0_grammar.getRightWord() + ", " + lr_0_grammar.getDotIndex());
//            }
//        }
    }

    /**
     * 辅助函数，检查两个List<LR0Grammar>是否相同
     * @param list1
     * @param list2
     */
    private boolean areGrammarsEqual(List<LR0Grammar> list1, List<LR0Grammar> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (LR0Grammar grammar : list1) {
            if (!list2.contains(grammar)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算集群的闭包
     * @param I
     * @return closure
     */
    public List<LR0Grammar> closureHelper(List<LR0Grammar> I){
        // 初始化一个set用于存储已经遍历过的项目
        Set<LR0Grammar> closure = new HashSet<>(I);
        // 初始化一个队列用于存储待遍历的项目
        Queue<LR0Grammar> queue = new LinkedList<>(I);
        while (!queue.isEmpty()){
            LR0Grammar lr_0_grammar = queue.poll();
            // 如果当前项目的点在最后一个位置，则跳过
            if(lr_0_grammar.getDotIndex() == lr_0_grammar.getRightWord().size()){
                continue;
            }
            String nextWord = lr_0_grammar.getRightWord().get(lr_0_grammar.getDotIndex());
            if(lr_grammars.containsKey(nextWord)){
                for (LR0Grammar grammar : lr_grammars.get(nextWord)) {
                    // 循环遍历set
                    closure.add(grammar);
                    queue.add(grammar);
                }
            }
        }
        return new ArrayList<>(closure);
    }

    /**
     * 生成LR分析表
     */
    public void generateLRConstructionTable() {

        // 填充动作表和状态转换表
        for (Map.Entry<Integer, List<LR0Grammar>> entry : canonicalCollection.entrySet()) {
            int state = entry.getKey();
            List<LR0Grammar> items = entry.getValue();

            actionTable.putIfAbsent(state, new HashMap<>());
            gotoTable.putIfAbsent(state, new HashMap<>());

            for (LR0Grammar item : items) {
                if (item.getDotIndex() < item.getRightWord().size()) {
                    String symbol = item.getRightWord().get(item.getDotIndex());
                    if(Objects.equals(symbol, "E")){
                        Set<String> follow = ll_grammerAnalyzer.getFollow().get(item.getLeftWord());
                        for (String s : follow) {
                            if(terminals.contains(s)){
                                actionTable.get(state).put(s, item);
                            }
                        }
                        continue;
                    }
                    if (terminals.contains(symbol)) {
                        int nextState = getNextState(state, symbol);
                        if (nextState != -1) {
                            actionTable.get(state).put(symbol, "S" + nextState);
                        }else{
                            System.out.println("Error");
                            System.out.println(state+" "+symbol);
                            return ;
                        }
                    } else {
                        int nextState = getNextState(state, symbol);
                        if (nextState != -1) {
                            gotoTable.get(state).put(symbol, nextState);
                        }else{
                            System.out.println("Error");
                            System.out.println(state+" "+symbol);
                            return ;
                        }
                    }
                } else if (item.getLeftWord().equals("program'") && item.getRightWord().get(0).equals("program")) {
                    actionTable.get(state).put("$", "ACC");
                } else {
                    // 获取item.getLeftWord()的follow集
                    Set<String> follow = ll_grammerAnalyzer.getFollow().get(item.getLeftWord());
                    for (String symbol : follow) {
                        if (terminals.contains(symbol)) {
                            actionTable.get(state).put(symbol, item);
                        }
                    }
                }
            }
        }

//         输出动作表和状态转换表
//        System.out.println("Action Table:");
//        for (Map.Entry<Integer, Map<String, Object>> entry : actionTable.entrySet()) {
//            System.out.println("State " + entry.getKey() + ": " + entry.getValue());
//        }
//
//        System.out.println("Goto Table:");
//        for (Map.Entry<Integer, Map<String, Object>> entry : gotoTable.entrySet()) {
//            System.out.println("State " + entry.getKey() + ": " + entry.getValue());
//        }
    }

    /**
     * 获取下一个状态
     * @param currentState
     * @param symbol
     * @return
     */
    private int getNextState(int currentState, String symbol) {
        List<LR0Grammar> items = canonicalCollection.get(currentState);
        List<LR0Grammar> nextItems = new ArrayList<>();
        for (LR0Grammar item : items) {
            if (item.getDotIndex() < item.getRightWord().size() && item.getRightWord().get(item.getDotIndex()).equals(symbol)) {
                nextItems.add(new LR0Grammar(item.getLeftWord(), item.getRightWord(), item.getDotIndex() + 1));
            }
        }
        nextItems = closureHelper(nextItems);
        for (Map.Entry<Integer, List<LR0Grammar>> entry : canonicalCollection.entrySet()) {
            if (areGrammarsEqual(entry.getValue(), nextItems)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * 查找产生式的索引
     * @param lhs
     * @param rhs
     * @return
     */
    private int findProductionIndex(String lhs, List<String> rhs) {
        String production = lhs + " ->";
        for (String s : rhs) {
            production += " " + s;
        }
        System.out.println(production);
        System.out.println(productions.indexOf(production));
        return productions.indexOf(production);
    }
}