package org.example.LRParser.LR0;

import java.util.List;
import java.util.Objects;

/**
 * LR0文法数据结构
 */
public class LR0Grammar {

    private String LeftWord;

    private List<String> RightWord;

    // 点的位置，表示点在第几个rightWord前面
    private int dotIndex;

    public LR0Grammar(String leftWord, List<String> rightWord, int dotIndex) {
        LeftWord = leftWord;
        RightWord = rightWord;
        this.dotIndex = dotIndex;
    }

    public LR0Grammar() {
    }

    public String getLeftWord() {
        return LeftWord;
    }

    public void setLeftWord(String leftWord) {
        LeftWord = leftWord;
    }

    public List<String> getRightWord() {
        return RightWord;
    }

    public void setRightWord(List<String> rightWord) {
        RightWord = rightWord;
    }

    public int getDotIndex() {
        return dotIndex;
    }

    public void setDotIndex(int dotIndex) {
        this.dotIndex = dotIndex;
    }

    @Override
    public String toString() {
        return "LR0Grammar{" +
                "LeftWord='" + LeftWord + '\'' +
                ", RightWord=" + RightWord +
                ", dotIndex=" + dotIndex +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LR0Grammar lrGrammar = (LR0Grammar) o;
        return dotIndex == lrGrammar.dotIndex && Objects.equals(LeftWord, lrGrammar.LeftWord) && Objects.equals(RightWord, lrGrammar.RightWord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(LeftWord, RightWord, dotIndex);
    }
}
