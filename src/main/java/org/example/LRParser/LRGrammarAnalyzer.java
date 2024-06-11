package org.example.LRParser;

import org.example.LLParser.LLGrammerAnalyzer;
import org.example.LLParser.LL_Grammer;

import java.util.*;

public class LRGrammarAnalyzer {
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

    private final Map<String,List<LR_Grammar>> lr_grammars = new HashMap<>();

    private final Map<Integer,List<LR_Grammar>> canonicalCollection = new HashMap<>();

    public LRGrammarAnalyzer() {
        parseLRGrammar(ll_grammerAnalyzer.getGrammerList());
    }

    public static void main(String[] args) {
        LRGrammarAnalyzer lr = new LRGrammarAnalyzer();
        // 打印lr_grammars
        for (Map.Entry<String, List<LR_Grammar>> entry : lr.lr_grammars.entrySet()) {
            System.out.print(entry.getKey() + " : [");
            for (LR_Grammar lr_grammar : entry.getValue()) {
                System.out.print(lr_grammar.getRightWord().toString() + ",");
            }
            System.out.println("]");
        }
    }

    public void parseLRGrammar(List<LL_Grammer> ll_grammars){
        for (LL_Grammer ll_grammer : ll_grammars) {
            List<List<String>> rightWord = ll_grammer.getRightWord();
            for (List<String> strings : rightWord) {
                List<String> right = new ArrayList<>(strings);
                LR_Grammar lr_grammar = new LR_Grammar(ll_grammer.getLeftWord(),right,0);
                if(lr_grammars.containsKey(lr_grammar.getLeftWord())){
                    lr_grammars.get(lr_grammar.getLeftWord()).add(lr_grammar);
                }else {
                    List<LR_Grammar> list = new ArrayList<>();
                    list.add(lr_grammar);
                    lr_grammars.put(lr_grammar.getLeftWord(),list);
                }
            }
        }
    }

    public void calculateCanonical(){
        // 初始化I0
        List<LR_Grammar> I0 = new ArrayList<>();
        I0.add(new LR_Grammar("program'", List.of("program"), 0));
        

    }



}
