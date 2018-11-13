package Syntax;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LRTable implements Serializable{
    private LRTableItem[][] actionTable;

    private List<Character> terminalList;

    private List<Character> nonterminalList;

    private int[][] gotoTable;

    private List<String> rules;


    public LRTable(List<Character> terminalList, List<Character> nonterminalList, List<String> rules, int[][]gotoTable, LRTableItem[][]actionTable)
    {
        this.terminalList = terminalList;
        this.nonterminalList = nonterminalList;
        this.rules = rules;
        this.gotoTable = gotoTable;
        this.actionTable = actionTable;
    }
    public String getRule(int i ){
        assert i < rules.size();
        return rules.get(i);
    }

    public LRTableItem action(int s, char a)
    {
        int col = terminalList.indexOf(a);
        return actionTable[col][s];
    }
    public int goTo(int s, char a)
    {
        int col = nonterminalList.indexOf(a);
        return gotoTable[col][s];
    }
}
