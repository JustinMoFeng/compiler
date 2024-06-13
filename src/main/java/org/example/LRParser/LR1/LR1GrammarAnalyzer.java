package org.example.LRParser.LR1;

import org.example.LLParser.LLGrammerAnalyzer;
import org.example.LRParser.LR0.LR0Grammar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LR1GrammarAnalyzer {
    private static final List<String> productions = Arrays.asList(
            "program' -> program",
            "program -> compoundstmt",
            "stmt ->  ifstmt  |  whilestmt  |  assgstmt  |  compoundstmt",
            "compoundstmt ->  { stmts }",
            "stmts ->  stmt stmts   |   E",
            "ifstmt ->  if ( boolexpr ) then stmt else stmt",
            "whilestmt ->  while ( boolexpr ) stmt",
            "assgstmt ->  ID = arithexpr ;",
            "boolexpr  ->  arithexpr boolop arithexpr",
            "boolop ->   <  |  >  |  <=  |  >=  | ==",
            "arithexpr  ->  multexpr arithexprprime",
            "arithexprprime ->  + multexpr arithexprprime  |  - multexpr arithexprprime  |   E",
            "multexpr ->  simpleexpr  multexprprime",
            "multexprprime ->  * simpleexpr multexprprime  |  / simpleexpr multexprprime  |   E",
            "simpleexpr ->  ID  |  NUM  |  ( arithexpr )"
    );

    private static final List<String> terminals = Arrays.asList(
            "{", "}", "if", "(", ")", "then", "else", "while", "ID", "=", ">", "<", ">=", "<=", "==", "+", "-", "*", "/", "NUM", "E", ";", "$"
    );

    private final LLGrammerAnalyzer ll_grammerAnalyzer = new LLGrammerAnalyzer(productions, terminals);

    private final Map<String,List<LR1Grammar>> lr_grammars = new HashMap<>();

    private final Map<Integer,List<LR1Grammar>> canonicalCollection = new HashMap<>();

    private final Map<Integer,Map<String,Object>> actionTable = new HashMap<>();

    private final Map<Integer,Map<String,Integer>> gotoTable = new HashMap<>();

    public LR1GrammarAnalyzer() {
        init();
    }

    public void init() {

    }

}
