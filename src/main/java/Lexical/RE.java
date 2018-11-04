package Lexical;

import java.util.ArrayList;
import java.util.Stack;

public class RE {
    private String name = "";
    private String content = "";
    public RE(String name, String content)
    {
        this.name = name;
        this.content = content;
    }

    public String getName(){
        return name;
    }

    public FANode toNFA(){
        String contentAfterAddingDots = getContentAfterAddingDots();
        System.out.println(contentAfterAddingDots);
        String postContent = getPostContent(contentAfterAddingDots);
        System.out.println(postContent);
        return postToNfa(postContent);
    }

    public FANode postToNfa(String s)
    {
        Stack<FANode> stack = new Stack<FANode>();
        for(int i = 0 ; i < s.length() ; i++)
        {
            char cur = s.charAt(i);
            if(!isOperator(cur)) {
                FANode single = single(cur);
                stack.push(single);
            }
            else{
                FANode later = stack.pop();
                FANode res = null;
                if(cur == '*')
                {
                    res = closure(later);
                }else {
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

    private FANode closure(FANode later)
    {
        //新建开始和结束节点
        FANode start = new FANode(0);
        FANode end = new FANode(-1);
//        start.addToOutNodes(later);
//        start.addToOutNodes(end);
        //开始节点连接到结束节点，和原来的开始节点上
        ArrayList<FANode> listToAddToStart = new ArrayList<FANode>();
        listToAddToStart.add(later);
        listToAddToStart.add(end);
        start.setOutnodes(listToAddToStart);
        //原来的结束节点连接到新结束节点和原来的开始节点上
        ArrayList<FANode> listToAdd = new ArrayList<FANode>();
        listToAdd.add(end);
        listToAdd.add(later);
        findLastAndAddOutNodes(later,listToAdd);
        //把该小自动机置终态
        start.setEndAt(end);
        return start;
    }
    private FANode union(FANode former, FANode later)
    {
        //新建开始和结束节点
        FANode start = new FANode(0);
        FANode end = new FANode(-1);
        //开始节点连接到原来的两个开始节点上
        ArrayList<FANode> listToAddToStart = new ArrayList<FANode>();
        listToAddToStart.add(former);
        listToAddToStart.add(later);
        start.setOutnodes(listToAddToStart);
//        start.addToOutNodes(former);
//        start.addToOutNodes(later);
//        System.out.println("start added");

        ArrayList<FANode> listToAdd = new ArrayList<FANode>();
        listToAdd.add(end);
        findLastAndAddOutNodes(former,listToAdd);
        findLastAndAddOutNodes(later,listToAdd);
//        System.out.println("end added");
        //把该小自动机置终态
        start.setEndAt(end);
        return start;
    }

    private FANode concat(FANode former, FANode later)
    {

//        ArrayList<FANode> toConcat = later.getOutnodes();
        //连接former和later
        ArrayList<FANode> toConcat  = new ArrayList<FANode>();
        toConcat.add(later);
        findLastAndAddOutNodes(former,toConcat);
        //把该小自动机置终结态为later的终结态
        former.setEndAt(later.getEndAt());
        return former;
    }

    private void findLastAndAddOutNodes(FANode node, ArrayList<FANode> outs)
    {
        FANode last = findLast(node);
        last.setOutnodes(outs);
//        for(FANode n : outs)
//            node.addToOutNodes(n);
    }

    private FANode findLast(FANode node)
    {
        FANode end = node.getEndAt();
        node.setEndAt(null);
        return end;
    }
    private FANode single(char c){
        FANode start = new FANode(c, 1);
        FANode end = new FANode(-1);
        start.addToOutNodes(end);
        start.setEndAt(end);
        return start;
    }

    private String getPostContent(String s){
        Stack<Character> stack = new Stack<Character>();
        StringBuffer res = new StringBuffer();
        int i = 0;
        while (i < s.length()) {
            char cur = s.charAt(i);

            if(!isOperator(cur))
            {
                res.append(cur);
            }
            else if(stack.isEmpty() && isOperator(cur))
            {
                stack.push(cur);
            }
            else if(cur == '(')
            {
                stack.push(cur);
            }
            else if(cur == ')')
            {
                while (stack.peek() != '(')
                    res.append(stack.pop());
                stack.pop();
            }
            else
            {
                while(!stack.isEmpty()&&isOperator(stack.peek()) && !smallerPrio(stack.peek(), cur))
                {
                    res.append(stack.pop());
                }
                stack.push(cur);
            }
            i++;
//            System.out.println(res.toString());
//            if(!stack.isEmpty())System.out.println("stack"+stack.peek());
        }

        while(!stack.isEmpty())
        {
            res.append(stack.pop());
        }
    return res.toString();
    }

    //a的优先级比b小
    private boolean smallerPrio(char a, char b)
    {
        assert isOperator(a) && isOperator(b);
        if(a == '*')return false;
        if(a == '|' && b != '*')return false;
        if(a == '.' && b == '.')return false;
        return true;
    }

    private String getContentAfterAddingDots() {
        StringBuffer contentAfterAddingDots = new StringBuffer();
        char[] chars = content.toCharArray();
        char last = '\0';
        for (int i = 0 ; i < chars.length ; i++)
        {
            char cur = chars[i];
            if(i != 0){
                if((last == ')' || last == '*' || !isOperator(last))&& (cur == '(' || !isOperator(cur)))
                    contentAfterAddingDots.append('.');
            }
            contentAfterAddingDots.append(cur);
            last = cur;
        }
        String s = contentAfterAddingDots.toString();
        return s;
    }

    private boolean isOperator(char c)
    {
        return c == '|' || c == '(' || c == ')' || c == '*' || c == '.';
    }

    public static FANode mergeNFA(ArrayList<FANode> list, ArrayList<String> names)
    {
        System.out.println("listsize"+list.size());
        System.out.println("namessize"+names.size());
        FANode start = new FANode(2);
        for (int i  = 0 ; i < list.size() ; i++) {
            FANode fa = list.get(i);
            System.out.println("endAt" + fa.getEndAt());
            fa.getEndAt().setName(names.get(i));
            System.out.println(fa.getEndAt()+ " "+names.get(i));
            start.addToOutNodes(fa);
        }
        return start;
    }

    public static void main(String args[])
    {
        //tocheck DFA
//          System.out.println(new RE("","").postToNfa("a*b*|*ab|*.ab|."));
//        System.out.println(new RE("","").postToNfa("a*")); pass
//        System.out.println(new RE("","").postToNfa("ab.")); // pass with indexoutofbounds
//       System.out.println(new RE("","").postToNfa("ab|")); //pass with 2 special line(sl)
//        System.out.println(new RE("","").postToNfa("ab|*")); //pass with 2 special line(sl)
//        System.out.println(new RE("","").postToNfa("a*b*|*")); //pass with 2 special line(sl)
//          System.out.println(new RE("","").postToNfa("a*b*.")); //pass
//        System.out.println(new RE("","").postToNfa("a*b*|")); //pass
//        ArrayList<FANode> list = new ArrayList<FANode>();
//        list.add(new RE("","").postToNfa("a*"));
//        list.add(new RE("","").postToNfa("ab."));
//        System.out.println(mergeNFA(list));
//        System.out.println();
       new RE("","").postToNfa("a*b.");
//        new RE("","").postToNfa("a*");
//        System.out.println();
//        System.out.println(new RE("","").getPostContent("a*.b"));
//       System.out.println(new RE("","a*b").getContentAfterAddingDots());
    }
}
