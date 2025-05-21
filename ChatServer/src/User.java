package src;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private boolean isOnline;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isOnline = false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User other = (User) obj;
            return this.username.equals(other.username);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
} 