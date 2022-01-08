import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    private boolean isLoggedIn;
    private User user;
    private long lastUpdatedTime = System.currentTimeMillis();

    @Override
    public void run() {
        try {
            while(true) {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                sender = new PrintWriter(socket.getOutputStream(), true);

                sender.println(Util.formatMessage("server","1)Login \n2)Register"));
                listen(reader);
            }

        } catch(Exception e ) {
            e.printStackTrace();
        }
    }

    private void listen(BufferedReader reader) throws IOException {
        boolean isMenuPrinted = false;
        while(true) {
            String input = null;
            if(isLoggedIn){
                if(!isMenuPrinted){
                    sender.println(Util.formatMessage("server","Welcome!"));
                    sender.println(Util.formatMessage("server","1) find user"));
                    sender.println(Util.formatMessage("server","if you want to exit, please logout"));
                    isMenuPrinted = true;
                }
                input = reader.readLine();
                if (input.equals("1")){
                    input = null;
                    while (input == null) {
                        sender.println(Util.formatMessage("server","give me a username"));
                        input = reader.readLine();
                    }
                    String finalInput = input;
                    User findedUser = server.getOnlineUsers().stream().filter(u->u.getUsername().equalsIgnoreCase(finalInput)).findFirst().orElse(null);
                    if(findedUser != null){
                        sender.println("userport:"+findedUser.getPort());
                        this.interrupt();
                    }else{
                        sender.println(Util.formatMessage("server","user not found"));
                        isMenuPrinted = false;
                    }
                }else if(input.contains("port")){
                    user.setPort(input.split(":")[1]);
                    server.getOnlineUsers().add(user);
                    isMenuPrinted = false;
                }else if(input.equalsIgnoreCase("LOGOUT")){
                    server.getOnlineUsers().remove(user);
                    socket.close();
                    this.stop();
                    break;
                }

            }else{
                input = reader.readLine();
                if(input.equals("1")){
                    User user;
                    String password;
                    while (true){
                        sender.println(Util.formatMessage("server","please enter username"));
                        String username = reader.readLine();
                        user = server.getUsers().stream().filter(u-> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
                        if(Objects.nonNull(user)){
                            sender.println(Util.formatMessage("server","please enter password"));
                            password = reader.readLine();
                            if(password.equalsIgnoreCase(user.getPassword())){
                                this.user = user;
                                sender.println(Util.formatMessage("server","you are logged in"));
                                sender.println(Util.formatMessage("server","200"));
                                isLoggedIn = true;
                                break;
                            }
                        }
                    }
                }else if(input.equals("2")){
                    while (true){
                        sender.println(Util.formatMessage("server","please enter username"));
                        String username = reader.readLine();
                        user = server.getUsers().stream().filter(u-> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
                        if(Objects.isNull(user)){
                            sender.println(Util.formatMessage("server","please enter password"));
                            String password = reader.readLine();
                            User user = new User(username,password);
                            server.addUser(user);
                            sender.println(Util.formatMessage("server","you are registered"));
                            break;
                        }else if(Objects.nonNull(user)){
                            sender.println(Util.formatMessage("server","username is used"));
                        }
                    }
                }
                lastUpdatedTime = System.currentTimeMillis();
            }
        }
    }

}
