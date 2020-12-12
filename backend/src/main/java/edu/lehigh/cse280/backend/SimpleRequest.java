package edu.lehigh.cse280.backend;

/**
 * SimpleRequest provides a format for clients to present title and message 
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class SimpleRequest {
    public int uid;

    public String uName;

    public String uEmail;

    public int uGender;

    public int uTidiness;

    public int uNoise;

    public int uSleepTime;

    public int uWakeTime;

    public int uPet;

    public int uVisitor;

    public String uHobby;
}