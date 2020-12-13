package edu.lehigh.cse280.backend;

public class DataAdmin {
    /**
     * The unique identifier associated with this element.  It's final, because
     * we never want to change it.
     */
    public int uAdmin;

    /**
     * @param admin Admin Check Result
     */
    DataAdmin(int admin) {
        uAdmin = admin;
    }

    /**
     * Copy constructor to create one DataRowUserProfile from another
     */
    DataAdmin(DataRowUserProfile data) {
        uAdmin = data.uAdmin;
    }
}