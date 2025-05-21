package src;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private ServerSocket serverSocket;
    private Map<String, ClientHandler> clients;
    private UserManager userManager;

    public ChatServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clients = new HashMap<>();
            userManager = new UserManager();
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private User currentUser;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String command = (String) in.readObject();
                    switch (command) {
                        case "LOGIN":
                            handleLogin();
                            break;
                        case "REGISTER":
                            handleRegister();
                            break;
                        case "MESSAGE":
                            handleMessage();
                            break;
                        case "LOGOUT":
                            handleLogout();
                            return;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (currentUser != null) {
                        userManager.logout(currentUser);
                        clients.remove(currentUser.getUsername());
                    }
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleLogin() throws IOException, ClassNotFoundException {
            String username = (String) in.readObject();
            String password = (String) in.readObject();
            
            User user = userManager.login(username, password);
            if (user != null) {
                currentUser = user;
                clients.put(username, this);
                out.writeObject("SUCCESS");
                broadcastMessage(username + " has joined the chat");
            } else {
                out.writeObject("FAILED");
            }
        }

        private void handleRegister() throws IOException, ClassNotFoundException {
            String username = (String) in.readObject();
            String password = (String) in.readObject();
            
            boolean success = userManager.register(username, password);
            out.writeObject(success ? "SUCCESS" : "FAILED");
        }

        private void handleMessage() throws IOException, ClassNotFoundException {
            String username = (String) in.readObject();
            String message = (String) in.readObject();
            broadcastMessage(username + ": " + message);
        }

        private void handleLogout() throws IOException {
            if (currentUser != null) {
                broadcastMessage(currentUser.getUsername() + " has left the chat");
            }
        }

        private void broadcastMessage(String message) {
            for (ClientHandler client : clients.values()) {
                try {
                    client.out.writeObject("MESSAGE");
                    client.out.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(5000);
        server.start();
    }
} 