package Syntax;

public class Item {
    private Kernel kernel;
    private char lookahead;

    public int getProidx()
    {
        return kernel.getProidx();
    }

    public char getHead()
    {
        return kernel.getHead();
    }

    public char getNext()
    {
        return kernel.getNext();
    }

    public boolean isFinal()
    {
        return kernel.isFinal();
    }

    public char getProAfterCur()
    {
        return kernel.getProAfterCur();
    }
    public char getLookahead()
    {
        return lookahead;
    }

    public Item getItemAfterStepMove()
    {
        return new Item(kernel.getKernelAfterStepMove(), lookahead);
    }

    public boolean isCurCharacter(char c)
    {
        return kernel.isCurCharacter(c);
    }

    public Item(int proidx, char lookahead)
    {
        this.kernel = new Kernel(proidx);
        this.lookahead = lookahead;
    }

    public Item(Kernel kernel, char lookahead)
    {
        this.kernel = kernel;
        this.lookahead = lookahead;
    }

    @Override
    public boolean equals(Object obj) {
        Item tmp = (Item)obj;
        if (lookahead!=tmp.lookahead)
            return false;
        if (!kernel.equals(tmp.kernel))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return  kernel.toString()+" "+lookahead;
    }
}
