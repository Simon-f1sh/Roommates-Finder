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
//public class DataRowLiteTest extends TestCase {
//    /**
//     * Create the test case
//     *
//     * @param testName name of the test case
//     */
//    public DataRowLiteTest(String testName) {
//        super(testName);
//    }
//
//    /**
//     * @return the suite of tests being tested
//     */
//    public static Test suite() {
//        return new TestSuite(DataRowLiteTest.class);
//    }
//
//    /**
//     * Ensure that the copy constructor from DataRow to DataRowLite works correctly
//     */
//    public void testCopyConstructor() {
//        String title = "Test Title";
//        String content = "Test Content";
//        int id = 17;
//        int likes = 5;
//        Date date= new Date();
//        DataRow d = new DataRow(id, title, content, likes, date);
//        DataRowLite d2 = new DataRowLite(d);
//
//        assertTrue(d2.mId == id);
//        assertTrue(d2.mTitle.equals(title));
//    }
//}
