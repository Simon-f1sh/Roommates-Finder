package edu.lehigh.cse280.backend;

import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCrypt;

// Import Google's JSON library
import com.google.gson.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import spark.Spark;

public class App 
{
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
        HashMap<String, Integer> link = new HashMap<String, Integer>();

        // Get the Postgres conf from the environment
        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");
        
        // Connect to the database
        Database db = Database.getDatabase(ip, port, user, pass);
        db.init();

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
            DataRowUserProfile userInfo = new DataRowUserProfile(db.matchPwd(email).uId, db.matchPwd(email).uSername, db.matchPwd(email).uEmail, db.matchPwd(email).uSalt, db.matchPwd(email).uPassword, sessionKey);
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

        Spark.post("/register", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            LoginRequest req = gson.fromJson(request.body(), LoginRequest.class);
            response.status(200);
            response.type("application/json");
            // modify functions here
            String email = req.uEmail;
            String password = req.uPassword;
            db.insertRowToUser(email, password);
            return gson.toJson(new StructuredResponse("ok", "Sign up success!", null));
        });

        Spark.post("/counters", (request, response) -> {
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
                int newId = db.createCounter(req.uid, req.value);
                if (newId == -1) {
                    return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", "" + newId, null));
                }

            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));

        });

        Spark.put("/counters/:cid/incr", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will sned
            // a status 500
            int cid = Integer.parseInt(request.params("cid"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            int uid = req.uid;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME of JSON
                response.status(200);
                response.type("application/json");
                DataRow result = db.cIncrement(uid, cid);
                if (result == null) {
                    return gson.toJson(new StructuredResponse("error", "unable to update value of" + cid, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));
        });

        Spark.put("/counters/:cid/decr", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will sned
            // a status 500
            int cid = Integer.parseInt(request.params("cid"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            int uid = req.uid;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME of JSON
                response.status(200);
                response.type("application/json");
                DataRow result = db.cDecrement(uid, cid);
                if (result == null) {
                    return gson.toJson(new StructuredResponse("error", "unable to update value of" + cid, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));
        });

        Spark.put("/counters/:cid/share", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will sned
            // a status 500
            int cid = Integer.parseInt(request.params("cid"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME of JSON
                response.status(200);
                response.type("application/json");
                String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz"
                + ",./;-=+!@#$%^&*";

                // create StringBuffer size of AlphaNumericString
                StringBuilder sb = new StringBuilder(8);

                for (int i = 0; i < 8; i++) {

                    // generate a random number between
                    // 0 to AlphaNumericString variable length
                    int index = (int) (AlphaNumericString.length() * Math.random());

                    // add Character one by one in end of sb
                    sb.append(AlphaNumericString.charAt(index));
                }
                link.put(sb.toString(), cid);
                return gson.toJson(new StructuredResponse("ok", sb.toString(), null));
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));
        });

        Spark.put("/counters/:url", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will sned
            // a status 500
            String url = request.params("url");
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME of JSON
                response.status(200);
                response.type("application/json");
                int cid = link.get(url);
                DataRow result = db.selectCounter(cid);
                if (result == null) {
                    return gson.toJson(new StructuredResponse("error", "unable to get value of" + cid, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, result));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));
        });

        Spark.delete("/counters/:cid", (request, response) -> {
            // If we can't get an ID, Spark will sned a status 500
            int idx = Integer.parseInt(request.params("cid"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            if (sk.equals(session.get(em))){
                // ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                response.type("application/json");
                // NB: we won't concern ourselves too much with the quality of the
                //     message sent on a successful delete
                boolean result = db.deleteCounter(idx);
                if (!result) {
                    return gson.toJson(new StructuredResponse("error", "unable to delete row " + idx, null));
                } else {
                    return gson.toJson(new StructuredResponse("ok", null, null));
                }
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));

        });

        Spark.post("/listcounters", (request, response) -> {
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            String sk = req.sessionKey;
            String em = req.uEmail;
            int uid = req.uid;
            if (sk.equals(session.get(em))){
                return gson.toJson(new StructuredResponse("ok", null, db.selectAllCounter(uid)));
            }
            return gson.toJson(new StructuredResponse("error", "session key not correct..", null));
        });
    }
}
