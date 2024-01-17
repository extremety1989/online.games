package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class User {

    public void run(MongoCollection<Document> collection, Scanner scanner, MongoDatabase database){
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

                                // Create a new user
                                System.out.print("Enter surname: ");
                                String surname = scanner.nextLine();
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
                                this.create(database.getCollection("users"), surname, firstname, age, email, username, password);
                            
                            } else if (sub_option == 2) {

                                // Read a user
                                System.out.print("Enter username to find: ");
                                String username = scanner.nextLine();
                                Document founduser = collection.find(eq("surname", username)).first();
                                if (founduser != null) {
                                    System.out.println(founduser.getString("surname") + " " 
                                    + founduser.getString("firstname") + " " + founduser.getInteger("age"));
                                } else {
                                    System.out.println("user not found.");
                                }

                            } else if (sub_option == 3) {

                                // Update a user
                                System.out.print(
                                        "Enter surname or firstname of user to update (or press enter to skip): ");
                                Document updateDoc = new Document();
                                String update = scanner.nextLine();

                                System.out.print("Enter new surname: ");
                                String newSurname = scanner.nextLine();
                                System.out.print("Enter new firstname: ");
                                String newFirstname = scanner.nextLine();
                                System.out.print("Enter new age: ");
                                int newAge = 0;
                                String ageInput = scanner.nextLine();

                                if (!ageInput.isEmpty()) {
                                    newAge = Integer.parseInt(ageInput);
                                }

                                if (!newSurname.isEmpty()) {
                                    updateDoc.append("surname", newSurname);
                                }

                                if (!newFirstname.isEmpty()) {
                                    updateDoc.append("firstname", newFirstname);
                                }

                                if (newAge > 0) {
                                    updateDoc.append("age", newAge);
                                }

                                if (!updateDoc.isEmpty()) {
                                    this.update(collection, update, updateDoc);
                                }
                 
                            } else if (sub_option == 4) {
                                // Delete a user
                                System.out.print("Enter id, username or email of user to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(collection, delete);
                             
                            } else if (sub_option == 5) {
                                System.out.println("\n");
                                int pageSize = 5;
                                long totalDocuments = collection.countDocuments();
                                int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
                                System.out.printf("Total users: %d\n", totalDocuments);
                                if (totalPages == 0) {
                                    System.out.println("No users found.");
                                }else{
                                    int currentPage = 1; // Start with page 1
                                    boolean paginating = true;
    
                                    while (paginating) {
                                       
                                        System.out.println("\n");
                                        System.out.printf("%-29s %-20s %-20s %-5s\n", "Id", "Surname", "Firstname", "Age");
                                        System.out.println(
                                                "----------------------------------------------------------------------------");
    
                                        int skipDocuments = (currentPage - 1) * pageSize;
                                        this.read(collection, skipDocuments, pageSize);
    
                                        // Pagination controls
                                        System.out.println(
                                                "----------------------------------------------------------------------------");
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

    private void create(MongoCollection<Document> collection, String surname, String firstname, Integer age, 
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

    private void read(MongoCollection<Document> collection, int skipDocuments, int pageSize) {
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

    private void delete(MongoCollection<Document> collection, String delete) {
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

    private void update(MongoCollection<Document> collection, String update, Document updateDoc){
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
