package Lexical;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class Storage {

    /**
     * 持久化对象
     */
    public static void store(Object obj, OutputStream out) throws IOException
    {
        ObjectOutputStream outputStream = new ObjectOutputStream(out);
        outputStream.writeObject(obj);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 加载对象
     */
    public static Object load(InputStream in) throws IOException,
            ClassNotFoundException
    {
        ObjectInputStream inputStream = new ObjectInputStream(in);
        Object obj = inputStream.readObject();
        inputStream.close();
        return obj;
    }

    public static void main(String args[])
    {
        try {
            Storage.store("a",new FileOutputStream(new File("a.xml")));
            String s = (String)Storage.load(new FileInputStream("a.xml"));
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
