//package edu.lehigh.cse216.teamname.backend;
//
//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;
//
///**
// * Unit test for simple App.
// */
//public class DataRowUserProfileTest extends TestCase {
//    /**
//     * Create the test case
//     *
//     * @param testName name of the test case
//     */
//    public DataRowUserProfileTest(String testName) {
//        super(testName);
//    }
//
//    /**
//     * @return the suite of tests being tested
//     */
//    public static Test suite() {
//        return new TestSuite(DataRowUserProfileTest.class);
//    }
//
//    /**
//     * Ensure that the constructor populates every field of the object it
//     * creates
//     */
//    public void testConstructor() {
//        String email = "shy221@lehigh.edu";
//        String password = "is3eZd&v";
//        String username = "shy221";
//        String intro = "hi";
//        int uid = 4;
//        String salt = "FbTi43BuQHEBbRPjgipuHQme8z8gFQQ50TKiQ6jztHs=";
//
//        DataRowUserProfile d = new DataRowUserProfile(uid, username, email, salt, password, intro);
//
//        assertTrue(d.uEmail.equals(email));
//        assertTrue(d.uPassword.equals(password));
//        assertTrue(d.uSername.equals(username));
//        assertTrue(d.uIntro.equals(intro));
//        assertTrue(d.uId == uid);
//        assertTrue(d.uSalt.equals(salt));
//    }
//
//    /**
//     * Ensure that the copy constructor works correctly
//     */
//    public void testCopyconstructor() {
//        String email = "shy221@lehigh.edu";
//        String password = "is3eZd&v";
//        String username = "shy221";
//        String intro = "hi";
//        int uid = 4;
//        String salt = "FbTi43BuQHEBbRPjgipuHQme8z8gFQQ50TKiQ6jztHs=";
//        DataRowUserProfile d = new DataRowUserProfile(uid, username, email, salt, password, intro);
//        DataRowUserProfile d2 = new DataRowUserProfile(uid, username, email, salt, password, intro);
//
//        assertTrue(d2.uEmail.equals(d.uEmail));
//        assertTrue(d2.uPassword.equals(d.uPassword));
//        assertTrue(d2.uSername.equals(d.uSername));
//        assertTrue(d2.uIntro.equals(d.uIntro));
//        assertTrue(d2.uSalt.equals(d.uSalt));
//        assertTrue(d2.uId == d.uId);
//    }
//}