package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.xml.crypto.Data;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {

    public void run(Scanner scanner, MongoDatabase database){
                        // Users management
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create user");
                            System.out.println("2: Read user");
                            System.out.println("3: Update user");
                            System.out.println("4: Delete user");
                            System.out.println("5: List All users");
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 

                            if (sub_option == 1) {

                        
                                System.out.print("Enter lastname: ");
                                String lastname = scanner.nextLine();
                                System.out.print("Enter firstname: ");
                                String firstname = scanner.nextLine();
                                System.out.print("Enter age: ");
                                int age = scanner.nextInt();
                                scanner.nextLine(); 
                                System.out.print("Enter email: ");
                                String email = scanner.nextLine();
                                System.out.print("Enter username: ");
                                String username = scanner.nextLine();
                                System.out.print("Enter password: ");
                                String password = scanner.nextLine();
                                this.create(database, 
                                lastname, firstname, age, email, username, password);
                            
                            } else if (sub_option == 2) {

                        
                                System.out.print("Enter user_id, username or email to find: ");
                                String id_or_username_or_email = scanner.nextLine();
                                this.find(database, id_or_username_or_email);

                            } else if (sub_option == 3) {

                           
                                System.out.print(
                                        "Enter lastname or firstname of user to update (or press enter to skip): ");
                                Document updateDoc = new Document();
                                String update = scanner.nextLine();

                                System.out.print("Enter new lastname: ");
                                String newlastname = scanner.nextLine();
                                System.out.print("Enter new firstname: ");
                                String newFirstname = scanner.nextLine();
                                System.out.print("Enter new age: ");
                                int newAge = 0;
                                String ageInput = scanner.nextLine();
                                System.out.print("Enter new email: ");
                                String newEmail = scanner.nextLine();
        
                                System.out.print("Enter new password: ");
                                String newPassword = scanner.nextLine();

                                if (!ageInput.isEmpty()) {
                                    newAge = Integer.parseInt(ageInput);
                                }

                                if (!newlastname.isEmpty()) {
                                    updateDoc.append("lastname", newlastname);
                                }

                                if (!newFirstname.isEmpty()) {
                                    updateDoc.append("firstname", newFirstname);
                                }

                                if (newAge > 0) {
                                    updateDoc.append("age", newAge);
                                }

                                if (!newEmail.isEmpty()) {
                                    updateDoc.append("email", newEmail);
                                }

                                if (!newPassword.isEmpty()) {
                                    MessageDigest messageDigest;
                                    try {
                                        messageDigest = MessageDigest.getInstance("SHA-256");
                                        messageDigest.update(newPassword.getBytes());
                                        String passwordHash = new String(messageDigest.digest());
                                        updateDoc.append("password", passwordHash);
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (!updateDoc.isEmpty()) {
                                    this.update(database, update, updateDoc);
                                }
                 
                            } else if (sub_option == 4) {
                             
                                System.out.print("Enter id, username or email of user to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(database, delete);
                             
                            } else if (sub_option == 5) {
                                System.out.println("\n");
                                int pageSize = 5;
                                long totalDocuments = database.getCollection("users").countDocuments();
                                int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
                                System.out.printf("Total users: %d\n", totalDocuments);
                                if (totalPages == 0) {
                                    System.out.println("No users found.");
                                }else{
                                    int currentPage = 1; // Start with page 1
                                    boolean paginating = true;
    
                                    while (paginating) {
                                       
                                        System.out.println("\n");
                                        System.out.printf("%-29s %-20s %-20s %-5s %-30s %-20s\n", "Id", "lastname", "Firstname", "Age", "Email", "Username");
                                        System.out.println(
                                                "------------------------------------------------------------------------------------------------------");
    
                                        int skipDocuments = (currentPage - 1) * pageSize;
                                        this.read(database, skipDocuments, pageSize);
    
                                        // Pagination controls
                                        System.out.println(
                                                "------------------------------------------------------------------------------------------------------");
                                        System.out.print("\n");
                                        System.out.printf("Page %d of %d\n", currentPage, totalPages);
                                        System.out.print("\n");
                                        System.out.printf("n: Next page | p: Previous page | q: Quit\n");
                                        System.out.print("\n");
                                        System.out.print("Enter option: ");
    
                                        String paginationOption = scanner.nextLine();
    
                                        switch (paginationOption) {
                                            case "n":
                                                if (currentPage < totalPages) {
                                                    currentPage++;
                                                } else {
                                                    System.out.println("You are on the last page.");
                                                }
                                                break;
                                            case "p":
                                                if (currentPage > 1) {
                                                    currentPage--;
                                                } else {
                                                    System.out.println("You are on the first page.");
                                                }
                                                break;
                                            case "q":
                                                paginating = false;
                                                break;
                                            default:
                                                System.out.println("Invalid option. Please try again.");
                                                break;
                                        }
                                    }
                                }
                            } 


                            else if (sub_option == 0) {
                                sub_exit = true;
                                break;
                            }else {
                                System.out.println("Invalid option. Please try again.");
                                break;
                            }
                        }
    }

    private void create(MongoDatabase database, String lastname, String firstname, Integer age, 
        String email, String username, String password){
        if (lastname.isEmpty() || firstname.isEmpty() || age == null || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("Please enter all fields.");
            return;
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            String passwordHash = new String(messageDigest.digest());
            Document newuser = new Document()
                .append("lastname", lastname)
                .append("firstname", firstname)
                .append("age", age)
                .append("email", email)
                .append("username", username)
                .append("password", passwordHash);
            database.getCollection("users").createIndex(
                new Document("username", 1).append("email", 1).append("_id", 1).append("firstname", 1).append("lastname", 1), 
                new IndexOptions().unique(true));
            database.getCollection("users").insertOne(newuser);
            System.out.println("user created successfully!");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }




    private void read(MongoDatabase database, int skipDocuments, int pageSize) {
        FindIterable<Document> pageusers = database.getCollection("users").find()
        .skip(skipDocuments)
        .limit(pageSize);
        for (Document user : pageusers) {
            Object id = user.get("_id");
            System.out.printf("%-29s %-20s %-20s %-5d %-40s %-20s %-20s\n",
                    id.toString(),
                    user.getString("lastname"),
                    user.getString("firstname"),
                    user.getInteger("age"),
                    user.getString("email"),
                    user.getString("username"),
                    user.getString("password")
            );
        }
    }

    private void find(MongoDatabase database, String value){
        Document found = database.getCollection("users").find(or(
            eq("username", value),
            eq("email", value),
            eq("_id", value)
        )).first();
        if (found != null) {
            long totalComments = database.getCollection("comments").countDocuments(eq("user_id", found.get("_id")));
            long totalRatings = database.getCollection("ratings").countDocuments(eq("user_id", found.get("_id")));
            long totalPurchases = database.getCollection("purchases").countDocuments(eq("user_id", found.get("_id")));
            System.out.println(
            "Lastname: " + found.getString("lastname") 
            + " Firstname: "  +  found.getString("firstname") 
            + " Age: " + found.getInteger("age")
            + " Email: " + found.getString("email") 
            + " Username: " + found.getString("username")
            + " Password: " + found.getString("password")
            + " Total comments: " + totalComments
            + " Total ratins: " + totalRatings
            + " Total purchases: " + totalPurchases
            );
        } else {
            System.out.println("user not found.");
        }
    }

    private void delete(MongoDatabase database, String delete) {
         DeleteResult deleteResult = database.getCollection("users").deleteOne(or(
                                        eq("username", delete),
                                        eq("email", delete),
                                        eq("_id", delete)));
                                if (deleteResult.getDeletedCount() > 0) {
                                    System.out.println("user deleted successfully!");
                                } else {
                                    System.out.println("No user deleted.");
                                }
    }

    private void update(MongoDatabase database, String update, Document updateDoc){
            UpdateResult updateResult = database.getCollection("users").updateOne(
                    or(
                        eq("_id", update), 
                        eq("email", update),
                        eq("username", update)
                    ),
                    new Document("$set", updateDoc));

            if (updateResult.getModifiedCount() > 0) {
                System.out.println("user updated successfully!");
            } else {
                System.out.println("No user found.");
            }
    }
}
