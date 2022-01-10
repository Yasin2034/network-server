import lombok.Getter;
import lombok.SneakyThrows;

import org.json.JSONObject;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@Getter
public class Server extends Thread{

    private ServerSocket serverSocket;
    private Set<User> users =  Collections.synchronizedSet(new HashSet<>(Arrays.asList(new User[]{new User("a", "1"), new User("b", "1")})));
    private Set<User> onlineUsers = Collections.synchronizedSet(new HashSet<>(Arrays.asList(new User[]{new User("a", "1"), new User("b", "1")})));
    private List<UserToServerSocket> userToServerSockets = new ArrayList<>();

    @SneakyThrows
    @Override
    public void run() {
        serverSocket = new ServerSocket(Consants.SERVER_PORT);
        Thread listenUDPSocket = new Thread(()->{
            loginUpdaterThread();
        });
        listenUDPSocket.start();

        Thread loginChecker = new Thread(()->{
           while (true){
               try {
                   sleep(10_000);
                   Set<User> passiveUsers = onlineUsers
                           .stream()
                           .filter(u-> System.currentTimeMillis() - u.getLastUpdatedTime() > 20_000)
                           .collect(Collectors.toSet());
                           onlineUsers.removeAll(passiveUsers);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
        });
        loginChecker.start();
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

    private void loginUpdaterThread() {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket(3000);
        } catch (SocketException socketException) {
            socketException.printStackTrace();
        }
        byte[] receive = new byte[65535];

        DatagramPacket DpReceive = null;
        while (true)
        {
            DpReceive = new DatagramPacket(receive, receive.length);

            try {
                ds.receive(DpReceive);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject(data(receive));
            String username = jsonObject.getString("username");
            User user = users.stream().filter(u-> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
            user.setLastUpdatedTime(System.currentTimeMillis());
            onlineUsers.add(user);
            receive = new byte[65535];
        }
    }

    public void addUser(User user){
        users.add(user);
    }

    private String data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret.toString();
    }


}
