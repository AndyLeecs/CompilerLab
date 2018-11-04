package Lexical.lex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dstate implements Serializable {
    private int tag; //标记在最终partition中group的index
    private List<FANode> epiclosure = new ArrayList<>();
    private Map<Character, Dstate> Dtrans = new HashMap<>();
    private String name = "";//标记终结态的名字

    public Dstate() {
    }

    public Dstate(List<FANode> epiclosure) {
        this.epiclosure = epiclosure;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Character, Dstate> getDtrans() {
        return Dtrans;
    }

    public Dstate getMapValue(char c) {
        return Dtrans.get(c);
    }

    public void addToMap(char c, Dstate faNode) {
        Dtrans.put(c, faNode);
    }

    public List<FANode> getEpiclosure() {
        return epiclosure;
    }

    public boolean isMarked() {
        return !Dtrans.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        List<FANode> list = ((Dstate) obj).epiclosure;
        if (list.size() != epiclosure.size()) return false;
        for (FANode node1 : list) {
            for (FANode node2 : epiclosure) {
                if (node1 == node2) break;
                if (epiclosure.indexOf(node2) == (epiclosure.size() - 1)) {
                    return false;
                }
            }
        }
        return true;
    }
}
