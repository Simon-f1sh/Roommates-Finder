package edu.lehigh.cse216.teamname.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;

import java.net.URI;
import java.net.URISyntaxException;

public class Database {
    /**
     * The connection to the database
     */
    private Connection mConnection;

    /**
     * The Id of the next inserted row
     */
    private int mCount;

    /**
     * Some prepared statements
     */
    private PreparedStatement mDeleteOne;
    private PreparedStatement mInsertOne;
    private PreparedStatement mIncrementLikes;
    //when deleting a message also clear below parameters
    //added related sql
    private PreparedStatement mClearLikes;
    private PreparedStatement mClearDislikes;
    private PreparedStatement mClearComments;
    //added decrement likes, increment dislikes and decrement dislikes
    private PreparedStatement mDecrementLikes;
    private PreparedStatement mIncrementDislikes;
    private PreparedStatement mDecrementDislikes;
    private PreparedStatement mCheckLikes;
    private PreparedStatement mCheckDislikes;

    //added select user profile,
    //possibly all user profile ? No need.
    //added update user profile
    private PreparedStatement uSelectOne;
    private PreparedStatement uUpdateOne;
    //added update password
    private PreparedStatement uUpdatePwd;
    //check if password matches
    private PreparedStatement uAuth;
    //added select comments
    private PreparedStatement cSelectAll;
    //added insert comment
    private PreparedStatement cInsertOne;
    private PreparedStatement mSelectAll;
    private PreparedStatement mSelectOne;
    private PreparedStatement mUpdateOne;

    /**
     * The Database constructor is private: we only create Database objects
     * through the getDatabase() method.
     */
    private Database() {
        mCount = 0;
    }

    /**
     * Connect to the Database
     * 
     * @param db_url
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String db_url) {
        Database db = new Database();
        try {
            Class.forName("org.postgresql.Driver");
            URI dbUri = new URI(db_url);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Unable to find postgresql driver");
            return null;
        } catch (URISyntaxException s) {
            System.out.println("URI Syntax Error");
            return null;
        }
        try {
            // NB: we can easily get ourselves in trouble here by typing the
            //     SQL incorrectly.  We really should have things like "tblData"
            //     as constants, and then build the strings for the statements
            //     from those constants.

            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table 
            // creation/deletion, so multiple executions will cause an exception

            // Standard CRUD operations
            //tblData
            db.mDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblData WHERE mid = ?");
            db.mInsertOne = db.mConnection.prepareStatement("INSERT INTO tblData VALUES (default, ?, ?, ?, ?)");
            db.mSelectAll = db.mConnection.prepareStatement("SELECT mid, subject FROM tblData");
            db.mSelectOne = db.mConnection.prepareStatement(
                    "SELECT row.*, (SELECT COUNT(*) FROM tblLike WHERE tblLike.mid = row.mid) AS likes, (SELECT COUNT(*) FROM tblDislike WHERE tblDislike.mid = row.mid) AS dislikes FROM (SELECT * from tblData NATURAL JOIN tblUser) AS row WHERE row.mid = ?");
            db.mUpdateOne = db.mConnection.prepareStatement("UPDATE tblData SET subject = ?, message = ? WHERE mid = ?");
            db.mClearLikes = db.mConnection.prepareStatement("DELETE FROM tblLike WHERE mid = ?");
            db.mClearDislikes = db.mConnection.prepareStatement("DELETE FROM tblDislike WHERE mid = ?");
            db.mClearComments = db.mConnection.prepareStatement("DELETE FROM tblComment WHERE mid = ?");

            db.mIncrementLikes = db.mConnection.prepareStatement("INSERT INTO tblLike VALUES (?, ?)");
            db.mDecrementLikes = db.mConnection.prepareStatement("DELETE FROM tblLike WHERE uid = ? AND mid = ?");
            db.mIncrementDislikes = db.mConnection.prepareStatement("INSERT INTO tblDislike VALUES (?, ?)");
            db.mDecrementDislikes = db.mConnection.prepareStatement("DELETE FROM tblDislike WHERE uid = ? AND mid = ?");
            db.mCheckLikes = db.mConnection.prepareStatement("SELECT * from tblLike where uid = ? AND mid = ?");
            db.mCheckDislikes = db.mConnection.prepareStatement("SELECT * from tblDislike where uid = ? AND mid = ?");

            //add get user profile for spefic user
            db.uSelectOne = db.mConnection.prepareStatement("SELECT * from tblUser WHERE uid = ?");
            //tblUser
            //add update user profile for specific user
            db.uUpdateOne = db.mConnection.prepareStatement("UPDATE tblUser SET username = ?, intro = ? WHERE uid = ?");
            db.uAuth = db.mConnection.prepareStatement("SELECT * from tblUser WHERE email = ?");
            //add update password for specific user
            db.uUpdatePwd = db.mConnection.prepareStatement("UPDATE tblUser SET salt = ?, password = ? WHERE uid = ?");
            //tblComments
            //add get all comments for specific message
            db.cSelectAll = db.mConnection.prepareStatement("SELECT cid, uid, username, text FROM tblComment NATURAL JOIN tblUser where mid = ?");
            db.cInsertOne = db.mConnection.prepareStatement("INSERT INTO tblComment VALUES (default, ?, ?, ?)");
        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            db.disconnect();
            return null;
        }
        return db;
    }

    /**
     * Close the current connection to the Database, if one exists.
     * 
     * @return True if the connection was cleanly closed, false otherwise.
     */
    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        mConnection = null;
        return true;
    }

    /**
     * Insert a row into the database
     * 
     * @param subject The subject for this new row
     * @param message The message for this new row
     * @return The Id of the new row, or -1 if no row was created
     */
    //detail: title, content, likes, dislikes, comment.userid, comments.text
    public int createEntry(int uid, String subject, String message) {
        if (subject == null || message == null)
            return -1;
        int id = mCount++;
        try {
            mInsertOne.setInt(1, uid);
            mInsertOne.setString(2, subject);
            mInsertOne.setString(3, message);
            Date date= new Date();
            Timestamp ts = new Timestamp(date.getTime());
            mInsertOne.setTimestamp(4, ts);
            mInsertOne.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * Insert a row into the database
     *
     * @param uid The subject for this new row
     * @param mid The message for this new row
     * @param text
     * @return The Id of the new row, or -1 if no row was created
     */
    //detail: title, content, likes, dislikes, comment.userid, comments.text
    public int createComment(int uid, int mid, String text) {
        if (uid <= 0 || mid <= 0 ||text == null)
            return -1;
        try {
            cInsertOne.setInt(1, uid);
            cInsertOne.setInt(2, mid);
            cInsertOne.setString(3, text);
            cInsertOne.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mid;
    }

    /**
     * phase 2
     * Get all data for a specific row, by Id
     * 
     * @param id The Id of the row being requested
     * @return The data for the requested row, or null if the Id was invalid
     */
    public DataRow readOne(int id) {
        DataRow res = null;
        try {
            mSelectOne.setInt(1, id);
            ResultSet rs = mSelectOne.executeQuery();
            if (rs.next()) {
//                ArrayList<Comment> comments = new ArrayList<Comment>();
//                comments = ;
                //detail: title, content, likes, dislikes, comment.userid, comments.text
                //res = new DataRow(rs.getInt("id"), rs.getString("subject"), rs.getString("message"), rs.getInt("likes"), rs.getDate("date"));
//                res = new DataRow(rs.getInt("id"), rs.getString("subject"), rs.getString("message"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getDate("date"), comments);
                res = new DataRow(rs.getInt("mid"), rs.getInt("uid"), rs.getString("username"), rs.getString("subject"), rs.getString("message"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getDate("date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * phase 2
     * Query the database for a list of all comments and their related Ids
     *
     * @return All rows, as an ArrayList
     */
    public ArrayList<Comment> readAllComments(int mId) {
        ArrayList<Comment> res = new ArrayList<Comment>();
        try {
            // SQL that gets all comments.executeQuery()
            cSelectAll.setInt(1, mId);
            ResultSet rs = cSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new Comment(rs.getInt("cid"), rs.getInt("uid"), mId, rs.getString("username"), rs.getString("text")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query the database for a list of all subjects and their Ids
     * 
     * @return All rows, as an ArrayList
     */
    //in phase 1 only use DataRow for the convience of Android, probably need to change back to DataRowLite in later phase.
    //public ArrayList<DataRowLite> readAll() {
    public ArrayList<DataRow> readAll() {
        //ArrayList<DataRowLite> res = new ArrayList<DataRowLite>();
        ArrayList<DataRow> res = new ArrayList<DataRow>();
        try {
            ResultSet rs = mSelectAll.executeQuery();
            while (rs.next()) {
                //res.add(new DataRowLite(new DataRow(rs.getInt("id"), rs.getString("subject"), rs.getString("message"), rs.getInt("likes"))));
                //phase 1 used
                //res.add(new DataRow(rs.getInt("id"), rs.getString("subject"), rs.getString("message"), rs.getInt("likes"), rs.getDate("date")));
                //phase 2 with more parameters
                res.add(new DataRow(rs.getInt("mid"), rs.getString("subject")));
//                res.add(new DataRow(rs.getInt("mid"), rs.getInt("uid"), rs.getString("username"), rs.getString("subject"), rs.getString("message"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getDate("date")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update the message of a row in the database
     * 
     * @param mid The Id of the row to update
     * @param title The new subject for the row
     * @param message The new message for the row
     * @return a copy of the data in the row, if exists, or null otherwise
     */
    public DataRow updateOne(int mid, String title, String message) {
        try {
            mUpdateOne.setString(1, title);
            mUpdateOne.setString(2, message);
            mUpdateOne.setInt(3, mid);
            mUpdateOne.execute();
            return readOne(mid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Increment the likes value by 1
     *
     * @param mid the Id of the row to increment likes value
     * @return a copy of the data in the row, if exists, or null otherwise
     */
    public DataRow incrementLikes(int uid, int mid) {
        try {
            mIncrementLikes.setInt(1, uid);
            mIncrementLikes.setInt(2, mid);
            mIncrementLikes.execute();
            return readOne(mid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * phase 2
     * Decrement the likes value by 1
     *
     * @param uid the Id of the row to increment likes value
     * @param mid
     * @return a copy of the data in the row, if exists, or null otherwise
     */
    public DataRow decrementLikes(int uid, int mid) {
        try {
            mDecrementLikes.setInt(1, uid);
            mDecrementLikes.setInt(2, mid);
            mDecrementLikes.execute();
            return readOne(mid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //ucheck if table next is null then chall related function
    public DataRow doLikes(int uid, int mid){
        try {
            mCheckLikes.setInt(1,uid);
            mCheckLikes.setInt(2,mid);
            mCheckLikes.execute();
            ResultSet rs = mCheckLikes.executeQuery();
            if (rs.next()){
                decrementLikes(uid, mid);
            }
            else{
                incrementLikes(uid, mid);
            }
            return readOne(mid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Increment the likes value by 1
     *
     * @param uid the Id of the row to increment likes value
     * @param mid
     * @return a copy of the data in the row, if exists, or null otherwise
     */
    public DataRow incrementDislikes(int uid, int mid) {
        try {
            mIncrementDislikes.setInt(1, uid);
            mIncrementDislikes.setInt(2, mid);
            mIncrementDislikes.execute();
            return readOne(mid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * phase 2
     * Decrement the likes value by 1
     *
     * @param uid the Id of the row to increment likes value
     * @param mid
     * @return a copy of the data in the row, if exists, or null otherwise
     */
    public DataRow decrementDislikes(int uid, int mid) {
        try {
            mDecrementDislikes.setInt(1, uid);
            mDecrementDislikes.setInt(2, mid);
            mDecrementDislikes.execute();
            return readOne(mid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public DataRow doDislikes(int uid, int mid){
        try {
            mCheckDislikes.setInt(1,uid);
            mCheckDislikes.setInt(2,mid);
            mCheckDislikes.execute();
            ResultSet rs = mCheckDislikes.executeQuery();
            if (rs.next()){
                decrementDislikes(uid, mid);
            }
            else{
                incrementDislikes(uid, mid);
            }
            return readOne(mid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }



    /**
     * Delete a row from the database
     * 
     * @param id The Id of the row to delete
     * @return true if the row was delete, false otherwise
     */
    public boolean deleteOne(int id) {
        try {
            mDeleteOne.setInt(1, id);
            mDeleteOne.execute();
            mClearLikes.setInt(1, id);
            mClearDislikes.setInt(1, id);
            mClearComments.setInt(1, id);
            mClearLikes.execute();
            mClearDislikes.execute();
            mClearComments.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * phase 2
     *
     */

    /**
     * Check if the password for this specific user exists
     *
     * @param email login authorization
     */
    public DataRowUserProfile matchPwd(String email) {
        DataRowUserProfile res = null;
        try {
            uAuth.setString(1, email);
            ResultSet rs = uAuth.executeQuery();
            if (rs.next()) {
                res = new DataRowUserProfile(rs.getInt("uid"), rs.getString("username"), rs.getString("email"), rs.getString("salt"), rs.getString("password"),rs.getString("intro") );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * phase 2
     * Get all data for a specific row, by Id
     *
     * @param id The user Id of the row being requested
     * @return The data for the requested row, or null if the Id was invalid
     */
    public DataRowUserProfile uReadOne(int id) {
        DataRowUserProfile res = null;
        try {
            uSelectOne.setInt(1, id);
            ResultSet rs = uSelectOne.executeQuery();
            if (rs.next()) {
                //modify here
                //detail: uid, username, email, salt, password, intro
                //DataRowUserProfile for user profile!
                res = new DataRowUserProfile(rs.getInt("uid"), rs.getString("username"), rs.getString("email"), rs.getString("salt"), rs.getString("password"),rs.getString("intro") );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * phase 2
     * Update the profile of a row in the database
     *
     * can it be updated? uid cannot be updated!
     * uEmail is not necessary to be updated.
     * @param username The new username for the row
     * @param intro The new intro for the row
     * No other param to be updated.
     * @return a copy of the data in the row, if exists, or null otherwise
     */
    //DONE: need modify the index or add more params.
    public DataRowUserProfile uUpdateOne(int id, String username, String intro) {
        try {
            uUpdateOne.setString(1, username);
            uUpdateOne.setString(2, intro);
            uUpdateOne.setInt(3, id);
            uUpdateOne.execute();
            return uReadOne(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * phase 2
     * Update the password of a row in the database
     *
     * @param password The new password for the row
     * No other param to be updated.
     * @return a copy of the data in the row, if exists, or null otherwise
     */
    //DONE: need modify the index or add more params.
    public int uUpdatePwd(int id, String salt, String password) {
        try {
            uUpdatePwd.setString(1, salt);
            uUpdatePwd.setString(2, password);
            uUpdatePwd.setInt(3, id);
            uUpdatePwd.execute();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
