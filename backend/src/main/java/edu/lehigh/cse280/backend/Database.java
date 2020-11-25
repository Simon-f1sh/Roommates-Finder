package edu.lehigh.cse280.backend;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

public class Database {
    /**
     * The connection to the database
     */
    private Connection mConnection;

    /**
     * Some prepared statements
     */

    private PreparedStatement uAuth;
    private PreparedStatement uCreateTable;
    private PreparedStatement uDropTable;
    private PreparedStatement uInsertOne;
    private PreparedStatement cCreateTable;
    private PreparedStatement cDropTable;
    private PreparedStatement cInsertOne;
    private PreparedStatement cSelectOne;
    private PreparedStatement cSelectAll;
    private PreparedStatement cUpdateOne;
    private PreparedStatement cDeleteOne;

    /**
     * The Database constructor is private: we only create Database objects
     * through the getDatabase() method.
     */
    private Database() {

    }

    public void init() {
        try {
            uCreateTable.execute();
            cCreateTable.execute();
        } catch(Exception e) {
            e.printStackTrace();
            disconnect();
        }
    }

    /**
     * Connect to the Database
     * 
     * @param ip   The IP address of the database server
     * @param port The port on the database server to which connection requests
     *             should be sent
     * @param user The user ID to use when connecting
     * @param pass The password to use when connecting
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String ip, String port, String user, String pass) {
        Database db = new Database();;
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://" + ip + ":" + port + "/", user, pass);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }

            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        }

        // Create prepared statement
        try {
            db.uCreateTable = db.mConnection
                    .prepareStatement("CREATE TABLE tblUser (uid SERIAL PRIMARY KEY, username VARCHAR(50) "
                            + "NOT NULL, email VARCHAR(500) NOT NULL, salt VARCHAR(500) NOT NULL, password VARCHAR(500) NOT NULL)");
            db.uDropTable = db.mConnection.prepareStatement("DROP TABLE tblUser");

            // Standard CRUD operations
            db.uInsertOne = db.mConnection.prepareStatement("INSERT INTO tblUser VALUES (default, ?, ?, ?, ?)");
            db.uAuth = db.mConnection.prepareStatement("SELECT * from tblUser WHERE email = ?");

            db.cCreateTable = db.mConnection
                    .prepareStatement("CREATE TABLE tblCounter (cid SERIAL PRIMARY KEY, uid INTEGER "
                            + "NOT NULL, value INTEGER NOT NULL, "
                            + "FOREIGN KEY(uid) REFERENCES tblUser)");
            db.cDropTable = db.mConnection.prepareStatement("DROP TABLE tblCounter");

            // Standard CRUD operations
            db.cDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblCounter WHERE cid = ?");
            db.cInsertOne = db.mConnection.prepareStatement("INSERT INTO tblCounter VALUES (default, ?, ?)");
            db.cSelectOne = db.mConnection.prepareStatement("SELECT * from tblCounter WHERE cid = ?");
            db.cSelectAll = db.mConnection.prepareStatement("SELECT * FROM tblCounter WHERE uid = ?");
            db.cUpdateOne = db.mConnection.prepareStatement("UPDATE tblCounter SET value = ? WHERE cid = ?");
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
            cDropTable.execute();
            uDropTable.execute();
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
     * @param email The subject for this new row
     * 
     * @return The number of rows that were inserted
     */
    public int insertRowToUser(String email, String password) {
        // modify functions here
        String salt = BCrypt.gensalt(12);
        String hash = BCrypt.hashpw(password, salt);
        int count = 0;
        try {
            uInsertOne.setString(1, email.split("@")[0]);
            uInsertOne.setString(2, email);
            uInsertOne.setString(3, salt);
            uInsertOne.setString(4, hash);
            count += uInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

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
                res = new DataRowUserProfile(rs.getInt("uid"), rs.getString("username"), rs.getString("email"), rs.getString("salt"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

	public int createCounter(int uid, int value) {
		int count = 0;
        try {
            cInsertOne.setInt(1, uid);
            cInsertOne.setInt(2, value);
            count += cInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
	}

	public DataRow cIncrement(int uid, int cid) {
		try {
            cSelectOne.setInt(1, cid);
            ResultSet rs = cSelectOne.executeQuery();
            if (rs.next()) {
                cUpdateOne.setInt(1, rs.getInt("value") + 1);
                cUpdateOne.setInt(2, cid);
                cUpdateOne.execute();
            }
            return selectCounter(cid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
	}

	public DataRow cDecrement(int uid, int cid) {
		try {
            cSelectOne.setInt(1, cid);
            ResultSet rs = cSelectOne.executeQuery();
            if (rs.next()) {
                cUpdateOne.setInt(1, rs.getInt("value") - 1);
                cUpdateOne.setInt(2, cid);
                cUpdateOne.execute();
            }
            return selectCounter(cid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
	}

	public DataRow selectCounter(int cid) {
		DataRow res = null;
        try {
            cSelectOne.setInt(1, cid);
            ResultSet rs = cSelectOne.executeQuery();
            if (rs.next()) {
                res = new DataRow(rs.getInt("cid"), rs.getInt("uid"), rs.getInt("value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    
    public ArrayList<DataRow> selectAllCounter(int uid) {
		ArrayList<DataRow> res = new ArrayList<DataRow>();
        try {
            cInsertOne.setInt(1, uid);
            ResultSet rs = cSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new DataRow(rs.getInt("cid"), rs.getInt("uid"), rs.getInt("value")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
	}

	public boolean deleteCounter(int idx) {
        try {
            cDeleteOne.setInt(1, idx);
            cDeleteOne.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
	}
}