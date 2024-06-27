package com.example.server.security;

public class Views {
    public static class Public {}
    public static class General extends Public {}
    public static class Private extends General {}
    public static class Admin extends Private {}
}
