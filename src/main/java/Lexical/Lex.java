package Lexical;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Lex {
    private static final String name = "Input";
    public void handle() throws IOException {
        BufferedReader br =
                new BufferedReader(new InputStreamReader
                        (new FileInputStream(new File(Lex.class.getClassLoader().getResource(name+"rule.txt").getPath())),
                                "UTF-8"));
        String line;
        ArrayList<RE> reList = new ArrayList<RE>();
        DFA.allow = br.readLine().toCharArray();
        ArrayList<String> names = new ArrayList<String>();
        while ((line = br.readLine()) != null && line.length() > 0) {
            String [] strs = line.split(" ");
            //TODO: remove str[0] from RE
            reList.add(new RE(strs[0], strs[1]));
            names.add(strs[0]);
        }
        br.close();
        ArrayList<FANode> nfaList = new ArrayList<FANode>();
        for (RE re:reList) {
            FANode fanode = re.toNFA();
            nfaList.add(fanode);
        }
//        FANode mergedNFA = RE.mergeNFA(nfaList);
        List<Dstate> finalminDFA = DFA.toMinDFA(nfaList, names);
        for (Dstate dstate : finalminDFA)
        {
            if (dstate.getName().equals("reservedWords")) {
                System.out.println(dstate);
                System.out.println("find reservedWords");
            }
        }
        char[] input = Reader.read(name+".txt");
        int start = 0;
        int end = 0;
        ArrayList<Token> tokens = new ArrayList<Token>();
        TokenAndPos tokenAndPos = null;
        while(input[start] != '$') {
            if(input[start] == '\n' || input[start] == ' ') {
                start++;
                continue;
            }
                tokenAndPos = DFA.check(start, input, finalminDFA);
                if(tokenAndPos != null){
                end = tokenAndPos.getEnd();
                    StringBuffer s = new StringBuffer();
                    for(int i = start ; i <= end; i++){
                        s.append(input[i]);
                    }
                    tokens.add(tokenAndPos.getToken());
                    start = end + 1;
                }
                 else
                    {
                        System.out.println("no matched pattern"); //后添加具体错误信息
                        return;
                    }
            }
//        for(Token token : tokens){
////            System.out.println(token);
////        }
        Writer.output(tokens, this.getClass().getClassLoader().getResource(name+".txt").getPath());
    }
}
