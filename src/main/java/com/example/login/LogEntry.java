package com.example.login;

import java.time.LocalDateTime;

public class LogEntry {
    private final int id;
    private final String username;
    private final LocalDateTime loginTime;

    public LogEntry(int id, String username, LocalDateTime loginTime) {
        this.id = id;
        this.username = username;
        this.loginTime = loginTime;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    @Override
    public String toString() {
        return "LogEntry{id=" + id + ", username='" + username + "', loginTime=" + loginTime + "}";
    }
}
