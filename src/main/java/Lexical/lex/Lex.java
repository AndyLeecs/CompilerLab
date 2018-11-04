package Lexical.lex;

import Lexical.Storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lex {
    private static final String name = "single";

    public static void main(String args[]) {
        try {
            new Lex().handle();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handle() throws IOException, ClassNotFoundException {
        BufferedReader br =
                new BufferedReader(new InputStreamReader
                        (new FileInputStream(new File(Objects.requireNonNull(Lex.class.getClassLoader().getResource(name + "rule.txt")).getPath())),
                                "UTF-8"));
        String line;
        ArrayList<RE> reList = new ArrayList<>();
        DFA.allow = br.readLine().toCharArray();
        ArrayList<String> names = new ArrayList<>();
        while ((line = br.readLine()) != null && line.length() > 0) {
            String[] strs = line.split(" ");
            reList.add(new RE(strs[1]));
            names.add(strs[0]);
        }
        br.close();
        ArrayList<NFANode> nfaList = new ArrayList<>();
        for (RE re : reList) {
            NFANode fanode = re.toNFA();
            nfaList.add(fanode);
        }
        List<Dstate> finalminDFA = new DFA().toMinDFA(nfaList, names);
//        Storage.store(finalminDFA, new FileOutputStream(new File("generationfile")));
        try {
            Storage.serialize(finalminDFA, new File(name+"_generationfile"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
