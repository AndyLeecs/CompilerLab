package Lexical.lex;

import Lexical.Storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lex {
    private static final String name = "simple";

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
        ArrayList<FANode> nfaList = new ArrayList<>();
        for (RE re : reList) {
            FANode fanode = re.toNFA();
            nfaList.add(fanode);
        }
        List<Dstate> finalminDFA = new DFA().toMinDFA(nfaList, names);
        for (Dstate dstate : finalminDFA) {
            if (dstate.getName().equals("reservedWords")) {
                System.out.println(dstate);
                System.out.println("find reservedWords");
            }
        }
        Storage.store(finalminDFA, new FileOutputStream(new File("a.xml")));
        System.out.println("store finished");
    }
}
