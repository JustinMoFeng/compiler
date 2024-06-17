package org.example.LLParser;

import java.util.List;

/**
 * LL(1)文法数据结构
 */
public class LL_Grammer {
    // 左部
    private String LeftWord;

    // 右部
    private List<List<String>> RightWord;

    public LL_Grammer(String leftWord, List<List<String>> rightWord) {
        LeftWord = leftWord;
        RightWord = rightWord;
    }

    public LL_Grammer() {
    }

    public String getLeftWord() {
        return LeftWord;
    }

    public void setLeftWord(String leftWord) {
        LeftWord = leftWord;
    }

    public List<List<String>> getRightWord() {
        return RightWord;
    }

    public void setRightWord(List<List<String>> rightWord) {
        RightWord = rightWord;
    }
}
