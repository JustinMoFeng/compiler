package org.example.LRParser.LR1;

import org.example.LLParser.LLGrammerAnalyzer;

import java.lang.reflect.Array;
import java.util.*;

public class LR1GrammarAnalyzer {
//    private static final List<String> productions = Arrays.asList(
//            "program' -> program",
//            "program -> compoundstmt",
//            "stmt -> ifstmt | whilestmt | assgstmt | compoundstmt",
//            "compoundstmt -> { stmts }",
//            "stmts -> stmt stmts | E",
//            "ifstmt -> if ( boolexpr ) then stmt else stmt",
//            "whilestmt -> while ( boolexpr ) stmt",
//            "assgstmt -> ID = arithexpr ;",
//            "boolexpr -> arithexpr boolop arithexpr",
//            "boolop -> < | > | <= | >= | ==",
//            "arithexpr -> multexpr arithexprprime",
//            "arithexprprime -> + multexpr arithexprprime | - multexpr arithexprprime | E",
//            "multexpr -> simpleexpr multexprprime",
//            "multexprprime -> * simpleexpr multexprprime | / simpleexpr multexprprime | E",
//            "simpleexpr -> ID | NUM | ( arithexpr )"
//    );

//    private static final List<String> terminals = Arrays.asList(
//            "{", "}", "if", "(", ")", "then", "else", "while", "ID", "=", ">", "<", ">=", "<=", "==", "+", "-", "*", "/", "NUM", "E", ";", "$"
//    );

    private static final List<String> productions = Arrays.asList(
            "S' -> S",
            "S -> L = R",
            "S -> R",
            "L -> * R",
            "L -> id",
            "R -> L"
    );

    private static final List<String> terminals = Arrays.asList(
            "id", "*", "=", "$"
    );

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

    }

    public void closureHelper(List<String> I){

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
