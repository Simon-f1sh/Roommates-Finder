//package edu.lehigh.cse216.teamname.backend;
//
//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;
//
//import java.util.Date;
//
///**
// * Unit test for simple App.
// */
//public class DataRowLoginTest extends TestCase {
//    /**
//     * Create the test case
//     *
//     * @param testName name of the test case
//     */
//    public DataRowLoginTest(String testName) {
//        super(testName);
//    }
//
//    /**
//     * @return the suite of tests being tested
//     */
//    public static Test suite() {
//        return new TestSuite(DataRowLoginTest.class);
//    }
//
//    /**
//     * Ensure that the constructor populates every field of the object it
//     * creates
//     */
//    public void testConstructor() {
//        String email = "shy221@lehigh.edu";
//        String password = "is3eZd&v";
//        int uid = 4;
//        String salt = "FbTi43BuQHEBbRPjgipuHQme8z8gFQQ50TKiQ6jztHs=";
//        DataRowLogin d = new DataRowLogin(new DataRowUserProfile(uid, null, email, salt, password, null));
//
//        assertTrue(d.uEmail.equals(email));
//        assertTrue(d.uPassword.equals(password));
//        assertTrue(d.uId == uid);
//        assertTrue(d.uSalt.equals(salt));
//    }
//
//    /**
//     * Ensure that the copy constructor works correctly
//     */
//    public void testCopyconstructor() {
//        String email = "Test Title";
//        String password = "Test Content";
//        int uid = 4;
//        String salt = "FbTi43BuQHEBbRPjgipuHQme8z8gFQQ50TKiQ6jztHs=";
//        DataRowLogin d = new DataRowLogin(new DataRowUserProfile(uid, null, email, salt, password, null));
//        DataRowLogin d2 = new DataRowLogin(new DataRowUserProfile(uid, null, email, salt, password, null));
//
//        assertTrue(d2.uEmail.equals(d.uEmail));
//        assertTrue(d2.uPassword.equals(d.uPassword));
//        assertTrue(d2.uSalt.equals(d.uSalt));
//        assertTrue(d2.uId == d.uId);
//
//    }
//}