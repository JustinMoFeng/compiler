package org.example.LLParser;

import java.util.*;

public class LLGrammerAnalyzer {
    private final Set<String> terminals = new HashSet<>();
    private final Set<String> nonterminals = new HashSet<>();

    private final List<String> productions = new ArrayList<>();

    private final Map<String, Set<String>> first = new HashMap<>();

    private final Map<String, Set<String>> follow = new HashMap<>();

    private final List<LL_Grammer> grammerList = new ArrayList<>();

    private final Map<String, Map<String, String>> LLParseTable = new HashMap<>();

    public LLGrammerAnalyzer() {
        init();
    }

    public LLGrammerAnalyzer(List<String> ProductionList,List<String> terminalList){
        productions.addAll(ProductionList);
        terminals.addAll(terminalList);
        init();
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public Set<String> getNonterminals() {
        return nonterminals;
    }

    public List<String> getProductions() {
        return productions;
    }

    public Map<String, Set<String>> getFirst() {
        return first;
    }

    public List<LL_Grammer> getGrammerList() {
        return grammerList;
    }

    public Map<String, Set<String>> getFollow() {
        return follow;
    }

    public static void generateMap() {

    }

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
//        // 求出first集合
//        for (String nonterminal : analyzer.getNonterminals()) {
//            analyzer.calculateFirst(nonterminal);
//        }
//        // 打印first集合
//        for (Map.Entry<String, Set<String>> entry : analyzer.getFirst().entrySet()) {
//            System.out.println("First(" + entry.getKey() + ") = " + entry.getValue());
//        }

//        // 求出follow集合
//        for (String nonterminal : analyzer.getNonterminals()) {
//            analyzer.calculateFollow(nonterminal);
//        }
//        // 打印follow集合
//        for (Map.Entry<String, Set<String>> entry : analyzer.getFollow().entrySet()) {
//            System.out.println("Follow(" + entry.getKey() + ") = " + entry.getValue());
//        }
        analyzer.generateLLParseTable();
        // 把表格打印出来
        for (Map.Entry<String, Map<String, String>> entry : analyzer.getLLParseTable().entrySet()) {
            System.out.println("LL(1)分析表[" + entry.getKey() + "] = " + entry.getValue());
        }
    }

    public Map<String, Map<String, String>> getLLParseTable() {
        return LLParseTable;
    }

    private List<String> calculateFirst(String nonterminal) {
        if(first.containsKey(nonterminal)) {
            return new ArrayList<>(first.get(nonterminal));
        }
        Set<String> firstSet = new HashSet<>();
        for (LL_Grammer grammer : grammerList) {
            if (grammer.getLeftWord().equals(nonterminal)) {
                List<List<String>> rightWord = grammer.getRightWord();
                for (List<String> strings : rightWord) {
                    if (terminals.contains(strings.get(0))) {
                        firstSet.add(strings.get(0));
                    } else {
                        int num = 0;
                        List<String> firstList = calculateFirst(strings.get(num++));
                        while(firstList.contains("E")) {
                            firstList.remove("E");
                            if (strings.size() == 1) {
                                firstList.add("E");
                                break;
                            }
                            firstList.addAll(calculateFirst(strings.get(num++)));
                        }

                        firstSet.addAll(firstList);
                    }
                }
            }
        }
        first.put(nonterminal, firstSet);
        return new ArrayList<>(firstSet);
    }

    private void generateLLParseTable() {
        for (LL_Grammer grammer : grammerList) {
            String nonterminal = grammer.getLeftWord();
            List<List<String>> rightWordLists = grammer.getRightWord();

            for (List<String> rightWords : rightWordLists) {
                Set<String> firstSet = calculateFirstForWords(rightWords);

                for (String terminal : firstSet) {
                    if (!terminal.equals("E")) {
                        LLParseTable.computeIfAbsent(nonterminal, k -> new HashMap<>()).put(terminal, rightWordsToString(rightWords));
                    }
                }

                if (firstSet.contains("E")) {
                    Set<String> followSet = follow.get(nonterminal);
                    for (String followSymbol : followSet) {
                        LLParseTable.computeIfAbsent(nonterminal, k -> new HashMap<>()).put(followSymbol, rightWordsToString(rightWords));
                    }
                }
            }
        }
    }

    // Helper method to calculate the FIRST set for a list of right-hand side symbols
    private Set<String> calculateFirstForWords(List<String> words) {
        Set<String> firstSet = new HashSet<>();
        boolean containsEpsilon = true;

        for (String word : words) {
            if (terminals.contains(word)) {
                firstSet.add(word);
                containsEpsilon = false;
                break;
            } else {
                Set<String> firstOfWord = first.get(word);
                firstSet.addAll(firstOfWord);
                if (!firstOfWord.contains("E")) {
                    containsEpsilon = false;
                    break;
                }
            }
        }

        if (containsEpsilon) {
            firstSet.add("E");
        }

        return firstSet;
    }

    // Helper method to convert a list of right-hand side symbols to a string representation
    private String rightWordsToString(List<String> rightWords) {
        return String.join(" ", rightWords);
    }

    /**
     * this method is to initialize the terminals and nonterminals
     */
    private void init() {

        // 规则预处理
        processProduction();

        // 求出first集合
        for (String nonterminal : nonterminals) {
            calculateFirst(nonterminal);
        }

        // 求出follow集合
        for (String nonterminal : getNonterminals()) {
            calculateFollow(nonterminal);
        }

        // 生成LL(1)分析表
        generateLLParseTable();

    }

    private void processProduction() {
        for (String production : productions) {
            String[] split = production.split("->");
            LL_Grammer grammer = new LL_Grammer();
            String left = split[0].trim();
            String right = split[1].trim();
            grammer.setLeftWord(left);
            nonterminals.add(left);
            String[] rightSplit = right.split("\\|");
            List<List<String>> rightWordList = new ArrayList<>();
            for (String s : rightSplit) {
                String[] rightSplit2 = s.trim().split(" ");
                List<String> rightWord = new ArrayList<>();
                for (String s1 : rightSplit2) {
                    if (!terminals.contains(s1) && !Objects.equals(s1, "")) {
                        nonterminals.add(s1);
                    }
                    if (!Objects.equals(s1, "")) rightWord.add(s1);
                }
                rightWordList.add(rightWord);
            }
            grammer.setRightWord(rightWordList);
            grammerList.add(grammer);
        }
    }

    private void calculateFollow(String nonterminal) {
        if (!follow.containsKey(nonterminal)) {
            follow.put(nonterminal, new HashSet<>());
        }

        // Special case for the start symbol of the grammar
        if (nonterminal.equals("program")) {  // Assuming "program" is the start symbol
            follow.get(nonterminal).add("$");
        }

        // Process each production
        for (LL_Grammer grammar : grammerList) {
            List<List<String>> rights = grammar.getRightWord();
            for (List<String> right : rights) {
                for (int i = 0; i < right.size(); i++) {
                    String symbol = right.get(i);
                    if (nonterminals.contains(symbol)) {
                        Set<String> followSet = follow.computeIfAbsent(symbol, k -> new HashSet<>());

                        // Check if there's a symbol after the current nonterminal
                        if (i + 1 < right.size()) {
                            String nextSymbol = right.get(i + 1);
                            if (terminals.contains(nextSymbol)) {
                                followSet.add(nextSymbol);
                            } else {
                                Set<String> firstOfNext = first.get(nextSymbol);
                                followSet.addAll(firstOfNext);
                                if (firstOfNext.contains("E")) {
                                    followSet.addAll(follow.get(grammar.getLeftWord()));
                                    followSet.remove("E");  // Remove epsilon if it's there
                                }
                            }
                        } else {
                            // If at end of production, add FOLLOW of left-hand nonterminal
                            // 先判断follow.get(grammar.getLeftWord())是否为空，如果为空，说明还没有计算过follow(grammar.getLeftWord())
                            // 那么就先计算follow(grammar.getLeftWord())
                            if (!follow.containsKey(grammar.getLeftWord())) {
                                calculateFollow(grammar.getLeftWord());
                            }
                            followSet.addAll(follow.get(grammar.getLeftWord()));
                        }
                    }
                }
            }
        }
    }

}
