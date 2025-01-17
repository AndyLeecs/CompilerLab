package Lexical;

import Lexical.lex.Dstate;

import java.util.List;
import java.util.Stack;

public class Simulator {
    public static TokenAndPos check(int start, char[] input, List<Dstate> finalminDFA) {
        char c;
        Dstate dstate = finalminDFA.get(0);
        Stack<Dstate> stack = new Stack<>();
        stack.push(dstate);
        int ptr = start;
        StringBuffer s = new StringBuffer();
        while (true) {
            c = input[ptr];
            if (c == '\n') return getTokenAndPos(start, stack, s);
            assert dstate != null;
            dstate = dstate.getMapValue(c);
            //回退
            if (dstate == null) {
                    return getTokenAndPos(start, stack, s);
            } else {
                stack.push(dstate);
                ptr++;
                s.append(c);
            }
        }
    }

    private static TokenAndPos getTokenAndPos(int start, Stack<Dstate> stack, StringBuffer s) {
        while (!stack.isEmpty()) {
            Dstate cur = stack.pop();
            String name = cur.getName();
            System.out.println(name);
            if (name.length() != 0) {
                return new TokenAndPos(name, s.toString(), start + s.length() - 1);
            }
        }
        return null;
    }
}
