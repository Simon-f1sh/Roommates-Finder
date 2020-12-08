//package edu.lehigh.cse216.teamname.backend;
//
//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;
//
///**
// * Unit test for simple App.
// */
//public class CommentTest extends TestCase {
//    /**
//     * Create the test case
//     *
//     * @param testName name of the test case
//     */
//    public CommentTest(String testName) {
//        super(testName);
//    }
//
//    /**
//     * @return the suite of tests being tested
//     */
//    public static Test suite() {
//        return new TestSuite(CommentTest.class);
//    }
//
//    /**
//     * Ensure that the constructor populates every field of the object it
//     * creates
//     */
//    public void testConstructor() {
//        int uid = 4;
//        int cid = 1;
//        int mid = 1;
//        String username = "shy221";
//        String text = "here is a comment.";
//        Comment d = new Comment(cid, uid, mid, username, text);
//
//        assertTrue(d.cUsername.equals(username));
//        assertTrue(d.cText.equals(text));
//        assertTrue(d.cId == cid);
//        assertTrue(d.uId == uid);
//        assertTrue(d.mId == mid);
//
//    }
//
//    /**
//     * Ensure that the copy constructor works correctly
//     */
//    public void testCopyconstructor() {
//        int uid = 4;
//        int cid = 1;
//        int mid = 1;
//        String username = "shy221";
//        String text = "here is a comment.";
//        Comment d = new Comment(cid, uid, mid, username, text);
//        Comment d2 = new Comment(d);
//
//        assertTrue(d2.cUsername.equals(d.cUsername));
//        assertTrue(d2.cText.equals(d.cText));
//        assertTrue(d2.uId == d.uId);
//        assertTrue(d2.cId == d.cId);
//        assertTrue(d2.mId == d.mId);
//
//    }
//}