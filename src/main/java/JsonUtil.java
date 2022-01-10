import lombok.experimental.UtilityClass;

import javax.json.Json;
import java.io.StringWriter;

@UtilityClass
public class JsonUtil {

    public String convertToJsonMessage(String code, String message){
        StringWriter stringWriter = new StringWriter();
        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                .add("code",code)
                .add("username", "Server")
                .add("message", message)
                .build());
        try {
            Main.fileWriter.write(stringWriter.toString() + "\n");
            Main.fileWriter.flush();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return stringWriter.toString();
    }

    public String submitUserPort(String code, String port){
        StringWriter stringWriter = new StringWriter();
        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                .add("code",code)
                .add("port", port)
                .build());
        try {
            Main.fileWriter.write(stringWriter.toString() + "\n");
            Main.fileWriter.flush();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return stringWriter.toString();
    }



}
