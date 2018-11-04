package Lexical;

import java.io.IOException;

public class Launcher {
    public static void main(String args[])
    {
        try {
            new Lex().handle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
