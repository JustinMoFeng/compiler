package org.example.LRParser.LR1;

import org.example.LLParser.LLGrammerAnalyzer;

import java.util.*;

public class LR1GrammarAnalyzer {
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

//    private static final List<String> productions = Arrays.asList(
//            "S' -> S",
//            "S -> L = R",
//            "S -> R",
//            "L -> * R",
//            "L -> id",
//            "R -> L"
//    );
//
//    private static final List<String> terminals = Arrays.asList(
//            "id", "*", "=", "$"
//    );

    private final LLGrammerAnalyzer ll_grammerAnalyzer = new LLGrammerAnalyzer(productions, terminals);

    private final Map<String,List<LR1Grammar>> lr_grammars = new HashMap<>();

    private final Map<Integer,List<LR1Grammar>> canonicalCollection = new HashMap<>();

    private final Map<Integer,Map<String,Object>> actionTable = new HashMap<>();

    private final Map<Integer,Map<String,Integer>> gotoTable = new HashMap<>();

    public LR1GrammarAnalyzer() {
        parseLR1Grammar();
    }

    public void parseLR1Grammar() {
        for (String production : productions) {
            String[] split = production.split("->");
            String leftWord = split[0].trim();
            String[] rightWord = split[1].trim().split(" ");
            List<String> rightwords = new ArrayList<>();
            List<String> lookHeads = new ArrayList<>();
            for (int i = 0; i < rightWord.length; i++) {
                if(rightWord[i].equals("|")){
                    LR1Grammar lr1Grammar = new LR1Grammar(leftWord, rightwords, 0, lookHeads);
                    if(lr_grammars.containsKey(leftWord)){
                        lr_grammars.get(leftWord).add(lr1Grammar);
                    }else {
                        List<LR1Grammar> lr1Grammars = new ArrayList<>();
                        lr1Grammars.add(lr1Grammar);
                        lr_grammars.put(leftWord,lr1Grammars);
                    }
                    rightwords = new ArrayList<>();
                }else if(i==rightWord.length-1) {
                    rightwords.add(rightWord[i]);
                    LR1Grammar lr1Grammar = new LR1Grammar(leftWord, rightwords, 0, lookHeads);
                    if (lr_grammars.containsKey(leftWord)) {
                        lr_grammars.get(leftWord).add(lr1Grammar);
                    } else {
                        List<LR1Grammar> lr1Grammars = new ArrayList<>();
                        lr1Grammars.add(lr1Grammar);
                        lr_grammars.put(leftWord, lr1Grammars);
                    }
                }else {
                    rightwords.add(rightWord[i]);
                }
            }
        }
        System.out.println(lr_grammars);
    }

    public void calculateCanonical(){
        // 初始化I0
        List<LR1Grammar> I0 = new ArrayList<>();
        I0.add(new LR1Grammar("program'", List.of("program"), 0, List.of("$")));
//        I0.add(new LR1Grammar("S'", List.of("S"), 0, List.of("$")));
        I0 = closureHelper(I0);
        canonicalCollection.put(0,I0);
        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);
        int i = 1;

        while (!queue.isEmpty()){
            Integer index = queue.poll();
            List<LR1Grammar> I = canonicalCollection.get(index);
            List<String> nextWords = new ArrayList<>();
            for (LR1Grammar lr_1_grammar : I) {
                if(lr_1_grammar.getDotIndex() < lr_1_grammar.getRightWord().size()){
                    String nextWord = lr_1_grammar.getRightWord().get(lr_1_grammar.getDotIndex());
                    if(Objects.equals(nextWord, "E")){
                        continue;
                    }
                    nextWords.add(nextWord);
                }
            }

            for (String nextWord : nextWords) {
                List<LR1Grammar> nextI = new ArrayList<>();
                for (LR1Grammar lr_1_grammar : I) {
                    if (lr_1_grammar.getDotIndex() < lr_1_grammar.getRightWord().size() && lr_1_grammar.getRightWord().get(lr_1_grammar.getDotIndex()).equals(nextWord)) {
                        nextI.add(new LR1Grammar(lr_1_grammar.getLeftWord(), lr_1_grammar.getRightWord(), lr_1_grammar.getDotIndex() + 1, lr_1_grammar.getLookHead()));
                    }
                }
                nextI = closureHelper(nextI);
                boolean isNew = true;
                for (List<LR1Grammar> grammars : canonicalCollection.values()) {
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
        // 输出项目集规范族
        for (Map.Entry<Integer, List<LR1Grammar>> entry : canonicalCollection.entrySet()) {
            System.out.println("I" + entry.getKey() + ":");
            for (LR1Grammar lr_1_grammar : entry.getValue()) {
                System.out.println("    " + lr_1_grammar.getLeftWord() + " -> " + lr_1_grammar.getRightWord() + ", " + lr_1_grammar.getDotIndex() + ", " + lr_1_grammar.getLookHead());
            }
        }
    }

    // 辅助函数，检查两个List<LR1Grammar>是否相同
    private boolean areGrammarsEqual(List<LR1Grammar> list1, List<LR1Grammar> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (LR1Grammar grammar : list1) {
            if (!list2.contains(grammar)) {
                return false;
            }
        }
        return true;
    }


    // 计算集群的闭包
    public List<LR1Grammar> closureHelper(List<LR1Grammar> I){
        // 初始化一个set用于存储已经遍历过的项目
        Set<LR1Grammar> closure = new HashSet<>(I);
        // 初始化一个队列用于存储待遍历的项目
        Queue<LR1Grammar> queue = new LinkedList<>(I);
        while (!queue.isEmpty()){
            LR1Grammar lr_1_grammar = queue.poll();
            // 如果当前项目的点在最后一个位置，则跳过
            if(lr_1_grammar.getDotIndex() == lr_1_grammar.getRightWord().size()){
                continue;
            }
            List<String> lookHeads;
            if(lr_1_grammar.getDotIndex() == lr_1_grammar.getRightWord().size()-1){
                lookHeads = lr_1_grammar.getLookHead();
            }else {
                Set<String> firstSet = ll_grammerAnalyzer.calculateFirstForWords(lr_1_grammar.getRightWord().subList(lr_1_grammar.getDotIndex() + 1, lr_1_grammar.getRightWord().size()));
                if(firstSet.contains("E")){
                    firstSet.remove("E");
                    firstSet.addAll(lr_1_grammar.getLookHead());
                }
                lookHeads = new ArrayList<>(firstSet);
            }
            String nextWord = lr_1_grammar.getRightWord().get(lr_1_grammar.getDotIndex());
            if(lr_grammars.containsKey(nextWord)){
                for (LR1Grammar grammar : lr_grammars.get(nextWord)) {
                    LR1Grammar newGrammar = new LR1Grammar(grammar.getLeftWord(), grammar.getRightWord(), 0, lookHeads);
                    // 循环遍历set
                    closure.add(newGrammar);
                    queue.add(newGrammar);
                }
            }
        }
        return new ArrayList<>(closure);
    }

    public void generateParseTable(){

    }

    public Map<Integer, List<LR1Grammar>> getCanonicalCollection() {
        return canonicalCollection;
    }

    public Map<Integer, Map<String, Object>> getActionTable() {
        return actionTable;
    }

    public Map<Integer, Map<String, Integer>> getGotoTable() {
        return gotoTable;
    }

    public static void main(String[] args) {
        LR1GrammarAnalyzer lr1GrammarAnalyzer = new LR1GrammarAnalyzer();
        lr1GrammarAnalyzer.calculateCanonical();
        System.out.println(lr1GrammarAnalyzer.getCanonicalCollection());
    }
}
