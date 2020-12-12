package edu.lehigh.cse280.backend;

/**
 * LoginRequest provides a format for clients to present email and password
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class LoginRequest {
    /**
     * The short-lifed access token returned from Google
     */
    public String id_token;
}