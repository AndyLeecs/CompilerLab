package Lexical;

import java.io.*;
import java.util.*;

public class DFA {
    public static char[] allow;//may be removed afterwards
    public static HashMap<Integer, FANode> map;
    public static ArrayList<FANode> nodes = new ArrayList<FANode>();
    private static ArrayList<Dstate> dstates = new ArrayList<Dstate>();

    public static List<Dstate> toMinDFA(ArrayList<FANode> nfaList, ArrayList<String> names) {
        //merge NFAs
        FANode nfa = RE.mergeNFA(nfaList, names);
        nfaList = null;
        //NFA TO DFA

        //得到状态编号
        getChildren(nfa);

        //子集构造法
        ArrayList<FANode> nodesToGetClosure = new ArrayList<FANode>();
        nodesToGetClosure.add(nodes.get(0));
        dstates.add(epiclosure(nodesToGetClosure));
        Dstate cur;
        while ((cur = getUnmarkedState(dstates)) != null) {
            for (char c : allow) {
                Dstate move = getMoveBy(c, cur);
                if(move!=null)
                move = findInDStates(move);
                cur.addToMap(c, move);
            }
        }
        //DFA TO MIN_DFA

        //初始分划，按照是否是终结态划分
        ArrayList<ArrayList<Dstate>> partition = new ArrayList<ArrayList<Dstate>>();
        ArrayList<Dstate> finals = new ArrayList<Dstate>();
        ArrayList<Dstate> unfinals = new ArrayList<Dstate>();

        //找到终结态FAnode(reserveWords的node优先考虑)
        ArrayList<FANode> last = new ArrayList<FANode>();
        ArrayList<FANode> reserves = new ArrayList<>();
        for (FANode node : nodes) {
            if (node.getState() == -1) {
                if(node.getName().equals("reservedWords"))
                {
                    System.out.println("find reserved Words");
                    reserves.add(node);
                }else last.add(node);
            }
        }
        System.out.println("last"+last.size());
        System.out.println("reserves"+reserves.size());
        for (Dstate dstate : dstates) {
            List<FANode> closure = dstate.getEpiclosure();
            for (FANode reserve : reserves) {
                if (closure.contains(reserve)) {
//                    System.out.println(node.getName());
//                    if(dstate.getName()!=null && !dstate.getName().equals("reservedWords"))
                    dstate.setName(reserve.getName());
                    System.out.println("I am a reserved word");
                    finals.add(dstate);
                    break;
                }
            }
            if (!finals.contains(dstate)) {
                for (FANode node : last) {
                    if (closure.contains(node)) {
//                    System.out.println(node.getName());
//                    if(dstate.getName()!=null && !dstate.getName().equals("reservedWords"))
                        dstate.setName(node.getName());
                        finals.add(dstate);
                        break;
                    }
                }
            }
            if (!finals.contains(dstate))
                unfinals.add(dstate);
        }

        partition.add(finals);
        partition.add(unfinals);
        System.out.println("finals"+finals.size());
        System.out.println("unfinals"+unfinals.size());

        ArrayList<ArrayList<Dstate>> newPartition = new ArrayList<ArrayList<Dstate>>();
        newPartition.addAll(partition);
        //进一步分划
        //对分划中的每一个组G
        while (true) {
            for (int i = 0; i < partition.size(); i++) {
                ArrayList<Dstate> group = partition.get(i);
                System.out.println(group.size());
                ArrayList<ArrayList<Dstate>> newGroups = new ArrayList<ArrayList<Dstate>>();
                //将group划分成更小的组
                for (Dstate dstate : group) {
                    if (newGroups.isEmpty()) {
                        ArrayList<Dstate> newgroup = new ArrayList<Dstate>();
                        newgroup.add(dstate);
                        newGroups.add(newgroup);
                    } else {
//                        System.out.println("start checking same group");
                        for (int j = 0; j < newGroups.size(); j++) {
                            ArrayList<Dstate> newgroup = newGroups.get(j);
                            if (newgroup.contains(dstate)) break;
                            if (stillSameGroup(newgroup.get(0), dstate, partition)) {
                                newgroup.add(dstate);
                                break;
                            }
                            if (newGroups.indexOf(newgroup) == (newGroups.size() - 1)) {
                                ArrayList<Dstate> singlegroup = new ArrayList<Dstate>();
                                singlegroup.add(dstate);
                                newGroups.add(singlegroup);
                            }
                        }
                    }
                }
                //在新的分划里面用newgroup来替代group
                if (newGroups.size() != 1) {
                    newPartition.remove(group);
                    System.out.println(newGroups.size());
                    for (ArrayList<Dstate> list : newGroups) {
                        newPartition.add(list);
                    }
                }
            }
            if (newPartition.size() == partition.size()) break;
            partition = newPartition;
        }

        //在各组中选择代表
        ArrayList<Dstate> finalDstates = new ArrayList<Dstate>();
        Dstate start = (Dstate) dstates.toArray()[0];
        finalDstates.add(start);
        for (ArrayList<Dstate> group : partition) {
            if (!group.contains(start)) {
                finalDstates.add(group.get(0));
                for (Dstate state : group) {
                    state.setTag(finalDstates.size() - 1);
                }
            } else {
                for (Dstate state : group) {
                    state.setTag(0);
                }
            }
        }
        //更新原来的Dtrans
        for (int i = 0; i < finalDstates.size(); i++) {
            Map<Character, Dstate> map = finalDstates.get(i).getDtrans();
            for (char c : allow) {
                Dstate dstate = map.get(c);
                if (dstate != null) {
                    int tag = dstate.getTag();
                    for (Dstate finalstate : finalDstates) {
                        if (finalstate.getTag() == tag) {
                            map.put(c, finalstate);
                            break;
                        }
                    }
                }
            }
        }

        return finalDstates;
    }

    private static boolean sameGroup(Dstate dstate1, Dstate dstate2, ArrayList<ArrayList<Dstate>> partition) {
        for (ArrayList<Dstate> group : partition
                ) {
            if (group.contains(dstate1) && group.contains(dstate2)) return true;
        }
        return false;
    }

    private static boolean stillSameGroup(Dstate dstate1, Dstate dstate2, ArrayList<ArrayList<Dstate>> partition) {
        Iterator<Map.Entry<Character, Dstate>> iter1 = dstate1.getDtrans().entrySet().iterator();
        while (iter1.hasNext()) {
            Map.Entry<Character, Dstate> entry1 = iter1.next();
            Dstate value1 = entry1.getValue();
            Dstate value2 = dstate2.getDtrans().get(entry1.getKey());
            if ((value1 == null && value2 != null) || (value1 != null && value2 == null)
                    || !sameGroup(value1, value2, partition)) {
                return false;
            }
        }
        return true;
    }

    private static Dstate findInDStates(Dstate target) {
        for (Dstate state : dstates) {
            if (state.equals(target)) {
//                System.out.println("already have the state");
                return state;
            }
        }
        dstates.add(target);
        return target;
    }

    private static Dstate getMoveBy(char c, Dstate cur) {
        List<FANode> list = new ArrayList<FANode>();
        for (FANode node : cur.getEpiclosure()) {
            if (node.getC() == c) {
                list.add(node.getOutnodes().get(0));
            }
        }
        if(list.isEmpty())return null;
        return epiclosure(list);
    }

    private static Dstate getUnmarkedState(ArrayList<Dstate> dstates) {
        for (Dstate dstate : dstates) {
            if (!dstate.isMarked()) return dstate;
        }
        return null;
    }

    private static Dstate epiclosure(List<FANode> list) {
        //原状态压栈,初始化返回值列表
        Stack<FANode> stack = new Stack<FANode>();
        List<FANode> finalepiclosure = new ArrayList<FANode>();
        for (FANode faNode : list) {
            stack.push(faNode);
            finalepiclosure.add(faNode);
        }

        //计算
        while (!stack.isEmpty()) {
            FANode node = stack.pop();
            //如果出边不是有字母的边而且不是终结态
            if (node.getC() == '\0' && node.getState() != -1) {
                ArrayList<FANode> outnodes = node.getOutnodes();
                for (FANode faNode : outnodes) {
                    if (!stack.contains(faNode)) {
                        finalepiclosure.add(faNode);
                        stack.push(faNode);
                    }
                }
            }
        }
        Dstate res = new Dstate(finalepiclosure);
//        dstates.add(res);
        return res;
    }

    private static void getChildren(FANode nfa) {
        FANode cur = nfa;
        int state = nfa.getState();
        nodes.add(cur);
        cur.setVisited();
        if (state == -1) return;
        ArrayList<FANode> out = nfa.getOutnodes();
        for (int i = 0; i < out.size(); i++) {
            if (!out.get(i).visited) {
                getChildren(out.get(i));
            }
        }
    }

    public static TokenAndPos check(int start, char[] input, List<Dstate> finalminDFA) {
        char c;
        Dstate dstate = finalminDFA.get(0);
        Stack<Dstate> stack = new Stack<Dstate>();
        stack.push(dstate);
        int ptr = start;
        StringBuffer s = new StringBuffer();
        while (true) {
            c = input[ptr];
            if (c == '\n') return getTokenAndPos(start, stack, s);
//            System.out.println("start checking " + c);
            assert dstate != null;
            dstate = dstate.getMapValue(c);
            //回退
            if (dstate == null) {
                while (true) {
                    TokenAndPos name = getTokenAndPos(start, stack, s);
                    if (name != null) return name;
                }
            } else {
                stack.push(dstate);
                ptr++;
                s.append(c);
            }
        }
    }

    private static TokenAndPos getTokenAndPos(int start, Stack<Dstate> stack, StringBuffer s) {

        if (!stack.isEmpty()) {
            Dstate cur = stack.pop();
            String name = cur.getName();
            if (name.length() != 0) {
                return new TokenAndPos(name, s.toString(), start + s.length() - 1);
            }
        }
        return null;
    }

    public static void main(String args[]) {
        ArrayList<FANode> list = new ArrayList<FANode>();
//        list.add(new RE("","").postToNfa("a*"));
//        list.add(new RE("","").postToNfa("ab."));
//        FANode NFA = RE.mergeNFA(list);
//        getChildren(NFA);
//        for (FANode node : nodes)
//        System.out.println(node);
        FANode nfa = new RE("", "(a|b)*abb").toNFA();
        list.add(nfa);
//        getChildren(nfa);
//        list.add(nodes.get(0));
//        dstates.add(epiclosure(list));
//        Dstate cur;
        try {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader
                            (new FileInputStream(new File(Lex.class.getClassLoader().getResource("tryrule.txt").getPath())),
                                    "UTF-8"));
            DFA.allow = br.readLine().toCharArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
//
//        while ((cur = getUnmarkedState(dstates))!=null){
//            for(char c : allow) {
//                Dstate move = getMoveBy(c, cur);
//                move = findInDStates(move);
//                cur.addToMap(c, move);
////                if(dstates.size() == 4){
////                    System.out.println(dstates.get(1).equals(dstates.get(3)));
////                    System.out.println(dstates.get(0).getEpiclosure());
////                    System.out.println(dstates.get(3).getEpiclosure());
////                }
//            }
//        }
//        System.out.println(dstates);
        ArrayList<String> names = new ArrayList<String>();
        names.add("it");
        toMinDFA(list, names);
    }
}
