package Syntax;

public class Kernel {
    private int ptr = 3;//·的位置
    private int proidx;//产生式的标号

    public int getProidx() {
        return proidx;
    }

    public char getHead()
    {
        return LRHelper.syntaxlist.get(proidx).charAt(0);
    }

    public boolean isFinal()
    {
        return ptr>= LRHelper.syntaxlist.get(proidx).length();
    }

    public char getNext(){
        String s = LRHelper.syntaxlist.get(proidx);
        if (s.length() > ptr) {
            return s.charAt(ptr);
        }
        else return '\0';
    }

    public char getProAfterCur()
    {
        int tmp = ptr+1;
        String s = LRHelper.syntaxlist.get(proidx);
        if (s.length() > tmp) {
            return s.charAt(tmp);
        }
        else return '\0';
    }

    public Kernel(int proidx)
    {
        this.proidx = proidx;
    }

    public Kernel getKernelAfterStepMove()
    {
        Kernel k = new Kernel(proidx);
        k.ptr = ptr + 1;
        return k;
    }

    public boolean isCurCharacter(char c)
    {
       return getNext() == c;
    }

    @Override
    public boolean equals(Object obj) {
        Kernel k = (Kernel)obj;
        return k.ptr == ptr && k.proidx == proidx;
    }

    @Override
    public String toString() {
        return "ptr"+ptr+" proidx"+proidx;
    }
}
