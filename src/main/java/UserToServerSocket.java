import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.*;
import java.net.Socket;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
public class UserToServerSocket extends Thread{

    private final Server server;
    private final Socket socket;
    private PrintWriter sender;
    private BufferedReader reader;
    private User user;
    private long lastUpdatedTime = System.currentTimeMillis();

    @Override
    public void run() {
        try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                sender = new PrintWriter(socket.getOutputStream(), true);
                listen(reader);

        } catch(Exception e ) {
            e.printStackTrace();
        }
    }

    private void listen(BufferedReader reader) {
        while (true) {
            JsonObject jsonObject = Json.createReader(reader).readObject();
            String code = jsonObject.getString("code");
            if (code.equalsIgnoreCase(Codes.LOGIN)){
                String username = jsonObject.getString("username");
                String password = jsonObject.getString("password");
                User findedUser = server.getUsers().stream().filter(u-> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
                if(Objects.isNull(findedUser)){
                    sender.println(JsonUtil.convertToJsonMessage(Codes.LOGIN_FAULT,""));
                }
                else{
                    if(findedUser.getPassword().equalsIgnoreCase(password)){
                        StringWriter stringWriter = new StringWriter();
                        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                                .add("code", Codes.LOGIN_SUCCESS)
                                .add("username", username)
                                .build());
                        findedUser.setPort(jsonObject.getString("port"));
                        server.getOnlineUsers().remove(findedUser);
                        server.getOnlineUsers().add(findedUser);
                        user = findedUser;
                        sender.println(stringWriter.toString());
                    }else{
                        sender.println(JsonUtil.convertToJsonMessage(Codes.LOGIN_FAULT,""));
                    }
                }
            }else if(code.equalsIgnoreCase(Codes.REGISTER)){
                String username = jsonObject.getString("username");
                String password = jsonObject.getString("password");
                User findedUser = server.getUsers().stream().filter(u-> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
                if(Objects.nonNull(findedUser)){
                    sender.println(JsonUtil.convertToJsonMessage(Codes.REGISTER_FAULT,""));
                }
                else{
                    server.addUser(new User(username,password));
                    sender.println(JsonUtil.convertToJsonMessage(Codes.REGISTER_SUCCESS,""));
                }

            }else if(code.equalsIgnoreCase(Codes.LOGOUT)){
               server.getOnlineUsers().remove(user);
               interrupt();
            }else if(code.equalsIgnoreCase(Codes.SEARCH_USER)){
                String username = jsonObject.getString("username");
                User findedUser = server.getOnlineUsers().stream().filter(u-> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
                if(Objects.isNull(findedUser)){
                    sender.println(JsonUtil.convertToJsonMessage(Codes.ONLINE_USER_NOT_FOUND,""));
                }else{
                    sender.println(JsonUtil.submitUserPort(Codes.ONLINE_USER_FOUND,findedUser.getPort()));
                }
            }


        }
    }

}
