package server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String USER_FILE = "ChatServer/users.dat";
    private List<User> users;
    private List<String> loggedInUsers;

    public UserManager() {
        users = new ArrayList<>();
        loggedInUsers = new ArrayList<>();
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
        try {
            // Đảm bảo thư mục tồn tại
            File file = new File(USER_FILE);
            file.getParentFile().mkdirs();
            
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(users);
            oos.close();
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

    public String login(String username, String password) {
        User user = findUser(username);
        if (user == null) {
            return "not_found"; // Tài khoản không tồn tại
        }
        if (!user.getPassword().equals(password)) {
            return "wrong_password"; // Sai mật khẩu
        }
        if (loggedInUsers.contains(username)) {
            return "already_logged_in"; // Tài khoản đang đăng nhập
        }
        loggedInUsers.add(username);
        return "success";
    }

    public void logout(String username) {
        loggedInUsers.remove(username);
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