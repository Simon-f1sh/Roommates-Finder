package edu.lehigh.cse216.teamname.backend;

/**
 * DataRowLite is for communicating back a subset of the information in a
 * DataRow.  Specifically, we only send back the id and title.  Note that
 * in order to keep the client code as consistent as possible, we ensure
 * that the field names in DataRowLite match the corresponding names in
 * DataRow.  As with DataRow, we plan to convert DataRowLite objects to
 * JSON, so we need to make their fields public.
 */
public class DataRowLogin {
    /**
     * The id for this row; see DataRow.mId
     */
    public int uId;

    public String uEmail;

    /**
     * The password string for this row of data; see DataRow.mTitle
     */
    public String uPassword;

    /**
     * The salt string for this row of data; see DataRow.mTitle
     */
    public String uSalt;

    /**
     * Create a DataRowLite by copying fields from a DataRow
     */
    public DataRowLogin(DataRowUserProfile data) {
        this.uId = data.uId;
        this.uEmail = data.uEmail;
        this.uPassword = data.uPassword;
        this.uSalt= data.uSalt;
    }
}