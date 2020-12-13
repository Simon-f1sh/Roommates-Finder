package edu.lehigh.cse280.backend;

// Import Google's JSON library
import com.google.gson.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Base64;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.net.URLConnection;

// Import encryption library
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCrypt;

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

        // gson provides us with a way to turn JSON into objects, and objects into JSON.
        final Gson gson = new Gson();

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

        // route for ~/login POST, for user login
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
            return gson.toJson(new StructuredResponse("ok", "Login success!", db.matchUsr(email)));
        });

        Spark.put("/profile/:uid", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal 
            // Server Error
            int uid = Integer.parseInt(request.params("uid"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            //原本用的是createEntry但因为我们只有一个tbluser所以是updateOne
            int returnId = db.updateOne(uid, req.uName, req.uGender, req.uTidiness, req.uNoise, req.uSleepTime, req.uWakeTime, req.uPet, req.uVisitor, req.uHobby);
            if (returnId == -1) {
                return gson.toJson(new StructuredResponse("error", "error in inserting detail info", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "" + returnId, null));
            }

        });

        // route for ~/profile GET, read all user profiles
        Spark.get("/profile", (request, response) -> {
            Set<String> queryParamSet = request.queryParams();
            if (queryParamSet.isEmpty()) {
                // no search query is passed in
                response.status(200);
                response.type("application/json");
                return gson.toJson(new StructuredResponse("ok", null, db.readAll()));
            } else {
                // if there is a search query, intake user preference into the query
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
                    }
                }
                // insert data we get into DataRowUserProfile
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

        // route for ~/profile/:uid GET, read a user profile
        Spark.get("/profile/:uid", (request, response) -> {
            int uid = Integer.parseInt(request.params("uid"));
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
        
        // route for ~/profile/:uid DELETE, delete user profile
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
    }
    
}
