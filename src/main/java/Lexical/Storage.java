package Lexical;

import java.io.*;
import java.util.Base64;

public class Storage {
    //序列化
    public static void serialize(Object obj, File out) throws Exception{
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(obj);
        objOut.flush();
        byte[] binary = byteOut.toByteArray();
//        String str = byteOut.toString("ISO-8859-1");//此处只能是ISO-8859-1,但是不会影响中文使用
        String str = Base64.getEncoder().encodeToString(binary);
        BufferedWriter writer = new BufferedWriter(new FileWriter(out));
        writer.write(str);
        writer.flush();
        writer.close();
    }

    //反序列化
    public static Object deserialize(FileInputStream tps) {
        Object obj = null;
        try {
            byte[] buf = new byte[tps.available()];
            tps.read(buf);
            tps.close();
            String template = new String(buf);
            System.out.println(template);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(Base64.getDecoder().decode(template));
//            ByteArrayInputStream byteIn = new ByteArrayInputStream(buf);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            obj = objIn.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
