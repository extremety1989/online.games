package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class User {
        public static void createUser(MongoCollection<Document> collection, String surname, String firstname, Integer age, 
    String email, String username, String password){
        if (surname.isEmpty() || firstname.isEmpty() || age == null || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("Please enter all fields.");
            return;
        }
        ArrayList<Document> games = new ArrayList<Document>();
        ArrayList<Document> comments = new ArrayList<Document>();
        ArrayList<Document> ratings = new ArrayList<Document>();
        ArrayList<Document> transactions = new ArrayList<Document>();
        Document newuser = new Document()
        .append("surname", surname)
        .append("firstname", firstname)
        .append("age", age)
        .append("email", email)
        .append("username", username)
        .append("password", password)
        .append("games", games)
        .append("comments", comments)
        .append("ratings", ratings)
        .append("transactions", transactions);


        collection.insertOne(newuser);
        System.out.println("user created successfully!");
    }

    public static void read(MongoCollection<Document> collection, int skipDocuments, int pageSize) {
        FindIterable<Document> pageusers = collection.find()
        .skip(skipDocuments)
        .limit(pageSize);
        for (Document user : pageusers) {
            Object id = user.get("_id");
            System.out.printf("%-29s %-20s %-20s %-5d\n",
                    id.toString(),
                    user.getString("surname"),
                    user.getString("firstname"),
                    user.getInteger("age"));
        }
    }

    public static void delete(MongoCollection<Document> collection, String delete) {
         DeleteResult deleteResult = collection.deleteOne(or(
                                        eq("username", delete),
                                        eq("email", delete),
                                        eq("_id", delete)));
                                if (deleteResult.getDeletedCount() > 0) {
                                    System.out.println("user deleted successfully!");
                                } else {
                                    System.out.println("No user deleted.");
                                }
    }

    public static void update(MongoCollection<Document> collection, String update, Document updateDoc){
            UpdateResult updateResult = collection.updateOne(
                    or(eq("surname", update), eq("firstname", update)),
                    new Document("$set", updateDoc));

            if (updateResult.getModifiedCount() > 0) {
                System.out.println("user updated successfully!");
            } else {
                System.out.println("No user found.");
            }
    }
}
