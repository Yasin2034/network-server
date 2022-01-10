import java.io.*;

public class Main {

    public static File file;
    public static OutputStreamWriter fileWriter;

    public static void main(String[] args) throws IOException {
        file = new File("log.txt");
        fileWriter = new OutputStreamWriter(new FileOutputStream(file));
        Server server = new Server();
        server.run();
        fileWriter.close();
    }
}
