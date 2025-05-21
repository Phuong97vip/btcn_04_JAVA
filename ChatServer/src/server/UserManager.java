package server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String USER_FILE = "users.dat";
    private List<User> users;

    public UserManager() {
        users = new ArrayList<>();
        loadUsers();
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(USER_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (List<User>) ois.readObject();
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

    public boolean register(String username, String password) {
        // Kiểm tra username đã tồn tại chưa
        if (findUser(username) != null) {
            return false;
        }

        // Thêm user mới
        users.add(new User(username, password));
        saveUsers();
        return true;
    }

    public boolean login(String username, String password) {
        User user = findUser(username);
        if (user == null) {
            return false;
        }
        return user.getPassword().equals(password);
    }

    private User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
} 