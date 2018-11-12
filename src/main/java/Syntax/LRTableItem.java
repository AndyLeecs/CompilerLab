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

    public boolean isErr()
    {
        return variety.equals(Variety.err);
    }

    public int getVal() {
        return val;
    }

    public String getVariety() {
        return variety;
    }
}

