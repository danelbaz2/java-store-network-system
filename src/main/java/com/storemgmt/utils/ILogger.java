package com.storemgmt.utils;

public interface ILogger {
    void info(String message);
    void error(String message);
    void error(String message, Exception ex);
}
