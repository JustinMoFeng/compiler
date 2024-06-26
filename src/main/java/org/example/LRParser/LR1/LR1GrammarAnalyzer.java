package org.example.LRParser.LR1;

import org.example.LLParser.LLGrammerAnalyzer;

import java.util.*;

/**
 * LR1文法分析器
 */
public class LR1GrammarAnalyzer {

    private final List<String> productions;

    private final List<String> terminals;

    private final LLGrammerAnalyzer ll_grammerAnalyzer;

    private final Map<String,List<LR1Grammar>> lr_grammars = new HashMap<>();

    private final Map<Integer,Set<LR1Grammar>> canonicalCollection = new HashMap<>();

    private final Map<Integer,Map<String,Object>> actionTable = new HashMap<>();

    private final Map<Integer,Map<String,Integer>> gotoTable = new HashMap<>();

    public LR1GrammarAnalyzer(List<String> productions, List<String> terminals) {
        this.productions = productions;
        this.terminals = terminals;
        ll_grammerAnalyzer = new LLGrammerAnalyzer(productions, terminals);
        parseLR1Grammar();
        calculateCanonical();
        constructLR1ParsingTable();
    }

    /**
     * 解析LR1文法
     */
    public void parseLR1Grammar() {
        for (String production : productions) {
            String[] split = production.split("->");
            String leftWord = split[0].trim();
            String[] rightWord = split[1].trim().split(" ");
            List<String> rightwords = new ArrayList<>();
            Set<String> lookHeads = new HashSet<>();
            for (int i = 0; i < rightWord.length; i++) {
                if(rightWord[i].equals("|")){
                    // 产生式右部的分隔符
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
                    // 产生式右部的最后一个单词
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
    }

    /**
     * 计算项目集规范族
     */
    public void calculateCanonical(){
        Set<LR1Grammar> start = new HashSet<>();
//        start.add(new LR1Grammar("S'", Arrays.asList("S"), 0, new HashSet<>(Arrays.asList("$"))));
        start.add(new LR1Grammar("program'", Arrays.asList("program"), 0, new HashSet<>(Arrays.asList("$"))));
        start = closureHelper(start);
        canonicalCollection.put(0, start);
        Queue<Set<LR1Grammar>> queue = new LinkedList<>();
        queue.add(start);
        int index = 0;
        while(!queue.isEmpty()){
            Set<LR1Grammar> prev = queue.poll();
            List<String> nextWords = new ArrayList<>();
            for(LR1Grammar lr1Grammar: prev){
                if(lr1Grammar.getDotIndex()<lr1Grammar.getRightWord().size()){
                    nextWords.add(lr1Grammar.getRightWord().get(lr1Grammar.getDotIndex()));
                }
            }
            for(String nextWord: nextWords) {
                if(nextWord.equals("E")){
                    continue;
                }
                Set<LR1Grammar> next = GOTO(prev, nextWord);
                if (next.size() > 0 && !isInMap(next, canonicalCollection)) {
                    canonicalCollection.put(++index, next);
                    queue.add(next);
                }
            }
        }
//        打印项目集规范族
//        for (Map.Entry<Integer, Set<LR1Grammar>> entry : canonicalCollection.entrySet()) {
//            System.out.println("I" + entry.getKey() + ":");
//            for (LR1Grammar lr1Grammar : entry.getValue()) {
//                System.out.println(lr1Grammar);
//            }
//        }
    }

    /**
     * 判断是否在项目集规范族中
     * @param next
     * @param canonicalCollection
     * @return
     */
    private boolean isInMap(Set<LR1Grammar> next, Map<Integer, Set<LR1Grammar>> canonicalCollection) {
        for (Map.Entry<Integer, Set<LR1Grammar>> entry : canonicalCollection.entrySet()) {
            if(areSetEqual(next, new HashSet<>(entry.getValue()))){
                return true;
            }
        }
        return false;
    }

    /**
     * 计算GOTO(I,X)
     * @param I
     * @param X
     * @return
     */
    public Set<LR1Grammar> GOTO(Set<LR1Grammar> I, String X){
        Set<LR1Grammar> J = new HashSet<>();
        for (LR1Grammar lr1Grammar : I) {
            if(lr1Grammar.getDotIndex()<lr1Grammar.getRightWord().size() && lr1Grammar.getRightWord().get(lr1Grammar.getDotIndex()).equals(X)){
                J.add(new LR1Grammar(lr1Grammar.getLeftWord(), lr1Grammar.getRightWord(), lr1Grammar.getDotIndex()+1, lr1Grammar.getLookHead()));
            }
        }
        return closureHelper(J);
    }

    /**
     * 计算闭包
     * @param I
     * @return
     */
    public Set<LR1Grammar> closureHelper(Set<LR1Grammar> I){
        Set<LR1Grammar> prev = new HashSet<>();
        Set<LR1Grammar> next = new HashSet<>(I);
        while(!areSetEqual(prev,next)) {
            prev = new HashSet<>(next);
            for (LR1Grammar lr1Grammar : prev) {
                if (lr1Grammar.getDotIndex() < lr1Grammar.getRightWord().size()) {
                    String nextWord = lr1Grammar.getRightWord().get(lr1Grammar.getDotIndex());
                    if (ll_grammerAnalyzer.getNonterminals().contains(nextWord)) {
                        List<LR1Grammar> lr1Grammars = lr_grammars.get(nextWord);
                        for (LR1Grammar lr1Grammar1 : lr1Grammars) {
                            Set<String> lookHeads;
                            if (lr1Grammar1.getRightWord().size() - lr1Grammar1.getDotIndex() > 0) {
                                lookHeads = ll_grammerAnalyzer.calculateFirstForWords(lr1Grammar.getRightWord().subList(lr1Grammar.getDotIndex() + 1, lr1Grammar.getRightWord().size()));
                                if (lookHeads.contains("E")) {
                                    lookHeads.remove("E");
                                    lookHeads.addAll(lr1Grammar.getLookHead());
                                }
                            } else {
                                lookHeads = lr1Grammar.getLookHead();
                            }
                            LR1Grammar tmp = findGrammarInSet(lr1Grammar1, next);
                            if (tmp==null) {
                                next.add(new LR1Grammar(lr1Grammar1.getLeftWord(), lr1Grammar1.getRightWord(), 0, lookHeads));
                            } else {
                                tmp.getLookHead().addAll(lookHeads);
                            }
                        }
                    }
                }
            }
        }
        return next;
    }

    /**
     * 在集合中查找文法
     * @param lr1Grammar1
     * @param next
     * @return
     */
    private LR1Grammar findGrammarInSet(LR1Grammar lr1Grammar1, Set<LR1Grammar> next) {
        for (LR1Grammar lr1Grammar : next) {
            if(lr1Grammar.getDotIndex()==lr1Grammar1.getDotIndex() && lr1Grammar.getLeftWord().equals(lr1Grammar1.getLeftWord()) && lr1Grammar.getRightWord().equals(lr1Grammar1.getRightWord())){
                return lr1Grammar;
            }
        }
        return null;
    }

    /**
     * 判断两个集合是否相等
     * @param set1
     * @param set2
     * @return
     */
    public boolean areSetEqual(Set<LR1Grammar> set1, Set<LR1Grammar> set2){
        if(set1.size()!=set2.size()){
            return false;
        }
        for(LR1Grammar lr1Grammar : set1){
            LR1Grammar tmp = findGrammarInSet(lr1Grammar, set2);
            if(tmp==null){
                return false;
            }
            if(!tmp.getLookHead().equals(lr1Grammar.getLookHead())){
                return false;
            }
        }
        return true;
    }

    /**
     * 构造LR1分析表
     */
    public void constructLR1ParsingTable() {
        // 初始化action表和goto表
        for (Integer state : canonicalCollection.keySet()) {
            actionTable.put(state, new HashMap<>());
            gotoTable.put(state, new HashMap<>());
        }

        // 遍历所有项集
        for (Integer state : canonicalCollection.keySet()) {
            Set<LR1Grammar> items = canonicalCollection.get(state);

            for (LR1Grammar item : items) {
                if(item.getRightWord().size()==1&&item.getRightWord().get(0).equals("E")){
                    for (String lookAhead : item.getLookHead()) {
                        actionTable.get(state).put(lookAhead, item);
                    }
                    continue;
                }
                if (item.getDotIndex() < item.getRightWord().size()) {
                    String symbol = item.getRightWord().get(item.getDotIndex());
                    // 如果是终结符，检查是否应该shift
                    if (terminals.contains(symbol)) {
                        Set<LR1Grammar> nextStateItems = GOTO(canonicalCollection.get(state), symbol);
                        int nextState = getStateNumber(nextStateItems);
                        if (nextState != -1) {
                            actionTable.get(state).put(symbol, "S" + nextState);
                        }
                    } else {
                        // 如果是非终结符，填写goto表
                        Set<LR1Grammar> nextStateItems = GOTO(canonicalCollection.get(state), symbol);
                        int nextState = getStateNumber(nextStateItems);
                        if (nextState != -1) {
                            gotoTable.get(state).put(symbol, nextState);
                        }
                    }
                } else {
                    // 处理规约项
                    for (String lookAhead : item.getLookHead()) {
                        if (item.getLeftWord().equals("program'") && lookAhead.equals("$")) {
                            actionTable.get(state).put(lookAhead, "Accept");
                        } else {
                            actionTable.get(state).put(lookAhead, item);
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
//        for (Map.Entry<Integer, Map<String, Integer>> entry : gotoTable.entrySet()) {
//            System.out.println("State " + entry.getKey() + ": " + entry.getValue());
//        }
    }

    private int getStateNumber(Set<LR1Grammar> items) {
        for (Integer state : canonicalCollection.keySet()) {
            if (areSetEqual(items, new HashSet<>(canonicalCollection.get(state)))) {
                return state;
            }
        }
        return -1; // 表示未找到
    }

    public Map<Integer, Set<LR1Grammar>> getCanonicalCollection() {
        return canonicalCollection;
    }

    public Map<Integer, Map<String, Object>> getActionTable() {
        return actionTable;
    }

    public Map<Integer, Map<String, Integer>> getGotoTable() {
        return gotoTable;
    }
}
