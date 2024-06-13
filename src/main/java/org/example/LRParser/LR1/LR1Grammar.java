package org.example.LRParser.LR1;

import java.util.List;
import java.util.Objects;

public class LR1Grammar {

    private String LeftWord;

    private List<String> RightWord;

    private int DotIndex;

    private String LookHead;

    public LR1Grammar(String leftWord, List<String> rightWord, int dotIndex, String lookHead) {
        LeftWord = leftWord;
        RightWord = rightWord;
        DotIndex = dotIndex;
        LookHead = lookHead;
    }

    public LR1Grammar() {
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
        return DotIndex;
    }

    public void setDotIndex(int dotIndex) {
        DotIndex = dotIndex;
    }

    public String getLookHead() {
        return LookHead;
    }

    public void setLookHead(String lookHead) {
        LookHead = lookHead;
    }

    @Override
    public String toString() {
        return "LR0Grammar{" +
                "LeftWord='" + LeftWord + '\'' +
                ", RightWord=" + RightWord +
                ", dotIndex=" + DotIndex +
                ", LookHead='" + LookHead + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LR1Grammar lrGrammar = (LR1Grammar) o;
        return DotIndex == lrGrammar.DotIndex && Objects.equals(LeftWord, lrGrammar.LeftWord) && Objects.equals(RightWord, lrGrammar.RightWord) && Objects.equals(LookHead, lrGrammar.LookHead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(LeftWord, RightWord, DotIndex, LookHead);
    }
}
