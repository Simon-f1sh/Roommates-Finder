//package edu.lehigh.cse216.teamname.backend;
//
//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;
//import java.util.Date;
//
///**
// * Unit test for simple App.
// */
//public class DataRowTest extends TestCase {
//    /**
//     * Create the test case
//     *
//     * @param testName name of the test case
//     */
//    public DataRowTest(String testName) {
//        super(testName);
//    }
//
//    /**
//     * @return the suite of tests being tested
//     */
//    public static Test suite() {
//        return new TestSuite(DataRowTest.class);
//    }
//
//    /**
//     * Ensure that the constructor populates every field of the object it
//     * creates
//     */
//    public void testConstructor() {
//        String title = "Test Title";
//        String content = "Test Content";
//        int mid = 17;
//        int uid = 4;
//        int likes = 5;
//        int dislikes = 10;
//        String username = "Eva";
//        Date date = new Date();
//        long datelong = date.getTime();
//        DataRow d = new DataRow(mid, uid, username, title, content, likes, dislikes,  date);
//
//        assertTrue(d.cUsername.equals(username));
//        assertTrue(d.mTitle.equals(title));
//        assertTrue(d.mContent.equals(content));
//        assertTrue(d.mId == mid);
//        assertTrue(d.uId == uid);
//        assertTrue(d.mLikes == likes);
//        assertTrue(d.mDislikes == dislikes);
//        assertFalse(d.mCreated.equals(datelong));
//    }
//
//    /**
//     * Ensure that the copy constructor works correctly
//     */
//    public void testCopyconstructor() {
//        String title = "Test Title For Copy";
//        String content = "Test Content For Copy";
//        int mid = 17;
//        int uid = 4;
//        int likes = 5;
//        int dislikes = 10;
//        String username = "Eva";
//        Date date= new Date();
//        long datelong = date.getTime();
//        DataRow d = new DataRow(mid, uid, username, title, content, likes, dislikes,  date);
//        DataRow d2 = new DataRow(d);
//
//        assertTrue(d2.mTitle.equals(d.mTitle));
//        assertTrue(d2.mContent.equals(d.mContent));
//        assertTrue(d2.cUsername.equals(d.cUsername));
//        assertTrue(d2.mId == d.mId);
//        assertTrue(d2.uId == d.uId);
//        assertTrue(d2.mLikes == d.mLikes);
//        assertTrue(d2.mDislikes == d.mDislikes);
//        assertTrue(d2.mCreated.equals(d.mCreated));
//    }
//}