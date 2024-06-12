package org.example.LRParser;

import java.util.List;
import java.util.Objects;

public class LR_Grammar {

    private String LeftWord;

    private List<String> RightWord;

    private int dotIndex;

    public LR_Grammar(String leftWord, List<String> rightWord, int dotIndex) {
        LeftWord = leftWord;
        RightWord = rightWord;
        this.dotIndex = dotIndex;
    }

    public LR_Grammar() {
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
        return "LR_Grammar{" +
                "LeftWord='" + LeftWord + '\'' +
                ", RightWord=" + RightWord +
                ", dotIndex=" + dotIndex +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LR_Grammar lrGrammar = (LR_Grammar) o;
        return dotIndex == lrGrammar.dotIndex && Objects.equals(LeftWord, lrGrammar.LeftWord) && Objects.equals(RightWord, lrGrammar.RightWord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(LeftWord, RightWord, dotIndex);
    }
}
