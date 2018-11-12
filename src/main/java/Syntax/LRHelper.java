package Syntax;

import java.util.*;

public class LRHelper {
    public static List<String> syntaxlist = Arrays.asList("!->S","S->CC","C->cC","C->d");

    public static List<Character> terminalList = Arrays.asList('c','d','$');

    public static List<Character> nonterminalList = Arrays.asList('S','C');

    public static List<Character> alllist = Arrays.asList('S','C','c','d','$');


    public static List<List<Item>> items()
    {
        List<List<Item>> c = new ArrayList<>();
        List<Item> init = new ArrayList<>();
        init.add(new Item(0,'$'));
        System.out.println(init.get(0));
        System.out.println("before add closure of first");
        c.add(closure(init));
        System.out.println("init finished");

        int i = 0;
        while(i < c.size())
        {
                System.out.println("check the "+i+"th item");
                List<Item> tmp = c.get(i);
                    for (char ch : alllist)
                    {
                        List<Item> goToList = goTo(tmp,ch);
                        System.out.println("get goTo list");
                        if (!goToList.isEmpty()&&!alreadyIn(c, goToList))
                        {
                            c.add(goToList);
                        }
                    }

            i++;
//            System.out.println(i);
        }

        return c;
    }

    private static boolean alreadyIn(List<List<Item>> c,  List<Item> list)
    {
        for (List<Item> list1 : c)
            if(same(list1, list))
                return true;
        return false;
    }

    private static boolean alreadyIn(List<Item> c,  Item item)
    {
        for (Item item1 : c)
            if(item1.equals(item))
                return true;
        return false;
    }

    private static boolean same(List<Item> list1, List<Item> list)
    {
        if (list1.size() != list.size())return false;
        boolean found = false;
        for (Item item : list) {
            found = false;
            for (Item item1 : list1)
            {
                if (item.equals(item1)){
                    found = true;
                    break;
                }
            }
            if (!found)return false;
        }

        return true;
    }

    public static List<Item> goTo(List<Item> items, char c)
    {
        List<Item> res = new ArrayList<>();
        for (Item item : items)
        {
            if (item.isCurCharacter(c))
                res.add(item.getItemAfterStepMove());
        }
        res = closure(res);

        return res;
    }

    public static List<Item> closure(final List<Item> init)
    {
        List<Item> res = new ArrayList<>();
        res.addAll(init);
        int i = 0;
        while(i<res.size())
        {
           Item item = res.get(i);
            List<Character> tmp = new ArrayList<>();
            if (isCharInSystem(item.getNext()))
            tmp.add(item.getProAfterCur());//加入beta
            tmp.add(item.getLookahead());
            System.out.println("the item is" + item);
//            System.out.println(item.getNext());
//            System.out.println(item.getLookahead());
            for (int j = 0; j < syntaxlist.size() ; j++
                 ) {
                System.out.println("start checking "+syntaxlist.get(j));
                if (startWith(syntaxlist.get(j), item.getNext()))
                for (char c : first(tmp))
                {
                    System.out.println("in first I HAVE "+c);
                    if (!isTerminal(c))continue;
                    System.out.println("add new item with lookahead "+c);
                    Item it = new Item(j,c );
                    if (!alreadyIn(res,it))
                        res.add(it);
                    else
                        break;
                }
//                System.out.println("j "+j);
            }
            i++;
            System.out.println("i "+i);
        }
        return res;
    }

    //如果只有epi，返回空
    //如果传入是空（即传入是epi），返回空
    public static List<Character> first(List<Character> list)
    {
        List<Character> firstlist = new ArrayList<>();
        for (char c : list)
        {
            if (c == '\0')continue;
            List<Character> tmp = first(c);
            if(!tmp.isEmpty())
            {
                firstlist = tmp;
                break;
            }
        }
        firstlist = new ArrayList<>(new HashSet<>(firstlist));
        return firstlist;
    }

    public static List<Character> follow(char c)
    {
        assert !isTerminal(c);
        List<Character> followlist = new ArrayList<>();
        followlist.add('$');//假设$是结束符号
        for (String s : syntaxlist) {
            char tmp = followInPro(s, c);
            if (tmp == '\0'||first(tmp).isEmpty())
            {
                followlist.addAll(follow(getHead(s)));
            }else{
                followlist.addAll(first(tmp));
            }
        }
        followlist = new ArrayList<>(new HashSet<>(followlist));
        return followlist;
    }

    private static List<Character> first(char c)
    {
        List<Character> firstlist = new ArrayList<>();
        if(isTerminal(c))firstlist.add(c);
        else{
            for(String s: syntaxlist)
            {
                if (startWith(s, c))
                {
                    firstlist.addAll(first(getProList(s)));
                }
            }
        }
        return firstlist;
    }

    //假设拿来比较的不是终结符就是非终结符
    private static boolean isTerminal(char c)
    {
        for (char a : terminalList)
    {
        if (a == c)return true;
    }
        return false;
}

    private static boolean isCharInSystem(char c)
    {
        for (char a : alllist)
        {
            if (a == c)return true;
        }
        return false;
    }

    private static List<Character> getProList(String s)
    {
        List<Character> list = new ArrayList<>();
        int i = 3;
        while (s.length() > i)
        {
            list.add(s.charAt(i));
            i++;
        }
        return list;
    }

    private static boolean startWith(final String s, char c)
    {
        if(getHead(s) == c)
        {
            return true;
        }
        else return false;
    }

    //返回s中第一个在c后面的字符
    private static char followInPro(final String s, char c)
    {
        int i = 3;
        while(s.length() > i)
        {
            if (s.charAt(i) == c){
                break;
            }
            i++;
        }
        i++;
        if (s.length() > i)
            return s.charAt(i);
        else return '\0';
    }

    private static char getHead(String s)
    {
        return s.charAt(0);
    }

//    private static void addChar(List<Character> list, char c)
//    {
//        if (c != '\0')list.add(c);
//    }


    public static LRTable getLRTable()
    {
        List<List<Item>> graph = items();
        int[][] goToTable = new int[nonterminalList.size()][graph.size()];
        LRTableItem[][] actionTable = new LRTableItem[terminalList.size()][graph.size()];

        for (int i = 0; i < actionTable.length ; i++)
            Arrays.fill(actionTable[i], new LRTableItem(0, Variety.err));

        for (int i = 0 ; i < graph.size() ;i++)
        {
            List<Item> items = graph.get(i);
            for (int j = 0; j < graph.size() ; j++) {
                //GOTO转换，如果GOTO(I_i,A)=I_j,则GOTO[i,A] = j
                for (int idx = 0; idx < nonterminalList.size(); idx++) {
                    char c = nonterminalList.get(idx);
                    if (same(goTo(items, c), graph.get(j)))
                        goToTable[idx][i] = j;
                }
                //action转换，如果[A->alpha `a beta,b]在I_i中，并且GOTO(I_i,a)=I_j,那么将Action[i,a]设置为移入j
                for (int idx = 0; idx < terminalList.size(); idx++) {
                    char c = terminalList.get(idx);
                    if (same(goTo(items, c), graph.get(j))) {
                        if (actionTable[idx][i].getVariety().equals(Variety.err))
                            actionTable[idx][i] = new LRTableItem(j, Variety.S);
                        else
                        {
                            System.err.println("conflict!");
                        }
                    }
                }
            }
                //action转换
                //如果[A->alpha`,a]在I_i中且A不是！，将Action[i,a]置为规约A->alpha
                //如果A是!,置为acc
            for (int k = 0 ; k < items.size(); k++)
            {
                Item item = items.get(k);
                char lookahead = item.getLookahead();

                if (item.isFinal()){
                    if (item.getHead() == '!')
                    {
                        actionTable[terminalList.indexOf(lookahead)][i] = new LRTableItem(0, Variety.acc);
                    }
                    else
                    {
                        if (actionTable[terminalList.indexOf(lookahead)][i].getVariety().equals(Variety.err))
                        actionTable[terminalList.indexOf(lookahead)][i] = new LRTableItem(item.getProidx(), Variety.r);
                        else
                        {
                            System.err.println("conflict!");
                        }
                    }
                }
            }
        }

        return new LRTable(terminalList, nonterminalList, syntaxlist, goToTable, actionTable);
    }
}
