package edu.lehigh.cse216.teamname.backend;

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
    public final int mId;
    public int uId;

    /**
     * The title for this row of data
     */
    public String mTitle;

    /**
     * The content for this row of data
     */
    public String mContent;

    public int mLikes;

    //phase 2 Shenyi Yu

    public int mDislikes;

    /**
     * all comments for this message
     */
//    ArrayList<Comment> mComments;
    public String cText;
    public String cUsername;

    /**
     * The creation date for this row of data.  Once it is set, it cannot be 
     * changed
     */
    public  Date mCreated;


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
    DataRow(int mid, int uid, String username, String title, String content, int likes, int dislikes, Date date) {
        mId = mid;
        uId = uid;
        cUsername = username;
        mTitle = title;
        mContent = content;
        mLikes = likes;
        mDislikes = dislikes;
        mCreated = new Date();
    }

    DataRow(int mid, String title) {
        mId = mid;
        mTitle = title;
    }



    /**
     * Copy constructor to create one data row from another
     */
    DataRow(DataRow data) {
        mId = data.mId;
        uId = data.uId;
        // NB: Strings and Dates are immutable, so copy-by-reference is safe
        cUsername = data.cUsername;
        mTitle = data.mTitle;
        mContent = data.mContent;
        mLikes = data.mLikes;
        mDislikes = data.mDislikes;
        mCreated = data.mCreated;
    }
}