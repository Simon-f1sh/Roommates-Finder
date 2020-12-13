package edu.lehigh.cse280.backend;

import java.util.Date;

/**
 * DataRow holds a row of information.  A row of information consists of
 * an identifier, strings for a "title" and "content", and a creation date.
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public.  That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */
public class DataRow {
    /**
     * The unique identifier associated with this element.  It's final, because
     * we never want to change it.
     */
    public final int uid;
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

    /**
     * Constructor that input all data
     */
    DataRow(int id, String username, String email, int gender, int tidiness, int noise, int sleep, int wake, int pet, int visitor, String hobby) {
        uid = id;
        uName = username;
        uEmail = email;
        uGender = gender;
        uTidiness = tidiness;
        uNoise = noise;
        uSleepTime = sleep;
        uWakeTime = wake;
        uPet = pet;
        uVisitor = visitor;
        uHobby = hobby;
    }



    /**
     * Copy constructor to create one data row from another
     */
    DataRow(DataRow data) {
        uid = data.uid;
        uName = data.uName;
        uEmail = data.uEmail;
        uGender = data.uGender;
        uTidiness = data.uTidiness;
        uNoise = data.uNoise;
        uSleepTime = data.uSleepTime;
        uWakeTime = data.uWakeTime;
        uPet = data.uPet;
        uVisitor = data.uVisitor;
        uHobby = data.uHobby;
    }
}