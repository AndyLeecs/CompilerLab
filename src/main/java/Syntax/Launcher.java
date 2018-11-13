package Syntax;

import java.io.*;
import java.util.*;

public class Launcher {

    public static void main(String args[])
    {
        int id = 1;
        try (BufferedReader br = new BufferedReader(new InputStreamReader
                (new FileInputStream(new File(Launcher.class.getClassLoader().getResource("sentence"+id+".txt").getPath())),
                        "UTF-8"))) {
            String string = br.readLine();
            br.close();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                    (new FileInputStream(new File(Launcher.class.getClassLoader().getResource("rule"+id+".txt").getPath())),
                            "UTF-8"));
            //初始化yacc
            String nonterminal = bufferedReader.readLine();
            String terminal = bufferedReader.readLine();
            List<Character> nonterminallist = new ArrayList<>(nonterminal.length());
            List<Character> terminallist = new ArrayList<>(terminal.length());
            List<Character> alllist = new ArrayList<>();

            for (char c : nonterminal.toCharArray())
            {
                nonterminallist.add(c);
                alllist.add(c);
            }

            for (char c : terminal.toCharArray())
            {
                terminallist.add(c);
                alllist.add(c);
            }

            List<String> syntax = new ArrayList<>();
            String line;
            while((line = bufferedReader.readLine())!=null && !line.isEmpty())
            {
                syntax.add(line);
            }

            char[] sentence = string.toCharArray();//符号串,以#开始，$结尾
            int ip = 0;//指向的符号位置
            Stack<Integer> stateStack = new Stack<>();//状态栈
            Stack<Character> charStack = new Stack<>();//符号栈

            List<String> rules = new ArrayList<>();

            LRHelper.syntaxlist = syntax;
            LRHelper.terminalList = terminallist;
            LRHelper.nonterminalList = nonterminallist;
            LRHelper.alllist = alllist;
            LRTable lrTable = LRHelper.getLRTable();

            //开始
            //0入状态栈
            stateStack.push(0);
            //'#'入符号栈,ip指向sentence的第一个符号
            charStack.push('#');

            //开始状态为！
            //重复执行如下过程
            while (!(sentence[ip] == '$' && charStack.peek() == '!'))
            {
                //令S是状态栈栈顶，a是ip所指向的符号
                int s = stateStack.peek();
                char a = sentence[ip];
               LRTableItem item = lrTable.action(s,a);
                System.out.println("current item "+s+" "+a +" " + item.getVariety());
                //移进
                if(item.getVariety().equals(Variety.S))
                {
                    //a入符号栈
                    charStack.push(a);
                    //状态j入状态栈
                    stateStack.push(item.getVal());
                    //ip指向下一个符号
                    ip++;
                }
                //规约
                else if(item.getVariety().equals(Variety.r))
                {
                    //获得产生式
                    String rule = lrTable.getRule(item.getVal());
                    //计算|β|
                    int beta = rule.length() - 3;
                    //        从符号栈弹出|β|个符号；
                    //        从状态栈弹出|β|个符号；
                    while (beta>0)
                    {
                        charStack.pop();
                        stateStack.pop();
                        beta--;
                    }
                    //        令S’是状态栈栈顶；
                    int top = stateStack.peek();
                    //        A入符号栈;
                    char A = rule.charAt(0);
                    charStack.push(A);
                    //        goto[S’,A]入状态栈;
                    stateStack.push(lrTable.goTo(top,A));
                    //将产生式加入输出列表
                    System.out.println("current rule "+rule);
                    rules.add(rule);
                }
                //接受
                else if(item.getVariety().equals(Variety.acc))
                {
                    break;
                }
                else
                {
                    rules.add("error");
                    break;
                }
            }

            File outputFile = new File("res"+id + ".txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, false));

            for (String rule:rules) {
                bw.write(rule);
                bw.newLine();
            }

            bw.flush();
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
