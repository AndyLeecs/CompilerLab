package Lexical;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Writer {

    public static void output(List<Token> output, String inputFile) throws IOException {

        File outputFile = new File(inputFile + "res.txt");

        if (!outputFile.exists())
            outputFile.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, false));

        for (Token t : output) {
            bw.write(t.toString());
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

}
