package edu.lehigh.cse216.teamname.backend;

/**
 * SimpleRequest provides a format for clients to present title and message 
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class SimpleRequest {
    public int mid;
    public int uid;
    /**
     * The title being provided by the client.
     */
    public String mTitle;

    /**
     * The message being provided by the client.
     */
    public String mMessage;
    /**
     * The email being provided by the client.
     */
    public String uEmail;
    /**
     * The sessoinKey being provided by the client.
     */
    public String sessionKey;
}