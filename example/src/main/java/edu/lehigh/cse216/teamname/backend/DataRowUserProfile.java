package edu.lehigh.cse216.teamname.backend;

import java.util.Date;
//phase 2 Shenyi Yu
/**
 * DataRowUserProfile holds a row of information.  A row of information consists of
 * an identifier, strings for a "username", "email", "salt", "password" and "intro".
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
    public String uSername;

    /**
     * The email address for this row of data
     */
    public String uEmail;

    /**
     * The salt that correlates with the specific password user is using for this row of data
     */
    public String uSalt;
    /**
     * The password saved for the specific user for this row of data
     */
    public String uPassword;
    /**
     * The introduction user types for this row of data
     */
    public String uIntro;

    public String sessionKey;
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
     * @param salt The salt related to the password for this row of data
     *
     * @param password The password for this row of data
     *
     * @param intro The introduction for this row of data
     */
    DataRowUserProfile(int uid, String username, String email, String salt, String password, String intro) {
        uId = uid;
        uSername = username;
        uEmail = email;
        uSalt = salt;
        uPassword = password;
        uIntro = intro;
    }

    DataRowUserProfile(int uid, String username, String email, String salt, String password, String intro, String SessionKey) {
        uId = uid;
        uSername = username;
        uEmail = email;
        uSalt = salt;
        uPassword = password;
        uIntro = intro;
        sessionKey = SessionKey;
    }

    /**
     * Copy constructor to create one DataRowUserProfile from another
     */
    DataRowUserProfile(DataRowUserProfile data) {
        uId = data.uId;
        // NB: Strings and Dates are immutable, so copy-by-reference is safe
        uSername = data.uSername;
        uEmail = data.uEmail;
        uSalt = data.uSalt;
        uPassword = data.uPassword;
        uIntro = data.uIntro;
        sessionKey = data.sessionKey;
    }
}