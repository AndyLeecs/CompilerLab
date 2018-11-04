package Lexical;

import Lexical.lex.Dstate;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Launcher {
    private static final String name = "Inputwithfault";

    public static void main(String args[]) {
        try {
//            List<Dstate> finalminDFA = (List<Dstate>) Storage.load(new FileInputStream("generationfile"));
//            List<Dstate> finalminDFA = (List<Dstate>) Storage.deserialize(new File("generationfile"));
//            String gen = name+"_generationfile";
            String gen = "Input"+"_generationfile";
            List<Dstate> finalminDFA = (List<Dstate>) Storage.deserialize(new FileInputStream(gen));
            System.out.println(finalminDFA.get(0));
            char[] input = Reader.read(name + ".txt");
            int start = 0;
            int end;
            ArrayList<Token> tokens = new ArrayList<>();
            TokenAndPos tokenAndPos;
            int line = 1;
            while (input[start] != '$') {
                if (input[start] == '\n' || input[start] == ' ') {
                    if(input[start] == '\n') {
                        line++;
                    }
                    start++;
                    continue;
                }
                tokenAndPos = Simulator.check(start, input, finalminDFA);
                if (tokenAndPos != null) {
                    end = tokenAndPos.getEnd();
                    StringBuilder s = new StringBuilder();
                    for (int i = start; i <= end; i++) {
                        s.append(input[i]);
                    }
                    tokens.add(tokenAndPos.getToken());
                    start = end + 1;
                } else {
                    tokens.add(new Token("no matched pattern with char " + input[start] + "at line "+line));
                    Writer.output(tokens, name);
                    return;
                }
            }
            Writer.output(tokens, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
