package Lexical;

import java.io.*;

public class Reader {
    public static char[] read(String file) throws IOException {
        BufferedReader br =
                new BufferedReader(new InputStreamReader
                        (new FileInputStream(new File(Lex.class.getClassLoader().getResource(file).getPath())),
                                "UTF-8"));

        String line;
        char[] res = new char[1000];
        int idx = 0;
        while ((line = br.readLine()) != null) {
            int cur = 0;
            char c;
            while (cur < line.length()) {
                if ((c = line.charAt(cur)) != '\t') {
                    res[idx++] = c;
                }
                cur++;
            }
            res[idx++] = '\n';
        }
        res[idx] = '$';
        br.close();
        return res;
    }
}
