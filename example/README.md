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

what next phase might work on:
    the datatype name in DataRow, SimpleRequest, StructuredResponse need to be checked. 



