package Lexical;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Writer {

    public static void output(List<Token> output, String inputFile) throws IOException {

//        String[] splits = inputFile.split("\\.");
//        File outputFile = new File(splits[0] + "res.txt");
        File outputFile = new File(inputFile+ "res.txt");

        if (!outputFile.exists())
            outputFile.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, false));
//        BufferedWriter bw =
//                new BufferedWriter(new OutputStreamWriter
//                        (new FileOutputStream(new File(Lex.class.getClassLoader().getResource(inputFile+"res").getPath())),
//                                "UTF-8"));
        for (Token t : output) {
            bw.write(t.toString());
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

    private static String getResourcePath() {
        try {
            URI resourcePathFile = System.class.getResource("/RESOURCE_PATH").toURI();
            String resourcePath = Files.readAllLines(Paths.get(resourcePathFile)).get(0);
            URI rootURI = new File("").toURI();
            URI resourceURI = new File(resourcePath).toURI();
            URI relativeResourceURI = rootURI.relativize(resourceURI);
            return relativeResourceURI.getPath();
        } catch (Exception e) {
            return null;
        }
    }
}
