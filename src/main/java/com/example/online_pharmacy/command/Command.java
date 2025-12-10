package com.example.online_pharmacy.command;

import jakarta.servlet.http.HttpServletRequest;

public interface Command {
    String execute(HttpServletRequest request);
    default void refresh(){};
}
