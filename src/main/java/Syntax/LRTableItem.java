package Syntax;

import java.io.Serializable;

public class LRTableItem implements Serializable{
    private String variety;
    private int val;
    public LRTableItem(int val, String variety)
    {
        this.val = val;
        this.variety = variety;
    }

    public int getVal() {
        return val;
    }

    public String getVariety() {
        return variety;
    }
}

