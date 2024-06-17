package org.example.TranslationScheme;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class Java_TranslationSchemaAnalysis {

    static boolean errorFlag = false;
    static List<IDAttribute> idTable = new ArrayList<>();

    /**
     * IDAttribute class
     */
    static class IDAttribute {
        String name;
        String type;
        String value;

        IDAttribute(String name, String type, String value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }
    }

    /**
     * LLGrammar类
     */
    static class Grammar {
        private final List<String> grammars = new ArrayList<>();
        private final List<String> nonterminals = new ArrayList<>();
        private final List<String> terminals = new ArrayList<>();
        private final Map<String, List<String>> first = new HashMap<>();
        private final Map<String, List<String>> follow = new HashMap<>();
        private final List<List<String>> productions = new ArrayList<>();
        private final Map<String, Integer> NT_productions_begin = new HashMap<>();
        private final List<Boolean> productions_Eflag = new ArrayList<>();

        public boolean isNonTerminal(String symbol) {
            return nonterminals.contains(symbol);
        }


        static class TableLine {
            String nonterminal;
            Map<String, Integer> ter_pro = new HashMap<>();
        }

        private List<TableLine> table = new ArrayList<>();

        /**
         * 预处理
         */
        public void preprocess() {
            int productionNum = 0;
            for (String grammar : grammars) {
                Scanner ss = new Scanner(grammar);
                List<String> grammarSplit = new ArrayList<>();
                while (ss.hasNext()) {
                    grammarSplit.add(ss.next());
                }
                ss.close();

                nonterminals.add(grammarSplit.get(0));
                NT_productions_begin.put(grammarSplit.get(0), productionNum);
                List<String> tempProductions = new ArrayList<>();
                for (int j = 2; j < grammarSplit.size(); j++) {
                    if ("|".equals(grammarSplit.get(j))) {
                        productions.add(new ArrayList<>(tempProductions));
                        productions_Eflag.add(false);
                        tempProductions.clear();
                        productionNum++;
                    } else {
                        tempProductions.add(grammarSplit.get(j));
                        if (j == grammarSplit.size() - 1) {
                            productions.add(new ArrayList<>(tempProductions));
                            productions_Eflag.add(false);
                            tempProductions.clear();
                            productionNum++;
                        }
                    }
                }
            }
        }

        public void grammarGet() {
            grammars.add("program -> decls compoundstmt");
            grammars.add("decls -> decl ; decls | E");
            grammars.add("decl -> int ID = INTNUM | real ID = REALNUM");
            grammars.add("stmt -> ifstmt | assgstmt | compoundstmt");
            grammars.add("compoundstmt -> { stmts }");
            grammars.add("stmts -> stmt stmts | E");
            grammars.add("ifstmt -> if ( boolexpr ) then stmt else stmt");
            grammars.add("assgstmt -> ID = arithexpr ;");
            grammars.add("boolexpr -> arithexpr boolop arithexpr");
            grammars.add("boolop -> < | > | <= | >= | ==");
            grammars.add("arithexpr -> multexpr arithexprprime");
            grammars.add("arithexprprime -> + multexpr arithexprprime | - multexpr arithexprprime | E");
            grammars.add("multexpr -> simpleexpr multexprprime");
            grammars.add("multexprprime -> * simpleexpr multexprprime | / simpleexpr multexprprime | E");
            grammars.add("simpleexpr -> ID | INTNUM | REALNUM | ( arithexpr )");

            terminals.add("{");
            terminals.add("}");
            terminals.add("if");
            terminals.add("else");
            terminals.add("then");
            terminals.add("ID");
            terminals.add("=");
            terminals.add("<");
            terminals.add(">");
            terminals.add("<=");
            terminals.add(">=");
            terminals.add("==");
            terminals.add("+");
            terminals.add("-");
            terminals.add("*");
            terminals.add("/");
            terminals.add("(");
            terminals.add(")");
            terminals.add("ID");
            terminals.add("INTNUM");
            terminals.add("REALNUM");
            terminals.add("int");
            terminals.add("real");
            terminals.add(";");
            terminals.add("$");
        }

        public boolean isNonterminal(String str) {
            return nonterminals.contains(str);
        }

        public boolean isTerminal(String str) {
            return terminals.contains(str);
        }

        public List<String> firstGet(String nonterminal) {
            if (!first.containsKey(nonterminal)) {
                first.put(nonterminal, new ArrayList<>());
            } else {
                return new ArrayList<>(first.get(nonterminal));
            }

            int productionEnd = getProductionsEnd(nonterminal);
            for (int i = NT_productions_begin.get(nonterminal); i < productionEnd; i++) {
                List<String> production = productions.get(i);
                boolean eFlag = false;
                if ("E".equals(production.get(0))) {
                    first.get(nonterminal).add("E");
                    productions_Eflag.set(i, true);
                    continue;
                }
                for (String symbol : production) {
                    if (isNonterminal(symbol)) {
                        List<String> temp = firstGet(symbol);
                        for (String s : temp) {
                            first.get(nonterminal).add(s);
                            if ("E".equals(s)) {
                                eFlag = true;
                            }
                        }
                        if (!eFlag) break;
                    } else {
                        first.get(nonterminal).add(symbol);
                        break;
                    }
                }
                if (eFlag) {
                    productions_Eflag.set(i, true);
                }
            }
            return new ArrayList<>(first.get(nonterminal));
        }

        public List<String> followGet(String nonterminal) {
            if (!follow.containsKey(nonterminal)) {
                follow.put(nonterminal, new ArrayList<>());
            } else {
                return new ArrayList<>(follow.get(nonterminal));
            }

            if ("program".equals(nonterminal)) {
                follow.get(nonterminal).add("$");
            }

            int NTnum = 0;
            List<String> production;
            for (int i = 0; i < productions.size(); i++) {
                if (NTnum < nonterminals.size() - 1 && i == NT_productions_begin.get(nonterminals.get(NTnum + 1))) {
                    NTnum++;
                }
                production = productions.get(i);
                for (int j = 0; j < production.size(); j++) {
                    if (nonterminal.equals(production.get(j))) {
                        if (j == production.size() - 1) {
                            if (!nonterminal.equals(nonterminals.get(NTnum))) {
                                follow.get(nonterminal).addAll(followGet(nonterminals.get(NTnum)));
                            }
                        } else {
                            if (!isNonterminal(production.get(j + 1))) {
                                follow.get(nonterminal).add(production.get(j + 1));
                            } else {
                                for (int l = j + 1; l < production.size(); l++) {
                                    List<String> temp = firstGet(production.get(l));
                                    boolean eFlag = false;
                                    for (String s : temp) {
                                        if ("E".equals(s)) {
                                            eFlag = true;
                                        } else {
                                            follow.get(nonterminal).add(s);
                                        }
                                    }
                                    if (!eFlag) {
                                        break;
                                    }
                                    if (l == production.size() - 1 && !nonterminal.equals(nonterminals.get(NTnum))) {
                                        follow.get(nonterminal).addAll(followGet(nonterminals.get(NTnum)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            removeDuplicates(follow.get(nonterminal));
            return new ArrayList<>(follow.get(nonterminal));
        }

        private void removeDuplicates(List<String> list) {
            HashMap<String, Integer> map = new HashMap<>();
            list.removeIf(item -> map.put(item, 1) != null);
        }

        public void init() {
            grammarGet();
            preprocess();

            for (String nonterminal : nonterminals) {
                firstGet(nonterminal);
            }

            for (String nonterminal : nonterminals) {
                followGet(nonterminal);
            }
        }

        private int getProductionsEnd(String nonterminal) {
            for (int i = 0; i < nonterminals.size(); i++) {
                if (nonterminals.get(i).equals(nonterminal)) {
                    if (i < nonterminals.size() - 1) {
                        return NT_productions_begin.get(nonterminals.get(i + 1));
                    } else {
                        return productions.size();
                    }
                }
            }
            return -1; // Or throw an exception
        }

        /**
         * 构造预测分析表
         */
        public void tableConstruct() {
            for (String nonterminal : nonterminals) {
                TableLine temp = new TableLine();
                temp.nonterminal = nonterminal;
                for (String terminal : terminals) {
                    temp.ter_pro.put(terminal, -1);
                }
                table.add(temp);
            }

            int NTnum = 0;
            List<String> production;
            for (int i = 0; i < productions.size(); i++) {
                if (NTnum < nonterminals.size() - 1 && i == NT_productions_begin.get(nonterminals.get(NTnum + 1))) {
                    NTnum++;
                }
                production = new ArrayList<>(productions.get(i));
                for (String symbol : production) {
                    if (isNonterminal(symbol)) {
                        List<String> temp = firstGet(symbol);
                        boolean eFlag = false;
                        for (String s : temp) {
                            if ("E".equals(s)) {
                                eFlag = true;
                            } else {
                                insertTable(table.get(NTnum), i, s);
                            }
                        }
                        if (!eFlag) break;
                    } else {
                        if (!"E".equals(symbol)) {
                            insertTable(table.get(NTnum), i, symbol);
                            break;
                        }
                    }
                    if (production.size() - 1 == 0) {
                        int finalNTnum = NTnum;
                        int finalI = i;
                        followGet(nonterminals.get(NTnum)).forEach(s -> insertTable(table.get(finalNTnum), finalI, s));
                    }
                }
            }
        }

        private void insertTable(TableLine tableLine, int productionNum, String terminal) {
            Integer existingProduction = tableLine.ter_pro.get(terminal);
            if (existingProduction != null && !existingProduction.equals(-1) && !existingProduction.equals(productionNum)) {
                System.out.println("Insert <" + tableLine.nonterminal + "," + terminal + "> with " + productionNum + " conflict");
            } else {
                tableLine.ter_pro.put(terminal, productionNum);
            }
        }
    }

    /**
     * 核心：翻译
     */
    static class Translation {
        /**
         * 声明变量
         * @param id
         * @param type
         * @param value
         * @param valueType
         * @param line
         * @param error
         */
        void declAttribute(String id, String type, String value, String valueType, int line, List<String> error) {
//            System.out.println(id+" "+type+" "+value+" "+valueType);
            IDAttribute temp = new IDAttribute(id, type, value);
            if (type.equals("int")) {
                if (valueType.equals("int")) {
                    temp.value = value;
                } else {
                    error.add("error message:line " + line + ",realnum can not be translated into int type");
                    System.out.println(error);
                    errorFlag = true;
                    temp.value = String.valueOf((int) Double.parseDouble(value));
                }
            } else if (type.equals("real")) {
                if (valueType.equals("real")) {
                    temp.value = value;
                } else {
                    error.add("error message:line " + line + ",intnum can not be translated into real type");
                    errorFlag = true;
                    temp.value = String.valueOf(Double.parseDouble(value));
                }
            }
            idTable.add(temp);
        }

        /**
         * 赋值语句
         * @param id
         * @param value
         * @param valueType
         * @param line
         * @param error
         */
        void assgstmtAttribute(String id, String value, String valueType, int line, List<String> error) {
            boolean isKnownID = false;
            for (IDAttribute idAttr : idTable) {
                if (idAttr.name.equals(id)) {
                    isKnownID = true;
                    if (idAttr.type.equals("int")) {
                        if (valueType.equals("int")) {
                            idAttr.value = value;
                        } else if (!valueType.equals("E")) {
                            error.add("error message:line " + line + ",realnum can not be an intnum.");
                            errorFlag = true;
                        }
                    } else if (idAttr.type.equals("real")) {
                        if (valueType.equals("real")) {
                            idAttr.value = value;
                        } else if (!valueType.equals("E")) {
                            error.add("error message:line " + line + ",intnum can not be a realnum.");
                            errorFlag = true;
                        }
                    }
                    break;
                }
            }
            if (!isKnownID) {
                error.add("error message:line " + line + ", " + id + " is not declared.");
                errorFlag = true;
            }
        }

        /**
         * 布尔表达式
         * @param val1
         * @param type1
         * @param val2
         * @param type2
         * @param op
         * @param line
         * @param error
         * @return
         */
        boolean boolexprAttribute(String val1, String type1, String val2, String type2, String op, int line, List<String> error) {
            if (!type1.equals(type2)) {
                error.add("error message:line " + line + ",type of two operands are not the same.");
                errorFlag = true;
            }

            boolean bigger = false;
            boolean equal = false;
            if (type1.equals("int")) {
                val1 = val1.split("\\.")[0];
                val2 = val2.split("\\.")[0];
                int num1 = Integer.parseInt(val1);
                int num2 = Integer.parseInt(val2);
                if (num1 > num2) {
                    bigger = true;
                } else if (num1 == num2) {
                    equal = true;
                }
            } else if (type1.equals("real")) {
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);
                if (num1 > num2) {
                    bigger = true;
                } else if (num1 == num2) {
                    equal = true;
                }
            }

            switch (op) {
                case "<":
                    return !bigger && !equal;
                case ">":
                    return bigger;
                case "<=":
                    return !bigger;
                case ">=":
                    return bigger || equal;
                case "==":
                    return equal;
                default:
                    return false;
            }
        }

        /**
         * 算术表达式
         * @param multexprVal
         * @param multexprType
         * @param arithexprprimeVal
         * @param arithexprprimeType
         * @param op
         * @param line
         * @param error
         * @return
         */
        String arithexprAttribute(String multexprVal, String multexprType, String arithexprprimeVal, String arithexprprimeType, String op, int line, List<String> error) {
            if (arithexprprimeType.equals("E")) {
                return multexprVal;
            }
            String result;
            if (!multexprType.equals(arithexprprimeType) && !multexprType.equals("E") && !arithexprprimeType.equals("E")) {
                error.add("error message:line " + line + ",type of two operands are not the same.");
                errorFlag = true;
            }
            if (multexprType.equals("int")) {
                multexprVal = multexprVal.split("\\.")[0];
                arithexprprimeVal = arithexprprimeVal.split("\\.")[0];
                int num1 = Integer.parseInt(multexprVal);
                int num2 = Integer.parseInt(arithexprprimeVal);
                if (op.equals("+")) {
                    result = String.valueOf(num1 + num2);
                } else {
                    result = String.valueOf(num1 - num2);
                }
            } else {
                double num1 = Double.parseDouble(multexprVal);
                double num2 = Double.parseDouble(arithexprprimeVal);
                if (op.equals("+")) {
                    result = String.valueOf(num1 + num2);
                } else {
                    result = String.valueOf(num1 - num2);
                }
            }
            return result;
        }

        /**
         * 乘法表达式
         * @param simpleexprVal
         * @param simpleexprType
         * @param multexprprimeVal
         * @param multexprprimeType
         * @param op
         * @param line
         * @param error
         * @return
         */
        String multexprAttribute(String simpleexprVal, String simpleexprType, String multexprprimeVal, String multexprprimeType, String op, int line, List<String> error) {
            if (multexprprimeType.equals("E")) {
                return simpleexprVal;
            }
            String result;
            if (simpleexprType.equals("int")) {
                int num1 = Integer.parseInt(simpleexprVal);
                int num2 = Integer.parseInt(multexprprimeVal);
                if (op.equals("*")) {
                    result = String.valueOf(num1 * num2);
                } else {
                    if (num2 == 0) {
                        error.add("error message:line " + line + ",division by zero");
                        errorFlag = true;
                    }
                    result = String.valueOf(num1 / num2);
                }
            } else {
                double num1 = Double.parseDouble(simpleexprVal);
                double num2 = Double.parseDouble(multexprprimeVal);
                if (op.equals("*")) {
                    result = String.valueOf(num1 * num2);
                } else {
                    if (num2 == 0.0) {
                        error.add("error message:line " + line + ",division by zero");
                        errorFlag = true;
                    }
                    result = String.valueOf(num1 / num2);
                }
            }
            return result;
        }
    }

    /**
     * 语法树
     */
    static class LLTree {
        static class TreeNode {
            String symbol;
            int line;
            boolean hasChild;
            List<TreeNode> children = new ArrayList<>();
            TreeNode parent;
            Map<String, String> attributes = new HashMap<>();
            int childNum;
            boolean isDiscarded;
        }

        TreeNode start;

        /**
         * 构造语法树
         * @param symbol
         * @param translation
         * @param grammar
         * @param inputStack
         * @param needStack
         * @param line
         * @param lineNum
         * @param error
         * @param parent
         * @param childNum
         * @return
         */
        TreeNode treeCreate(String symbol, Translation translation, Grammar grammar, Stack<String> inputStack, Stack<String> needStack, List<Integer> line, int[] lineNum, List<String> error, TreeNode parent, int childNum) {
            TreeNode root = new TreeNode();
            root.symbol = symbol;
            root.line = line.get(lineNum[0]);
            root.parent = parent;
            root.childNum = childNum;

            if (root.parent != null) {
                root.isDiscarded = root.parent.isDiscarded;
            } else {
                root.isDiscarded = false;
            }

            if (root.parent != null && root.parent.symbol.equals("ifstmt") && root.symbol.equals("stmt")) {
                if (root.parent.children.get(2).attributes.get("BOOLVAL").equals("true") && root.childNum == 7) {
                    root.isDiscarded = true;
                } else if (root.parent.children.get(2).attributes.get("BOOLVAL").equals("false") && root.childNum == 5) {
                    root.isDiscarded = true;
                }
            }

            if (!grammar.isNonTerminal(symbol)) {
                root.hasChild = false;
                if (symbol.equals("E")) {
                    root.attributes.put("VAL", "E");
                    root.attributes.put("TYPE", "E");
                } else if (needStack.peek().equals("INTNUM")) {
//                    System.out.println(inputStack.peek() + " " + isIntnum(inputStack.peek())+ " "+ isRealnum(inputStack.peek()));
                    if (isIntnum(inputStack.peek())) {
                        root.attributes.put("TYPE", "int");
                        root.attributes.put("VAL", inputStack.pop());
                        needStack.pop();
                        lineNum[0]++;
                    } else if (isRealnum(inputStack.peek())) {
                        error.add("error message:line " + line.get(lineNum[0] - 1) + ",realnum can not be translated into int type.");
                        errorFlag = true;
                        root.attributes.put("TYPE", "int");
                        root.attributes.put("VAL", inputStack.pop());
                        needStack.pop();
                        lineNum[0]++;
                    }else{
                        error.add("error message:line " + line.get(lineNum[0] - 1) + ",only intnum can be translated into int type.");
                        errorFlag = true;
                    }
                } else if (needStack.peek().equals("REALNUM")) {
                    if (isRealnum(inputStack.peek())) {
                        root.attributes.put("TYPE", "real");
                        root.attributes.put("VAL", inputStack.pop());
                        needStack.pop();
                        lineNum[0]++;
                    } else if (isIntnum(inputStack.peek())) {
                        error.add("error message:line " + line.get(lineNum[0] - 1) + ",intnum can not be translated into real type.");
                        errorFlag = true;
                        root.attributes.put("TYPE", "real");
                        root.attributes.put("VAL", inputStack.pop());
                        needStack.pop();
                        lineNum[0]++;
                    } else {
                        error.add("error message:line " + line.get(lineNum[0] - 1) + ",only realnum can be translated into real type.");
                        errorFlag = true;
                    }
                } else if (needStack.peek().equals("ID")) {
                    if (isID(inputStack.peek())) {
                        root.attributes.put("TYPE", "ID");
                        root.attributes.put("VAL", inputStack.pop());
                        needStack.pop();
                        lineNum[0]++;
                    } else {
                        error.add("error message:line " + line.get(lineNum[0] - 1) + ", ID must be a string of lowercase letters.");
                        errorFlag = true;
                    }
                } else if (symbol.equals(inputStack.peek())) {
                    inputStack.pop();
                    needStack.pop();
                    lineNum[0]++;
                } else if (symbol.equals(needStack.peek())) {
                    needStack.pop();
                    error.add("error message:line " + line.get(lineNum[0] - 1) + ", miss\"" + symbol + "\"");
                    errorFlag = true;
                } else {
                    errorFlag = true;
                    error.add("unknown error.");
                }
            } else {
                root.hasChild = true;
                int NTnum = -1;
                for (int i = 0; i < grammar.nonterminals.size(); i++) {
                    if (grammar.nonterminals.get(i).equals(symbol)) {
                        NTnum = i;
                        break;
                    }
                }
                int productionNum = -2;
                if (grammar.isTerminal(inputStack.peek())) {
                    productionNum = grammar.table.get(NTnum).ter_pro.get(inputStack.peek());
                } else if (isID(inputStack.peek())) {
                    for (IDAttribute idAttr : idTable) {
                        if (idAttr.name.equals(inputStack.peek())) {
                            productionNum = grammar.table.get(NTnum).ter_pro.get("ID");
                            break;
                        }
                    }
                    if (productionNum == -2) {
                        productionNum = grammar.table.get(NTnum).ter_pro.get("ID");
                        error.add("error message:line " + line.get(lineNum[0] - 1) + ", " + inputStack.peek() + " is not defined.");
                        errorFlag = true;
                        IDAttribute temp = new IDAttribute(inputStack.peek(), "E", "0");
                        idTable.add(temp);
                    }
                } else if (isIntnum(inputStack.peek())) {
                    productionNum = grammar.table.get(NTnum).ter_pro.get("INTNUM");
                } else if (isRealnum(inputStack.peek())) {
                    productionNum = grammar.table.get(NTnum).ter_pro.get("REALNUM");
                }
                if (productionNum == -1) {
                    productionNum = grammar.table.get(NTnum).ter_pro.get(needStack.peek());
                }
                if (productionNum == -2) {
                    errorFlag = true;
                    error.add("error message:line " + line.get(lineNum[0] - 1) + ",symbol is not defined.");
                    return root;
                }
                List<String> production = grammar.productions.get(productionNum);
                for (int i = production.size() - 1; i >= 0; i--) {
                    if (!grammar.isNonTerminal(production.get(i)) && !production.get(i).equals("E")) {
                        needStack.push(production.get(i));
                    }
                }
                for (int i = 0; i < production.size(); i++) {
                    root.children.add(treeCreate(production.get(i), translation, grammar, inputStack, needStack, line, lineNum, error, root, i));
                }
            }

            if (root.isDiscarded) {
                return root;
            }

            switch (root.symbol) {
                case "decl":
                    translation.declAttribute(root.children.get(1).attributes.get("VAL"), root.children.get(0).symbol, root.children.get(3).attributes.get("VAL"), root.children.get(3).attributes.get("TYPE"), root.children.get(1).line, error);
                    break;
                case "assgstmt":
                    translation.assgstmtAttribute(root.children.get(0).attributes.get("VAL"), root.children.get(2).attributes.get("VAL"), root.children.get(2).attributes.get("TYPE"), root.children.get(1).line, error);
                    break;
                case "boolexpr":
                    root.attributes.put("BOOLVAL", String.valueOf(translation.boolexprAttribute(root.children.get(0).attributes.get("VAL"), root.children.get(0).attributes.get("TYPE"), root.children.get(2).attributes.get("VAL"), root.children.get(2).attributes.get("TYPE"), root.children.get(1).children.get(0).symbol, root.children.get(1).line, error)));
                    break;
                case "arithexpr":
                    root.attributes.put("VAL", translation.arithexprAttribute(root.children.get(0).attributes.get("VAL"), root.children.get(0).attributes.get("TYPE"), root.children.get(1).attributes.get("VAL"), root.children.get(1).attributes.get("TYPE"), root.children.get(1).children.get(0).symbol, root.children.get(1).line, error));
                    root.attributes.put("TYPE", root.children.get(0).attributes.get("TYPE"));
                    break;
                case "multexpr":
                    root.attributes.put("VAL", translation.multexprAttribute(root.children.get(0).attributes.get("VAL"), root.children.get(0).attributes.get("TYPE"), root.children.get(1).attributes.get("VAL"), root.children.get(1).attributes.get("TYPE"), root.children.get(1).children.get(0).symbol, root.children.get(1).line, error));
                    root.attributes.put("TYPE", root.children.get(0).attributes.get("TYPE"));
                    break;
                case "arithexprprime":
                    if (root.children.size() == 1) {
                        root.attributes.put("VAL", "E");
                        root.attributes.put("TYPE", "E");
                    } else {
                        root.attributes.put("VAL", translation.arithexprAttribute(root.children.get(1).attributes.get("VAL"), root.children.get(1).attributes.get("TYPE"), root.children.get(2).attributes.get("VAL"), root.children.get(2).attributes.get("TYPE"), root.children.get(2).children.get(0).symbol, root.children.get(1).line, error));
                        root.attributes.put("TYPE", root.children.get(1).attributes.get("TYPE"));
                    }
                    break;
                case "multexprprime":
                    if (root.children.size() == 1) {
                        root.attributes.put("VAL", "E");
                        root.attributes.put("TYPE", "E");
                    } else {
                        root.attributes.put("VAL", translation.multexprAttribute(root.children.get(1).attributes.get("VAL"), root.children.get(1).attributes.get("TYPE"), root.children.get(2).attributes.get("VAL"), root.children.get(2).attributes.get("TYPE"), root.children.get(2).children.get(0).symbol, root.children.get(1).line, error));
                        root.attributes.put("TYPE", root.children.get(1).attributes.get("TYPE"));
                    }
                    break;
                case "simpleexpr":
                    if (root.children.size() == 3) {
                        root.attributes.put("VAL", root.children.get(1).attributes.get("VAL"));
                        root.attributes.put("TYPE", root.children.get(1).attributes.get("TYPE"));
                    } else {
                        if (root.children.get(0).symbol.equals("INTNUM")) {
                            root.attributes.put("VAL", root.children.get(0).attributes.get("VAL"));
                            root.attributes.put("TYPE", "int");
                        } else if (root.children.get(0).symbol.equals("REALNUM")) {
                            root.attributes.put("VAL", root.children.get(0).attributes.get("VAL"));
                            root.attributes.put("TYPE", "real");
                        } else if (root.children.get(0).symbol.equals("ID")) {
                            boolean isKnownID = false;
                            for (IDAttribute idAttr : idTable) {
                                if (idAttr.name.equals(root.children.get(0).attributes.get("VAL"))) {
                                    isKnownID = true;
                                    root.attributes.put("VAL", idAttr.value);
                                    root.attributes.put("TYPE", idAttr.type);
                                    break;
                                }
                            }
                            if (!isKnownID) {
                                errorFlag = true;
                            }
                        }
                    }
                    break;
            }
            return root;
        }

        /**
         * 输出语法树（测试用）
         * @param root
         * @param tabNum
         */
        void treeOutput(TreeNode root, int tabNum) {
            if (root.hasChild) {
                for (int i = 0; i < tabNum; i++) {
                    System.out.print("\t");
                }
                System.out.println(root.symbol + "  " + root.line);
                for (TreeNode child : root.children) {
                    treeOutput(child, tabNum + 1);
                }
            } else {
                for (int i = 0; i < tabNum; i++) {
                    System.out.print("\t");
                }
                System.out.println(root.symbol + "  " + root.line);
            }
        }
    }

    private static StringBuffer prog = new StringBuffer();

    /**
     * 读取输入
     */
    private static void read_prog()
    {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            prog.append(sc.nextLine().trim()).append("\n");
        }
    }

    /**
     * 主函数
     * @param args
     */
    public static void main(String[] args) {
        read_prog();
        Grammar grammar = new Grammar();
        grammar.init();
        grammar.tableConstruct();
        Stack<String> inputStack = new Stack<>();
        List<Integer> line = new ArrayList<>();
        inputGet(prog, inputStack, line);
        Translation translation = new Translation();
        LLTree ll = new LLTree();
        int[] lineNum = new int[1];
        List<String> error = new ArrayList<>();
        Stack<String> needStack = new Stack<>();
        ll.start = ll.treeCreate("program", translation, grammar, inputStack, needStack, line, lineNum, error, null, 0);

        if (errorFlag) {
            // 最后一个不要有回车
            for (String s : error) {
                System.out.print(s);
                if(error.indexOf(s) != error.size()-1){
                    System.out.println();
                }
            }
        } else {
            idTable.forEach(idAttr -> System.out.println(idAttr.name + ": " + idAttr.value));
        }
    }

    /**
     * 读取输入（并处理）
     * @param progs
     * @param inputStack
     * @param line
     */
    static void inputGet(StringBuffer progs, Stack<String> inputStack, List<Integer> line) {
        Scanner scanner = new Scanner(progs.toString());
        int lines = 1;
        while (scanner.hasNextLine()) {
            String temp = scanner.nextLine();
            Scanner lineScanner = new Scanner(temp);
            while (lineScanner.hasNext()) {
                String temp2 = lineScanner.next();
                inputStack.push(temp2);
                line.add(lines);
            }
            lines++;
        }
        Collections.reverse(inputStack);
    }

    static boolean isIntnum(String str) {
        for (char c : str.toCharArray()) {
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    static boolean isRealnum(String str) {
        int dotNum = 0;
        for (char c : str.toCharArray()) {
            if (c == '.') {
                dotNum++;
            } else if (c < '0' || c > '9') {
                return false;
            }
        }
        return dotNum == 1;
    }

    static boolean isID(String str) {
        for (char c : str.toCharArray()) {
            if (c < 'a' || c > 'z') {
                return false;
            }
        }
        return true;
    }

}
