import javax.json.Json;
import java.io.StringWriter;

public class Util {
    public static String formatMessage(String username, String message){
        StringWriter stringWriter = new StringWriter();
        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                .add("username", username)
                .add("message", message)
                .build());
        return stringWriter.toString();
    }
}
