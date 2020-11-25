package edu.lehigh.cse216.teamname.backend;

/**
 * LoginRequest provides a format for clients to present email and password
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class LoginRequest {
    /**
     * The email being provided by the client.
     */
    public String uEmail;

    /**
     * The password being provided by the client.
     */
    public String uPassword;

    public String uSalt;

    public String sessionKey;
}