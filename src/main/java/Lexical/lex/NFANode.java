package Lexical.lex;

import java.io.Serializable;
import java.util.ArrayList;

public class NFANode implements Serializable {
    private static final long serialVersionUID = 1L ;
    boolean visited = false;
    private int state; //1代表只有一条出边，此时char为出边上的字母，0代表有两条epi出边，-1代表终结态,2表示有多条epi出边,-2表示未指定其状态
    private ArrayList<NFANode> outnodes = new ArrayList<>();
    private char c = '\0';
    private NFANode endAt;
    private String name = "";//用于标识终结态的名字

    public NFANode() {
        this.state = -2;
    }

    NFANode(int state) {
        this.state = state;
    }

    NFANode(char c, int state) {
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

    public NFANode getEndAt() {
        return endAt;
    }

    public void setEndAt(NFANode endAt) {
        this.endAt = endAt;
    }

    public int getState() {
        return state;
    }

    public void addToOutNodes(NFANode NFANode) {
        outnodes.add(NFANode);
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

    public ArrayList<NFANode> getOutnodes() {
        return outnodes;
    }

    public void setOutnodes(ArrayList<NFANode> outnodes) {
        this.outnodes = outnodes;
        checkState();
    }
}
