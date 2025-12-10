package com.example.online_pharmacy.command.impl;

import com.example.online_pharmacy.command.Command;
import jakarta.servlet.http.HttpServletRequest;

public class LogoutCommand implements Command {
    @Override
    public String execute(HttpServletRequest request) {
        return "";
    }
}
