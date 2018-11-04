package Lexical;

import java.util.ArrayList;
import java.util.Stack;

public class RE {
    private String content = "";

    public RE(String content) {
        this.content = content;
    }

    public static FANode mergeNFA(ArrayList<FANode> list, ArrayList<String> names) {
        FANode start = new FANode(2);
        for (int i = 0; i < list.size(); i++) {
            FANode fa = list.get(i);
            fa.getEndAt().setName(names.get(i));
            start.addToOutNodes(fa);
        }
        return start;
    }

    public FANode toNFA() {
        String contentAfterAddingDots = getContentAfterAddingDots();
        String postContent = getPostContent(contentAfterAddingDots);
        return postToNfa(postContent);
    }

    public FANode postToNfa(String s) {
        Stack<FANode> stack = new Stack<FANode>();
        for (int i = 0; i < s.length(); i++) {
            char cur = s.charAt(i);
            if (!isOperator(cur)) {
                FANode single = single(cur);
                stack.push(single);
            } else {
                FANode later = stack.pop();
                FANode res = null;
                if (cur == '*') {
                    res = closure(later);
                } else {
                    FANode former = stack.pop();

                    if (cur == '.') {
                        res = concat(former, later);
                    } else if (cur == '|') {
                        res = union(former, later);
                    }
                }
                stack.push(res);
            }
        }

        FANode node = stack.pop();
        return node;
    }

    private FANode closure(FANode later) {
        //新建开始和结束节点
        FANode start = new FANode(0);
        FANode end = new FANode(-1);
        //开始节点连接到结束节点，和原来的开始节点上
        ArrayList<FANode> listToAddToStart = new ArrayList<FANode>();
        listToAddToStart.add(later);
        listToAddToStart.add(end);
        start.setOutnodes(listToAddToStart);
        //原来的结束节点连接到新结束节点和原来的开始节点上
        ArrayList<FANode> listToAdd = new ArrayList<FANode>();
        listToAdd.add(end);
        listToAdd.add(later);
        findLastAndAddOutNodes(later, listToAdd);
        //把该小自动机置终态
        start.setEndAt(end);
        return start;
    }

    private FANode union(FANode former, FANode later) {
        //新建开始和结束节点
        FANode start = new FANode(0);
        FANode end = new FANode(-1);
        //开始节点连接到原来的两个开始节点上
        ArrayList<FANode> listToAddToStart = new ArrayList<FANode>();
        listToAddToStart.add(former);
        listToAddToStart.add(later);
        start.setOutnodes(listToAddToStart);

        ArrayList<FANode> listToAdd = new ArrayList<FANode>();
        listToAdd.add(end);
        findLastAndAddOutNodes(former, listToAdd);
        findLastAndAddOutNodes(later, listToAdd);
        //把该小自动机置终态
        start.setEndAt(end);
        return start;
    }

    private FANode concat(FANode former, FANode later) {

//        ArrayList<FANode> toConcat = later.getOutnodes();
        //连接former和later
        ArrayList<FANode> toConcat = new ArrayList<FANode>();
        toConcat.add(later);
        findLastAndAddOutNodes(former, toConcat);
        //把该小自动机置终结态为later的终结态
        former.setEndAt(later.getEndAt());
        return former;
    }

    private void findLastAndAddOutNodes(FANode node, ArrayList<FANode> outs) {
        FANode last = findLast(node);
        last.setOutnodes(outs);
    }

    private FANode findLast(FANode node) {
        FANode end = node.getEndAt();
        node.setEndAt(null);
        return end;
    }

    private FANode single(char c) {
        FANode start = new FANode(c, 1);
        FANode end = new FANode(-1);
        start.addToOutNodes(end);
        start.setEndAt(end);
        return start;
    }

    private String getPostContent(String s) {
        Stack<Character> stack = new Stack<Character>();
        StringBuffer res = new StringBuffer();
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
        StringBuffer contentAfterAddingDots = new StringBuffer();
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
        String s = contentAfterAddingDots.toString();
        return s;
    }

    private boolean isOperator(char c) {
        return c == '|' || c == '(' || c == ')' || c == '*' || c == '.';
    }

}
