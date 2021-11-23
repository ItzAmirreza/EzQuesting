package me.deadlight.ezquesting.Database;

import com.mongodb.*;
import me.deadlight.ezquesting.EzQuesting;
import java.net.UnknownHostException;
public class DatabaseProvider {

    public static MongoClient mongoClient;
    public static DB database;
    public static DBCollection userData;

    public static void connectToDatabase(String connectionString) throws UnknownHostException {
        mongoClient = new MongoClient(new MongoClientURI(connectionString));
        database = mongoClient.getDB("EzQuesting");
        if (!database.collectionExists("userdata")) {
            database.createCollection("userdata", null);
        }
        userData = database.getCollection("userdata");
        EzQuesting.logConsole("&aDatabase connection was successful.");

    }


}
