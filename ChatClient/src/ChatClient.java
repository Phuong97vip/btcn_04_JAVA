package src;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private ChatFrame chatFrame;

    public ChatClient(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String username, String password) {
        try {
            out.writeObject("LOGIN");
            out.writeObject(username);
            out.writeObject(password);
            
            String response = (String) in.readObject();
            if (response.equals("SUCCESS")) {
                this.username = username;
                chatFrame = new ChatFrame(this);
                chatFrame.setVisible(true);
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean register(String username, String password) {
        try {
            out.writeObject("REGISTER");
            out.writeObject(username);
            out.writeObject(password);
            
            String response = (String) in.readObject();
            return response.equals("SUCCESS");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendMessage(String message) {
        try {
            out.writeObject("MESSAGE");
            out.writeObject(username);
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        try {
            out.writeObject("LOGOUT");
            out.writeObject(username);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("localhost", 5000);
        LoginFrame loginFrame = new LoginFrame(client);
        loginFrame.setVisible(true);
    }
} 