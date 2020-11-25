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
    public final int cId;
    public int uId;

    public int cValue;


    /**
     * Create a new DataRow with the provided id and title/content, and a 
     * creation date based on the system clock at the time the constructor was
     * called
     *
     * @param mid The id to associate with this row.  Assumed to be unique
     *           throughout the whole program.
     *
     * @param title The title string for this row of data
     *
     * @param content The content string for this row of data
     */
    DataRow(int cid, int uid, int value) {
        cId = cid;
        uId = uid;
        cValue = value;
    }



    /**
     * Copy constructor to create one data row from another
     */
    DataRow(DataRow data) {
        cId = data.cId;
        uId = data.uId;
        cValue = data.cValue;
    }
}