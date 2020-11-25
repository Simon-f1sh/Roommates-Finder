package edu.lehigh.cse216.teamname.backend;

import java.util.ArrayList;

//phase 3 Yuming Tian
/**
 * DataRowUserProfilePosts holds a row of information.  A row of information consists 
 * of a DataRowUserProfile object and an ArrayList of all posts uploaded by the user
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public.  That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */

 public class DataRowUserProfilePosts {
     /**
      * User's profile
      */
      public DataRowUserProfile userProfile;

      /**
       * An ArrayList of all posts uploaded by the user
       */
      public ArrayList<DataRow> posts;

      /**
       * Constructor
       * @param userProfile The user's profile
       */
      public DataRowUserProfilePosts(DataRowUserProfile userProfile, ArrayList<DataRow> posts) {
          this.userProfile = userProfile;
          this.posts = posts;
      }

 }