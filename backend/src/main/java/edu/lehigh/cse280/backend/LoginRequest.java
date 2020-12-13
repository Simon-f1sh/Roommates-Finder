package edu.lehigh.cse280.backend;

/**
 * LoginRequest provides a format for clients to present email and password
 * strings to the server.
 */
public class LoginRequest {
    /**
     * The short-lifed access token returned from Google
     */
    public String id_token;
}