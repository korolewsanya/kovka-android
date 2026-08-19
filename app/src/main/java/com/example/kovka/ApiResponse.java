package com.example.kovka;

public class ApiResponse {
    private boolean success;
    private String error;
    private String new_name;

    public boolean isSuccess() { return success; }
    public String getError() { return error; }
    public String getNewName() { return new_name; }
}