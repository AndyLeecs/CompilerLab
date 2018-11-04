package Lexical.lex;

import java.util.ArrayList;
import java.util.Stack;

public class RE {
    private String content;

    RE(String content) {
        this.content = content;
    }

    public static NFANode mergeNFA(ArrayList<NFANode> list, ArrayList<String> names) {
        NFANode start = new NFANode(2);
        for (int i = 0; i < list.size(); i++) {
            NFANode fa = list.get(i);
            fa.getEndAt().setName(names.get(i));
            start.addToOutNodes(fa);
        }
        return start;
    }

    public NFANode toNFA() {
        String contentAfterAddingDots = getContentAfterAddingDots();
        String postContent = getPostContent(contentAfterAddingDots);
        return postToNfa(postContent);
    }

    private NFANode postToNfa(String s) {
        Stack<NFANode> stack = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            char cur = s.charAt(i);
            if (!isOperator(cur)) {
                NFANode single = single(cur);
                stack.push(single);
            } else {
                NFANode later = stack.pop();
                NFANode res = null;
                if (cur == '*') {
                    res = closure(later);
                } else {
                    NFANode former = stack.pop();

                    if (cur == '.') {
                        res = concat(former, later);
                    } else if (cur == '|') {
                        res = union(former, later);
                    }
                }
                stack.push(res);
            }
        }
        return stack.pop();
    }

    private NFANode closure(NFANode later) {
        //新建开始和结束节点
        NFANode start = new NFANode(0);
        NFANode end = new NFANode(-1);
        //开始节点连接到结束节点，和原来的开始节点上
        ArrayList<NFANode> listToAddToStart = new ArrayList<>();
        listToAddToStart.add(later);
        listToAddToStart.add(end);
        start.setOutnodes(listToAddToStart);
        //原来的结束节点连接到新结束节点和原来的开始节点上
        ArrayList<NFANode> listToAdd = new ArrayList<>();
        listToAdd.add(end);
        listToAdd.add(later);
        findLastAndAddOutNodes(later, listToAdd);
        //把该小自动机置终态
        start.setEndAt(end);
        return start;
    }

    private NFANode union(NFANode former, NFANode later) {
        //新建开始和结束节点
        NFANode start = new NFANode(0);
        NFANode end = new NFANode(-1);
        //开始节点连接到原来的两个开始节点上
        ArrayList<NFANode> listToAddToStart = new ArrayList<>();
        listToAddToStart.add(former);
        listToAddToStart.add(later);
        start.setOutnodes(listToAddToStart);

        ArrayList<NFANode> listToAdd = new ArrayList<>();
        listToAdd.add(end);
        findLastAndAddOutNodes(former, listToAdd);
        findLastAndAddOutNodes(later, listToAdd);
        //把该小自动机置终态
        start.setEndAt(end);
        return start;
    }

    private NFANode concat(NFANode former, NFANode later) {

//        ArrayList<NFANode> toConcat = later.getOutnodes();
        //连接former和later
        ArrayList<NFANode> toConcat = new ArrayList<>();
        toConcat.add(later);
        findLastAndAddOutNodes(former, toConcat);
        //把该小自动机置终结态为later的终结态
        former.setEndAt(later.getEndAt());
        return former;
    }

    private void findLastAndAddOutNodes(NFANode node, ArrayList<NFANode> outs) {
        NFANode last = findLast(node);
        last.setOutnodes(outs);
    }

    private NFANode findLast(NFANode node) {
        NFANode end = node.getEndAt();
        node.setEndAt(null);
        return end;
    }

    private NFANode single(char c) {
        NFANode start = new NFANode(c, 1);
        NFANode end = new NFANode(-1);
        start.addToOutNodes(end);
        start.setEndAt(end);
        return start;
    }

    private String getPostContent(String s) {
        Stack<Character> stack = new Stack<>();
        StringBuilder res = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char cur = s.charAt(i);

            if (!isOperator(cur)) {
                res.append(cur);
            } else if (stack.isEmpty() && isOperator(cur)) {
                stack.push(cur);
            } else if (cur == '(') {
                stack.push(cur);
            } else if (cur == ')') {
                while (stack.peek() != '(')
                    res.append(stack.pop());
                stack.pop();
            } else {
                while (!stack.isEmpty() && isOperator(stack.peek()) && !smallerPrio(stack.peek(), cur)) {
                    res.append(stack.pop());
                }
                stack.push(cur);
            }
            i++;
        }

        while (!stack.isEmpty()) {
            res.append(stack.pop());
        }
        return res.toString();
    }

    //a的优先级比b小
    private boolean smallerPrio(char a, char b) {
        assert isOperator(a) && isOperator(b);
        if (a == '*') return false;
        if (a == '|' && b != '*') return false;
        if (a == '.' && b == '.') return false;
        return true;
    }

    private String getContentAfterAddingDots() {
        StringBuilder contentAfterAddingDots = new StringBuilder();
        char[] chars = content.toCharArray();
        char last = '\0';
        for (int i = 0; i < chars.length; i++) {
            char cur = chars[i];
            if (i != 0) {
                if ((last == ')' || last == '*' || !isOperator(last)) && (cur == '(' || !isOperator(cur)))
                    contentAfterAddingDots.append('.');
            }
            contentAfterAddingDots.append(cur);
            last = cur;
        }
        return contentAfterAddingDots.toString();
    }

    private boolean isOperator(char c) {
        return c == '|' || c == '(' || c == ')' || c == '*' || c == '.';
    }

}
