package edu.lehigh.cse216.teamname.backend;

// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route

//import bcrypt do generate salt and hash password
import org.springframework.security.crypto.bcrypt.BCrypt;
import spark.Spark;

// Import Google's JSON library
import com.google.gson.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

//import org.springframework.security.crypto.bcrypt;
//import sun.plugin.util.UserProfile;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * For now, our app creates an HTTP server that can only get and add data.
 */
public class App {

    public static void main(String[] args) {

        // gson provides us with a way to turn JSON into objects, and objects
        // into JSON.
        //
        // NB: it must be final, so that it can be accessed from our lambdas
        //
        // NB: Gson is thread-safe.  See 
        // https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse
        final Gson gson = new Gson();
        HashMap<String, String> session = new HashMap<String, String>();

        // Get the port on which to listen for requests
        Spark.port(getIntFromEnv("PORT", 4567));

        // Get the Postgres conf from the environment
        Map<String, String> env = System.getenv();
        String db_url = env.get("DATABASE_URL");
        
        // Connect to the database
        Database db = Database.getDatabase(db_url);
        if (db == null) 
            return;

        // Set up the location for serving static files. If the STATIC_LOCATION
        // environment variable is set, we will serve from it. Otherwise, serve
        // from "/web"
        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        // Set up a route for serving the main page
        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });

        /**
         * phase 2 NOTICE:
         * In every route an email address and a session key will be extracted from the request,
         * and backend will test if the current session key is the same session key
         * originally stored for this email address.
         */

        /**
         * phase 1 routes
         */

        // GET route that returns all params of message.
        //see db.readAll() for more
        // Get the data, embed it in a StructuredResponse, turn it into JSON,
        // and return it.  If there's no data, we return "[]", so there's no need
        // for error handling.
        Spark.post("/listmessages", (request, response) -> {
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            if (sk.equals(session.get(em))){
                return gson.toJson(new StructuredResponse("ok", null, db.readAll()));
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));
        });

        /**
         * Modified for phase 2
         * url/messages/:mid -->
         * detail: title, content, likes, dislikes, comments
         * GET route
         */
        // GET route that returns everything for a single row in the Database.
        // The ":mid" suffix in the first parameter to get() becomes
        // request.params("mid"), so that we can get the requested row ID.  If
        // ":mid" isn't a number, Spark will reply with a status 500 Internal
        // Server Error.  Otherwise, we have an integer, and the only possible 
        // error is that it doesn't correspond to a row with data.
        Spark.post("/messages/:mid", (request, response) -> {
            int mid = Integer.parseInt(request.params("mid"));
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            DataRow data = db.readOne(mid);
            String sk = req.sessionKey;
            String em = req.uEmail;
//            System.out.println(em);
//            System.out.println(sk);
            if (sk.equals(session.get(em))){
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", mid + " not found", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, data));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));


        });

        /**
         * Modified for phase 2
         * adding a new message with user id, message title and message content.
         * url/messages/:mid --> detail: title, content, comment.userid, comments.text, = get, put
         * POST route
         */
        // POST route for adding a new element to the Database.  This will read
        // JSON from the body of the request, turn it into a SimpleRequest 
        // object, extract the user id, title and message, insert them, and return the
        // ID of the newly created row.
        Spark.post("/messages", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal 
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME type of JSON
                // NB: even on error, we return 200, but with a JSON object that
                //     describes the error.
                response.status(200);
                response.type("application/json");
                // NB: createEntry checks for null title and message
                int newId = db.createEntry(req.uid, req.mTitle, req.mMessage);
                if (newId == -1) {
                    return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "" + newId, null));
                }

            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));

        });

        /**
         * Modified for phase 2
         * updating message with message title and message content.
         * comments will be processed in other functions
         * url/messages/:mid --> detail: title, content, comment.userid, comments.text, = get, put
         * PUT route
         */
        //What else can be updated? no need to update here, comments will be processed in other functions
        // PUT route for updating a row in the Database. This is almost
        // exactly the same as POST
        Spark.put("/messages/:id", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME of JSON
                response.status(200);
                response.type("application/json");
                //used to be updateOne(idx, req.mTitle, req.mMessage)
                DataRow result = db.updateOne(idx, req.mTitle, req.mMessage);
                if (result == null) {
                    return gson.toJson(new StructuredResponse("error", "unable to update message " + idx, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct.." + idx, null));

        });

        /**
         * Modified for phase 2
         * updating likes after noticing user hasn't clicked like yet.
         * PUT route
         */
        //PUT route for updating the value of likes in a row in the Database
        Spark.put("/messages/:mid/likes", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will sned
            // a status 500
            int mid = Integer.parseInt(request.params("mid"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            int uid = req.uid;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME of JSON
                response.status(200);
                response.type("application/json");
                DataRow result = db.doLikes(uid, mid);
                if (result == null) {
                    return gson.toJson(new StructuredResponse("error", "unable to update likes of" + mid, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));


        });

        /**
         * Modified for phase 2
         * updating dislikes after noticing user hasn't clicked dislike yet.
         * PUT route
         */
        //PUT route for updating the value of dislikes in a row in the Database
        Spark.put("/messages/:mid/dislikes", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will sned
            // a status 500
            int mid = Integer.parseInt(request.params("mid"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            int uid = req.uid;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME of JSON
                response.status(200);
                response.type("application/json");
                DataRow result = db.doDislikes(uid, mid);
                if (result == null) {
                    return gson.toJson(new StructuredResponse("error", "unable to update dislikes of" + mid, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));
        });

        /**
         * Modified for phase 2
         * deleting likes after noticing user has clicked like already.
         * DELETE route
         */
        //phase 2 delete like
        //when a user already liked the message, the next click user has will reflect as canceling the like
        //DELETE route for updating the value of likes in a row in teh Database
        Spark.delete("/messages/:mid/likes", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will sned
            // a status 500
            int mid = Integer.parseInt(request.params("mid"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            int uid = req.uid;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME of JSON
                response.status(200);
                response.type("application/json");
                DataRow result = db.doLikes(uid, mid);
                if (result == null) {
                    return gson.toJson(new StructuredResponse("error", "unable to cancel like of" + mid, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));


        });

        /**
         * Modified for phase 2
         * deleting dislikes after noticing user has clicked dislike already.
         * DELETE route
         */
        //when a user already disliked the message, the next click user has will reflect as canceling the dislike
        //DELETE route for updating the value of likes in a row in teh Database
        Spark.delete("/messages/:mid/dislikes", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will sned
            // a status 500
            int mid = Integer.parseInt(request.params("mid"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            int uid = req.uid;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME of JSON
                response.status(200);
                response.type("application/json");
                DataRow result = db.doDislikes(uid, mid);
                if (result == null) {
                    return gson.toJson(new StructuredResponse("error", "unable to cancel dislike " + mid, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));
        });

        // DELETE route for removing a row from the Database
        Spark.delete("/messages/:mid", (request, response) -> {
            // If we can't get an ID, Spark will sned a status 500
            int idx = Integer.parseInt(request.params("mid"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                response.type("application/json");
                // NB: we won't concern ourselves too much with the quality of the
                //     message sent on a successful delete
                boolean result = db.deleteOne(idx);
                if (!result) {
                    return gson.toJson(new StructuredResponse("error", "unable to delete row " + idx, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, null));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));
        });

        /**
         * phase 2 routes
         */

        /**
         * phase 2
         * login with email and password
         * POST route
         */

        // POST route for adding a new element to the Database.
        // This will read JSON from the body of the request,
        // turn it into a LoginRequest object, extract the user email and password,
        // insert them, and return if the password is correct.
        Spark.post("/login", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = new SecureRandom();
            int keyBitSize = 256;
            keyGenerator.init(keyBitSize, secureRandom);
            SecretKey secretKey = keyGenerator.generateKey();
            LoginRequest req = gson.fromJson(request.body(), LoginRequest.class);
            response.status(200);
            response.type("application/json");
            // modify functions here
            String email = req.uEmail;
            String password = req.uPassword;
            //get salt from db
            String salt = db.matchPwd(email).uSalt;
            String hash = BCrypt.hashpw(password, salt);
            // get base64 encoded version of the key
            String sessionKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
//            String sessionKey = secretKey.toString();
            DataRowUserProfile userInfo = new DataRowUserProfile(db.matchPwd(email).uId,db.matchPwd(email).uSername, db.matchPwd(email).uEmail, db.matchPwd(email).uSalt, db.matchPwd(email).uPassword, db.matchPwd(email).uIntro, sessionKey);
            session.put(email, sessionKey);
//            boolean matched = BCrypt.checkpw(password + salt, hash);
////            System.out.println(matched);
            if (db.matchPwd(email).uPassword.equals(hash)){
                    return gson.toJson(new StructuredResponse("ok", "Login success!", userInfo));
            }
            else{
                return gson.toJson(new StructuredResponse("error", email + " not found", userInfo));
            }
        });

        /**
         * phase 2
         * update password
         * PUT route
         */
        // PUT route for updating password to the Database.  This will read
        // JSON from the body of the request, turn it into a LoginRequest
        // object, extract the user email and password, insert them, and return the
        // if the password is correct.
        Spark.put("/:uid/updatepwd", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            int idx = Integer.parseInt(request.params("uid"));
            LoginRequest req = gson.fromJson(request.body(), LoginRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            String pwd = req.uPassword;
            String salt = db.matchPwd(em).uSalt;
            String hash = BCrypt.hashpw(pwd, salt);
            System.out.println(hash);
            if (sk.equals(session.get(em))){
                response.status(200);
                response.type("application/json");
                int result = db.uUpdatePwd(idx, salt, hash);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "unable to update password " + idx, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));


        });

        /**
         * phase 2
         * display user profile
         * url/:uid --> user profile: username, email, intro = get, put
         * GET route
         */
        Spark.post("/:uid/userprofile", (request, response) -> {
            int idx = Integer.parseInt(request.params("uid"));
            UserProfileRequest req = gson.fromJson(request.body(), UserProfileRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                response.type("application/json");
                //add new function based on this
                DataRowUserProfile data = db.uReadOne(idx);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", idx + " not found", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, data));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));

        });

        /**
         * phase 2
         * update user profile
         * url/:uid --> user profile: username, email, intro = get, put
         * PUT route
         */
        // PUT route for updating a row in the DataStore. This is almost
        // exactly the same as POST
        Spark.put("/:uid/userprofile", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will sned
            // a status 500
            int idx = Integer.parseInt(request.params("uid"));
            UserProfileRequest req = gson.fromJson(request.body(), UserProfileRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME of JSON
                response.status(200);
                response.type("application/json");
                DataRowUserProfile result = db.uUpdateOne(idx, req.uUsername, req.uIntro);
                if (result == null) {
                    return gson.toJson(new StructuredResponse("error", "unable to update user profile " + idx, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));



        });

        /**
         * phase 2
         * get comments
         * GET route
         */
        //url:uid/comments --> show all comments by this user = get
        // ? GET route that returns everything for a single row in the Database.
        // The ":uid" suffix in the first parameter to get() becomes
        // ? request.params("id"), so that we can get the requested row ID.  If
        // ":uid" isn't a number, Spark will reply with a status 500 Internal
        // Server Error.  Otherwise, we have an integer, and the only possible
        // error is that it doesn't correspond to a row with data.
        Spark.post("/:mid/listcomments", (request, response) -> {
            //?? params =
            int mid = Integer.parseInt(request.params("mid"));
            CommentRequest req = gson.fromJson(request.body(), CommentRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                response.type("application/json");
                ArrayList<Comment> data = db.readAllComments(mid);
                if (data == null) {
                    return gson.toJson(new StructuredResponse("error", mid + " not found", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, data));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));

        });

        /**
         * phase 2
         * add comment
         * POST route
         */
        //post comments
        // POST route for adding a new element to the Database.  This will read
        // JSON from the body of the request, turn it into a SimpleRequest
        // object, extract the title and message, insert them, and return the
        // ID of the newly created row.
        Spark.post("/:mid/comments", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            CommentRequest req = gson.fromJson(request.body(), CommentRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME type of JSON
                // NB: even on error, we return 200, but with a JSON object that
                //     describes the error.
                response.status(200);
                response.type("application/json");
                // NB: createEntry checks for null title and message
                int newId = db.createComment(req.uid, req.mid, req.text);
                if (newId == -1) {
                    return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "comments of " + newId, null));
                }

            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));

        });


//        //do we need this?
//        //url:uid/likes --> show all messages liked by this user : message title and content = get
//        Spark.get("/:uid/likes", (request, response) -> {
//            //?? params =
//            int idx = Integer.parseInt(request.params("id"));
//            // ensure status 200 OK, with a MIME type of JSON
//            response.status(200);
//            response.type("application/json");
//            DataRow data = db.readOne(idx);
//            if (data == null) {
//                return gson.toJson(new StructuredResponse("error", idx + " not found", null));
//            } else {
//                return gson.toJson(new StructuredResponse("ok", null, data));
//            }
//        });

//        //do we need this?
//        //url:uid/dislikes --> show all messages liked by this user : message title and content = get
//        Spark.get("/:uid/dislikes", (request, response) -> {
//            //?? params =
//            int idx = Integer.parseInt(request.params("id"));
//            // ensure status 200 OK, with a MIME type of JSON
//            response.status(200);
//            response.type("application/json");
//            //TODO: add new function
//            DataRow data = db.readOne(idx);
//            if (data == null) {
//                return gson.toJson(new StructuredResponse("error", idx + " not found", null));
//            } else {
//                return gson.toJson(new StructuredResponse("ok", null, data));
//            }
//        });

    }
    /**
    * Get an integer environment varible if it exists, and otherwise return the
    * default value.
    * 
    * @envar      The name of the environment variable to get.
    * @defaultVal The integer value to use as the default if envar isn't found
    * 
    * @returns The best answer we could come up with for a value for envar
    */
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }
}