import lombok.Getter;
import lombok.SneakyThrows;

import java.net.*;
import java.util.*;

@Getter
public class Server extends Thread{

    private ServerSocket serverSocket;
    private List<User> users = new ArrayList<User>(Arrays.asList(new User[]{new User("a", "1"), new User("b", "1")}));
    private List<User> onlineUsers = new ArrayList<>();
    private List<UserToServerSocket> userToServerSockets = new ArrayList<>();

    @SneakyThrows
    @Override
    public void run() {
        serverSocket = new ServerSocket(Consants.SERVER_PORT);
        try {
            while(true) {
                Socket socket = serverSocket.accept();
                UserToServerSocket userToServerSocket = new UserToServerSocket(this,socket);
                userToServerSocket.start();
                userToServerSockets.add(userToServerSocket);
            }
        } catch(Exception e ) {
            e.printStackTrace();
        }
    }

    public void addUser(User user){
        users.add(user);
    }


}
