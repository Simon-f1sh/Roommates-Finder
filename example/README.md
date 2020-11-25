# Back-End Server

In phase1:
    App.java:   
    Database.java:  
        edited to  corresponded with the change in table (added likes colume and date colume)
        added PUT messages/:id/likes to increment likes
    DataRow.java:
        changed the constructor so that mCreated is not a local variable stored in DataRow Object. 
        It is now a value stored in the database
    DataRowTest.java:
        tested the constructor and copy constructor of DataRow
    DataRowLite.java:
        a lighter version of DataRow so that it can pass only the id and title to the frontend
    DataRowLiteTest.java:
        tested the constructor of DataRowLite
    SimpleRequest.java:
        used in App.java to get the needed info from the JSON file we get from the frontend
    StructuredResponse.java:
        put all the DataRow/DataRowLite object into mData and structured all the info that frontend needs
    StructuredResponseTest.java:
        check the response format

Google OAuth work flow (phase3):
    1. Front-end sends an id_token to backend /login
    2. Backend access user email using the id_token
    3. Backend checks whether a user with the given email exists in our
       own database (if not, create a new user) and generates a session
       key
    4. Backend sents user info (includes uid, email, session key, etc)
       back to front-end




