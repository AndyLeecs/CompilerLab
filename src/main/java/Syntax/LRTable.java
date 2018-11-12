package Syntax;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LRTable implements Serializable{
    private LRTableItem[][] actionTable = {
            //0
                   {new LRTableItem(2,Variety.S),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err)},
            //1
                   {new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.acc)},

            //2
                   {new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(4,Variety.S),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err)},
            //3
                   {new LRTableItem(0,Variety.err),
                    new LRTableItem(5,Variety.S),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(6,Variety.S),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err)},
            //4
                    {new LRTableItem(2,Variety.r),
                    new LRTableItem(2,Variety.r),
                    new LRTableItem(2,Variety.r),
                    new LRTableItem(2,Variety.r),
                    new LRTableItem(2,Variety.r),
                    new LRTableItem(2,Variety.r)},
            //5
                   {new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(8,Variety.S),
                    new LRTableItem(0,Variety.err)},
            //6
                   {new LRTableItem(3,Variety.r),
                    new LRTableItem(3,Variety.r),
                    new LRTableItem(3,Variety.r),
                    new LRTableItem(3,Variety.r),
                    new LRTableItem(3,Variety.r),
                    new LRTableItem(3,Variety.r)},
            //7
                   {new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(9,Variety.S),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err),
                    new LRTableItem(0,Variety.err)},
            //8
                   {new LRTableItem(4,Variety.r),
                    new LRTableItem(4,Variety.r),
                    new LRTableItem(4,Variety.r),
                    new LRTableItem(4,Variety.r),
                    new LRTableItem(4,Variety.r),
                    new LRTableItem(4,Variety.r)},
            //9
                   {new LRTableItem(1,Variety.r),
                    new LRTableItem(1,Variety.r),
                    new LRTableItem(1,Variety.r),
                    new LRTableItem(1,Variety.r),
                    new LRTableItem(1,Variety.r),
                    new LRTableItem(1,Variety.r)}
    };
    //TODO
    private List<Character> terminalList = Arrays.asList('a','c','e','b','d','$');
    //TODO
    private List<Character> nonterminalList = Arrays.asList('S','A','B');
    //TODO
    private int[][] gotoTable =
            {{1,-1,-1},{-1,-1,-1},{-1,3,-1},
            {-1,-1,-1},{-1,-1,-1},{-1,-1,7},
            {-1,-1,-1},{-1,-1,-1},{-1,-1,-1},
                    {-1,-1,-1}};

    private List<String> rules = Arrays.asList("!->S","S->aAcBe","A->b","A->Ab","B->d");


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
//        System.out.println("ACTION"+s+a);
        int col = terminalList.indexOf(a);
//        return actionTable[s][col];
        return actionTable[col][s];
    }
    public int goTo(int s, char a)
    {
        int col = nonterminalList.indexOf(a);
//        return gotoTable[s][col];
        return gotoTable[col][s];
    }
}
