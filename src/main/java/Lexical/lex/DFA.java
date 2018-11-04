package Lexical.lex;

import java.util.*;

public class DFA {
    public static char[] allow;
    private ArrayList<NFANode> nodes = new ArrayList<>();
    private ArrayList<Dstate> dstates = new ArrayList<>();

    public List<Dstate> toMinDFA(ArrayList<NFANode> nfaList, ArrayList<String> names) {
        //merge NFAs
        NFANode nfa = RE.mergeNFA(nfaList, names);
        //NFA TO DFA

        //得到状态编号
        getChildren(nfa);

        //子集构造法
        ArrayList<NFANode> nodesToGetClosure = new ArrayList<>();
        nodesToGetClosure.add(nodes.get(0));
        dstates.add(epiclosure(nodesToGetClosure));
        Dstate cur;
        while ((cur = getUnmarkedState(dstates)) != null) {
            for (char c : allow) {
                Dstate move = getMoveBy(c, cur);
                if (move != null)
                    move = findInDStates(move);
                cur.addToMap(c, move);
            }
        }
        //DFA TO MIN_DFA

        //初始分划，按照是否是终结态划分
        ArrayList<ArrayList<Dstate>> partition = new ArrayList<>();
        ArrayList<Dstate> finals = new ArrayList<>();
        ArrayList<Dstate> unfinals = new ArrayList<>();

        //找到终结态FAnode(reserveWords的node优先考虑)
        ArrayList<NFANode> last = new ArrayList<>();
        for (NFANode node : nodes) {
            if (node.getState() == -1) {
                last.add(node);
            }
        }
        for (Dstate dstate : dstates) {
            List<NFANode> closure = dstate.getEpiclosure();
            for (NFANode node : last) {
                if (closure.contains(node)) {
                    dstate.setName(node.getName());
                    finals.add(dstate);
                    break;
                }
            }
            if (!finals.contains(dstate))
                unfinals.add(dstate);
        }

        partition.add(finals);
        partition.add(unfinals);

        ArrayList<ArrayList<Dstate>> newPartition = new ArrayList<>();
        newPartition.addAll(partition);
        //进一步分划
        //对分划中的每一个组G
        while (true) {
            for (int i = 0; i < partition.size(); i++) {
                ArrayList<Dstate> group = partition.get(i);
                ArrayList<ArrayList<Dstate>> newGroups = new ArrayList<>();
                //将group划分成更小的组
                for (Dstate dstate : group) {
                    if (newGroups.isEmpty()) {
                        ArrayList<Dstate> newgroup = new ArrayList<>();
                        newgroup.add(dstate);
                        newGroups.add(newgroup);
                    } else {
                        for (int j = 0; j < newGroups.size(); j++) {
                            ArrayList<Dstate> newgroup = newGroups.get(j);
                            if (newgroup.contains(dstate)) break;
                            if (stillSameGroup(newgroup.get(0), dstate, partition)) {
                                newgroup.add(dstate);
                                break;
                            }
                            if (newGroups.indexOf(newgroup) == (newGroups.size() - 1)) {
                                ArrayList<Dstate> singlegroup = new ArrayList<>();
                                singlegroup.add(dstate);
                                newGroups.add(singlegroup);
                            }
                        }
                    }
                }
                //在新的分划里面用newgroup来替代group
                if (newGroups.size() != 1) {
                    newPartition.remove(group);
                    for (ArrayList<Dstate> list : newGroups) {
                        newPartition.add(list);
                    }
                }
            }
            if (newPartition.size() == partition.size()) break;
            partition = newPartition;
        }

        //在各组中选择代表
        ArrayList<Dstate> finalDstates = new ArrayList<>();
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

    private boolean sameGroup(Dstate dstate1, Dstate dstate2, ArrayList<ArrayList<Dstate>> partition) {
        for (ArrayList<Dstate> group : partition
                ) {
            if (group.contains(dstate1) && group.contains(dstate2)) return true;
        }
        return false;
    }

    private boolean stillSameGroup(Dstate dstate1, Dstate dstate2, ArrayList<ArrayList<Dstate>> partition) {
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

    private Dstate findInDStates(Dstate target) {
        for (Dstate state : dstates) {
            if (state.equals(target)) {
                return state;
            }
        }
        dstates.add(target);
        return target;
    }

    private Dstate getMoveBy(char c, Dstate cur) {
        List<NFANode> list = new ArrayList<>();
        for (NFANode node : cur.getEpiclosure()) {
            if (node.getC() == c) {
                list.add(node.getOutnodes().get(0));
            }
        }
        if (list.isEmpty()) return null;
        return epiclosure(list);
    }

    private Dstate getUnmarkedState(ArrayList<Dstate> dstates) {
        for (Dstate dstate : dstates) {
            if (!dstate.isMarked()) return dstate;
        }
        return null;
    }

    private Dstate epiclosure(List<NFANode> list) {
        //原状态压栈,初始化返回值列表
        Stack<NFANode> stack = new Stack<>();
        List<NFANode> finalepiclosure = new ArrayList<>();
        for (NFANode NFANode : list) {
            stack.push(NFANode);
            finalepiclosure.add(NFANode);
        }

        //计算
        while (!stack.isEmpty()) {
            NFANode node = stack.pop();
            //如果出边不是有字母的边而且不是终结态
            if (node.getC() == '\0' && node.getState() != -1) {
                ArrayList<NFANode> outnodes = node.getOutnodes();
                for (NFANode NFANode : outnodes) {
                    if (!stack.contains(NFANode)) {
                        finalepiclosure.add(NFANode);
                        stack.push(NFANode);
                    }
                }
            }
        }
        return new Dstate(finalepiclosure);
    }

    private void getChildren(NFANode nfa) {
        int state = nfa.getState();
        nodes.add(nfa);
        nfa.setVisited();
        if (state == -1) return;
        ArrayList<NFANode> out = nfa.getOutnodes();
        for (int i = 0; i < out.size(); i++) {
            if (!out.get(i).visited) {
                getChildren(out.get(i));
            }
        }
    }


}
