package Lexical.lex;

import java.io.Serializable;
import java.util.ArrayList;

public class FANode implements Serializable {
    boolean visited = false;
    private int state; //1代表只有一条出边，此时char为出边上的字母，0代表有两条epi出边，-1代表终结态,2表示有多条epi出边,-2表示未指定其状态
    private ArrayList<FANode> outnodes = new ArrayList<>();
    private char c = '\0';
    private FANode endAt;
    private String name = "";//用于标识终结态的名字

    public FANode() {
        this.state = -2;
    }

    public FANode(int state) {
        this.state = state;
    }

    public FANode(char c, int state) {
        this.c = c;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getC() {
        return c;
    }

    public FANode getEndAt() {
        return endAt;
    }

    public void setEndAt(FANode endAt) {
        this.endAt = endAt;
    }

    public int getState() {
        return state;
    }

    public void addToOutNodes(FANode FANode) {
        outnodes.add(FANode);
        checkState();
    }

    private void checkState() {
        if (outnodes.size() == 1) {
            state = 1;
        } else if (outnodes.size() == 2)
            state = 0;
        else
            state = 2;
    }

    public void setVisited() {
        visited = true;
    }
//    @Override
//    public String toString() {
//        StringBuffer s = new StringBuffer();
//        s.append("state" + state + "char" + c + "\n");
//        setVisited();
//        if(state != -1) {
//            ArrayList<FANode> nodes = outnodes;
//            for (int i = 0; i < nodes.size(); i++) {
//                if(!nodes.get(i).visited)
//                s.append(nodes.get(i).toString());
//            }
//        }
//        return s.toString();
//    }

    public ArrayList<FANode> getOutnodes() {
        return outnodes;
    }

    public void setOutnodes(ArrayList<FANode> outnodes) {
        this.outnodes = outnodes;
        checkState();
//        System.out.println(state);
    }
}
