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
        lr.calculateCanonical();
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

    // 计算项目集规范族
    public void calculateCanonical(){
        // 初始化I0
        List<LR_Grammar> I0 = new ArrayList<>();
        I0.add(new LR_Grammar("program'", List.of("program"), 0));
        I0 = closureHelper(I0);
        canonicalCollection.put(0,I0);
        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);
        while (!queue.isEmpty()){
            Integer index = queue.poll();
            List<LR_Grammar> I = canonicalCollection.get(index);
            Set<String> nextWords = new HashSet<>();
            for (LR_Grammar lr_grammar : I) {
                if(lr_grammar.getDotIndex() < lr_grammar.getRightWord().size()){
                    nextWords.add(lr_grammar.getRightWord().get(lr_grammar.getDotIndex()));
                }
            }
            for (String nextWord : nextWords) {
                List<LR_Grammar> nextI = new ArrayList<>();
                for (LR_Grammar lr_grammar : I) {
                    if(lr_grammar.getDotIndex() < lr_grammar.getRightWord().size() && lr_grammar.getRightWord().get(lr_grammar.getDotIndex()).equals(nextWord)){
                        LR_Grammar nextLR_Grammar = new LR_Grammar(lr_grammar.getLeftWord(),lr_grammar.getRightWord(),lr_grammar.getDotIndex()+1);
                        nextI.add(nextLR_Grammar);
                    }
                }
                nextI = closureHelper(nextI);
                if(!canonicalCollection.containsValue(nextI)){
                    canonicalCollection.put(canonicalCollection.size(),nextI);
                    queue.add(canonicalCollection.size()-1);
                }
            }
        }
        // 输出项目集规范族
        for (Map.Entry<Integer, List<LR_Grammar>> entry : canonicalCollection.entrySet()) {
            System.out.println("I"+entry.getKey());
            for (LR_Grammar lr_grammar : entry.getValue()) {
                System.out.println(lr_grammar);
            }
        }

        

    }

    // 计算集群的闭包
    public List<LR_Grammar> closureHelper(List<LR_Grammar> I){
        // 初始化一个set用于存储已经遍历过的项目
        Set<LR_Grammar> closure = new HashSet<>(I);
        // 初始化一个队列用于存储待遍历的项目
        Queue<LR_Grammar> queue = new LinkedList<>(I);
        while (!queue.isEmpty()){
            LR_Grammar lr_grammar = queue.poll();
            // 如果当前项目的点在最后一个位置，则跳过
            if(lr_grammar.getDotIndex() == lr_grammar.getRightWord().size()){
                continue;
            }
            String nextWord = lr_grammar.getRightWord().get(lr_grammar.getDotIndex());
            if(lr_grammars.containsKey(nextWord)){
                for (LR_Grammar grammar : lr_grammars.get(nextWord)) {
                    if(closure.add(grammar)){
                        queue.add(grammar);
                    }
                }
            }
        }
        return new ArrayList<>(closure);
    }



}
