package edu.lehigh.cse216.teamname.backend;

/**
 * OAuth request provides a format to store JSON returned 
 * by Google OAuth Server. All fields must be set to match
 * the response from Google.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */

 public class OAuthRequest {
    /**
     * The short-lifed access token returned from Google
     */
    public String id_token;
 }