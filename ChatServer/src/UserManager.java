package src;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class UserManager {
    private Set<User> users;
    private static final String USER_FILE = "users.dat";

    public UserManager() {
        users = new HashSet<>();
        loadUsers();
    }

    public boolean register(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        User newUser = new User(username, password);
        if (users.contains(newUser)) {
            return false;
        }

        users.add(newUser);
        saveUsers();
        return true;
    }

    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                if (!user.isOnline()) {
                    user.setOnline(true);
                    return user;
                }
                return null; // User is already online
            }
        }
        return null; // Invalid credentials
    }

    public void logout(User user) {
        if (user != null) {
            user.setOnline(false);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(USER_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (Set<User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 