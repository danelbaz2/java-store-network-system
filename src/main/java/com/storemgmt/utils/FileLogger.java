package com.storemgmt.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class FileLogger implements ILogger {

    private final String filePath;

    public FileLogger(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void info(String message) {
        write("INFO", message, null);
    }

    @Override
    public void error(String message) {
        write("ERROR", message, null);
    }

    @Override
    public void error(String message, Exception ex) {
        write("ERROR", message, ex);
    }

    private void write(String level, String message, Exception ex) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            out.printf("%s [%s] %s%n", LocalDateTime.now(), level, message);
            if (ex != null) {
                out.printf("%s [%s] %s%n", LocalDateTime.now(), level, ex.toString());
            }
        } catch (IOException e) {
            // fallback: last resort
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}
