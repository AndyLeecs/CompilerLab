package Lexical;

import java.util.List;
import java.util.Stack;

public class Simulator {

    public static TokenAndPos check(int start, char[] input, List<Dstate> finalminDFA) {
        char c;
        Dstate dstate = finalminDFA.get(0);
        Stack<Dstate> stack = new Stack<Dstate>();
        stack.push(dstate);
        int ptr = start;
        StringBuffer s = new StringBuffer();
        while (true) {
            c = input[ptr];
            if (c == '\n') return getTokenAndPos(start, stack, s);
//            System.out.println("start checking " + c);
            assert dstate != null;
            dstate = dstate.getMapValue(c);
            //回退
            if (dstate == null) {
                while (true) {
                    TokenAndPos name = getTokenAndPos(start, stack, s);
                    if (name != null) return name;
                }
            } else {
                stack.push(dstate);
                ptr++;
                s.append(c);
            }
        }
    }

    private static TokenAndPos getTokenAndPos(int start, Stack<Dstate> stack, StringBuffer s) {

        if (!stack.isEmpty()) {
            Dstate cur = stack.pop();
            String name = cur.getName();
            if (name.length() != 0) {
                return new TokenAndPos(name, s.toString(), start + s.length() - 1);
            }
        }
        return null;
    }
}
