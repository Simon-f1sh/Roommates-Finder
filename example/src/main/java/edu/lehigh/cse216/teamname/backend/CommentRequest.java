package edu.lehigh.cse216.teamname.backend;

/**
 * SimpleRequest provides a format for clients to present title and message 
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class CommentRequest {
    //user id
    //message id
    //text
    /**
     * The user id being provided by the client.
     */
    public int uid;
    /**
     * The message id being provided by the cient.
     */

    public int mid;

    /**
     * The comment text being provided by the client.
     */
    public String text;
    /**
     * The email being provided by the client.
     */
    public String uEmail;
    /**
     * The sessoinKey being provided by the client.
     */
    public String sessionKey;
}