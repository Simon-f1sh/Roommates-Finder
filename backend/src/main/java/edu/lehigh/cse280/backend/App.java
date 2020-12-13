package edu.lehigh.cse280.backend;

import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCrypt;

// Import Google's JSON library
import com.google.gson.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Base64;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.net.URLConnection;

// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import spark.Spark;

//for google oauth
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

public class App 
{
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }
    
    public static void main(String[] args) {

        // gson provides us with a way to turn JSON into objects, and objects
        // into JSON.
        //
        // NB: it must be final, so that it can be accessed from our lambdas
        //
        // NB: Gson is thread-safe.  See 
        // https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse
        final Gson gson = new Gson();

        //HashMap<String, String> session = new HashMap<String, String>();
        HashMap<String, Integer> link = new HashMap<String, Integer>();

        
        

        // Set up the location for serving static files. If the STATIC_LOCATION
        // environment variable is set, we will serve from it. Otherwise, serve
        // from "/web"
        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        

        Spark.port(getIntFromEnv("PORT", 4567));
        Map<String, String> env = System.getenv();
        String db_url = env.get("DATABASE_URL");

        Database db = Database.getDatabase(db_url);
        if (db == null) 
            return;
        db.init();

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
            String idTokenString = req.id_token;
            response.status(200);
            response.type("application/json");

            // Google OAuth
            String email;

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList("939374055996-8c9s33egqvv3lifjc60eh9lf0r3vvdi3.apps.googleusercontent.com"))
                .build();
            
            //for debug
            int admin = 0;
            if (idTokenString.equals("faketoken")) {
                admin = 1;
                email = "yut222@lehigh.edu";
            } else {
                GoogleIdToken idToken = verifier.verify(idTokenString);
                if (idToken != null) {
                    Payload payload = idToken.getPayload();
                    email = payload.getEmail();
                } else {
                    return gson.toJson(new StructuredResponse("error", "invalid id token", null));
                }
            }
            
            if (db.matchUsr(email) == null){
                // We need to create a user
                int addResult = db.insertRowToUser(email, admin);
                if (addResult != 1)
                    return gson.toJson(new StructuredResponse("error", "failed to add user", addResult));
            }
            //String sessionKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            //session.put(email, sessionKey);
            return gson.toJson(new StructuredResponse("ok", "Login success!", db.matchUsr(email)));
        });

        Spark.put("/profile/:uid", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal 
            // Server Error
            int uid = Integer.parseInt(request.params("uid"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            //原本用的是createEntry但因为我们只有一个uTable所以是updateOne
            int returnId = db.updateOne(uid, req.uName, req.uGender, req.uTidiness, req.uNoise, req.uSleepTime, req.uWakeTime, req.uPet, req.uVisitor, req.uHobby);
            if (returnId == -1) {
                return gson.toJson(new StructuredResponse("error", "error in inserting detail info", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "" + returnId, null));
            }

        });

        Spark.get("/profile", (request, response) -> {
            Set<String> queryParamSet = request.queryParams();
            if (queryParamSet.isEmpty()) {
                response.status(200);
                response.type("application/json");
                return gson.toJson(new StructuredResponse("ok", null, db.readAll()));
            } else {
                String params[] = {"gender", "tidiness", "noise", "sleep", "wake", "pet", "visitor"};
                int values[] = {0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10};
                int i = 0;
                for (String param : params) {
                    System.out.print(param + " ");
                    String value = request.queryParams(param);
                    if (value == null) {
                        i += 2;
                        System.out.println("NULL");
                    } else {
                        values[i++] = Integer.parseInt(value);
                        values[i++] = Integer.parseInt(value);
                        System.out.println(value);
                        // response.status(400);
                        // response.type("application/json");
                        // db.disconnect();
                        // return gson.toJson(new StructuredResponse("error", "Server Shutdown", null));
                    }
                }
                ArrayList<DataRowUserProfile> data = db.readAll(values);
                if (data == null) {
                    response.status(400);
                    response.type("application/json");
                    return gson.toJson(new StructuredResponse("error", "Nothing Found", null));
                } else {
                    response.status(200);
                    response.type("application/json");
                    return gson.toJson(new StructuredResponse("ok", null, data));
                }
            }
        });

        // read user profile
        Spark.get("/profile/:uid", (request, response) -> {
            int uid = Integer.parseInt(request.params("uid"));
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            DataRow data = db.readOne(uid);
            if (data == null) {
                return gson.toJson(new StructuredResponse("error", uid + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });

        Spark.delete("/profile/:uid", (request, response) -> {
            // If we can't get an ID, Spark will sned a status 500
            int uid = Integer.parseInt(request.params("uid"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            boolean result = db.deleteOne(uid);
            if (!result) {
                return gson.toJson(new StructuredResponse("error", "unable to delete user" + uid, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, null));
            }

        });












        /*
        //这部分应该不需要了，但留着以防万一
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
        */
    }
    
}
