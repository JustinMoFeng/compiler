package org.example.LLParser;

import org.example.LLParser.CFGGrammerAnalyzer;
public class Java_LLParserAnalysis {
    private static final StringBuffer prog = new StringBuffer();

    // 导入CFG文法分析器
    private static final CFGGrammerAnalyzer analyzer = new CFGGrammerAnalyzer();





    /**
     * this method is to read the standard input
     */
    private static void read_prog() {
//        Scanner sc = new Scanner(System.in);
//        while (sc.hasNextLine()) {
//            prog.append(sc.nextLine());
//        }
        prog.append("{\n");
        prog.append("ID = NUM ;\n");
        prog.append("}");
    }


    // add your method here!!




    /**
     * you should add some code in this method to achieve this lab
     */
    private static void analysis() {
        read_prog();

    }

    /**
     * this is the main method
     *
     * @param args
     */
    public static void main(String[] args) {
        analysis();
    }
}










