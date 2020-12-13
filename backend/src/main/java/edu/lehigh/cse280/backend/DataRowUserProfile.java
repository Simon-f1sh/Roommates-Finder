package edu.lehigh.cse280.backend;

import java.util.Date;
/**
 * DataRowUserProfile holds a row of information.  A row of information consists of
 * an identifier, strings for a "username", "name", "email", "password" and "intro".
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public.  That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */
public class DataRowUserProfile {
    /**
     * The unique identifier associated with this element.  It's final, because
     * we never want to change it.
     */
    public  int uId;

    /**
     * The username for this row of data
     */
    public String uName;

    /**
     * The email address for this row of data
     */
    public String uEmail;

    public int uAdmin;


    /**
     * Create a new DataRowUserProfile with the provided user id and parameters,
     *
     * @param uid The id to associate with this row.  Assumed to be unique
     *           throughout the whole program.
     * 
     * @param username The username string for this row of data
     * 
     * @param email The email string for this row of data
     */
    DataRowUserProfile(int uid, String username, String email) {
        uId = uid;
        uName = username;
        uEmail = email;
    }

    /**
     * Create a new DataRowUserProfile with the provided user id and parameters,
     *
     * @param uid The id to associate with this row.  Assumed to be unique
     *           throughout the whole program.
     * 
     * @param username The username string for this row of data
     * 
     * @param email The email string for this row of data
     * 
     * @param admin If the user is admin
     */
    DataRowUserProfile(int uid, String username, String email, int admin) {
        uId = uid;
        uName = username;
        uEmail = email;
        uAdmin = admin;
    }

    /**
     * Copy constructor to create one DataRowUserProfile from another
     */
    DataRowUserProfile(DataRowUserProfile data) {
        uId = data.uId;
        // NB: Strings and Dates are immutable, so copy-by-reference is safe
        uName = data.uName;
        uEmail = data.uEmail;
        uAdmin = data.uAdmin;
    }
}