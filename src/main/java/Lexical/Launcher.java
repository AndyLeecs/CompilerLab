package Lexical;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Launcher {
    private static final String name = "simple";

    public static void main(String args[]) {
        try {
            List<Dstate> finalminDFA = (List<Dstate>) Storage.load(new FileInputStream("a.xml"));
            char[] input = Reader.read(name + ".txt");
            int start = 0;
            int end = 0;
            ArrayList<Token> tokens = new ArrayList<Token>();
            TokenAndPos tokenAndPos = null;
            while (input[start] != '$') {
                if (input[start] == '\n' || input[start] == ' ') {
                    start++;
                    continue;
                }
                tokenAndPos = Simulator.check(start, input, finalminDFA);
                if (tokenAndPos != null) {
                    end = tokenAndPos.getEnd();
                    StringBuffer s = new StringBuffer();
                    for (int i = start; i <= end; i++) {
                        s.append(input[i]);
                    }
                    tokens.add(tokenAndPos.getToken());
                    start = end + 1;
                } else {
                    System.out.println("no matched pattern");
                    return;
                }
            }
            Writer.output(tokens, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
