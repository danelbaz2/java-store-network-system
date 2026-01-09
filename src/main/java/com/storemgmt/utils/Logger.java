package com.storemgmt.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface Logger {
    static void write(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter("server_log.txt", true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            out.println(timestamp + " " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
