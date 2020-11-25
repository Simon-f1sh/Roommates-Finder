//phase 2 Shenyi Yu
package edu.lehigh.cse216.teamname.backend;

import java.util.Date;

/**
 * Comment holds a row of information.  A row of information consists of
 * an identifier, strings for a "title" and "content", and a creation date.
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public.  That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */
public class Comment {
    /**
     * The unique identifier associated with this element.  It's final, because
     * we never want to change it.
     */
    public final int cId;
    public final int uId;
    public final int mId;



    /**
     * The username for this row of data
     */
    public String cUsername;
    /**
     * The content for this row of data
     */
    public String cText;




    /**
     * Create a new Comment with the provided id and title/content, and a 
     * creation date based on the system clock at the time the constructor was
     * called
     *
     * @param cid The id to associate with this row.  Assumed to be unique
     *           throughout the whole program.
     * @param mid The message id that is being commented.
     * @param uid The user id that write this comment.
     *
     * @param username The title string for this row of data
     *
     * @param text The content string for this row of data
     */
    Comment(int cid, int uid, int mid, String username, String text) {
        cId = cid;
        uId = uid;
        mId = mid;
        cUsername = username;
        cText = text;
//        sessionKey = SessionKey;
    }

    /**
     * Copy constructor to create one Comment from another
     */
    Comment(Comment data) {
        cId = data.cId;
        uId = data.uId;
        mId = data.mId;
        // NB: Strings and Dates are immutable, so copy-by-reference is safe
        cUsername = data.cUsername;
        cText = data.cText;
//        sessionKey = data.sessionKey;
    }
}