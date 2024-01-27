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
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.MongoDatabase;

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
                            System.out.println("6: Purchase a game");
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 

                            if (sub_option == 1) {

                        
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

                        
                                System.out.print("Enter user_id, username or email to find: ");
                                String username_or_email = scanner.nextLine();
                                this.find(collection, database, username_or_email);

                            } else if (sub_option == 3) {

                           
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
                            else if (sub_option == 6) {
                                System.out.print("Enter username or email: ");
                                String username_or_email = scanner.nextLine();
                                System.out.print("Enter the game-name that he wants to purchase: ");
                                String gameName = scanner.nextLine();
                                System.out.println("Enter bank number: ");
                                System.out.println("Enter amount: ");
                                Double amount = scanner.nextDouble();
                                if (amount < 0) {
                                    System.out.println("Invalid amount. Please try again.");
                                    break;
                                }
                                System.out.println("Enter a currency US or EUR: ");
                                String currency = scanner.nextLine();
                                this.purchaseAGame(collection, database, username_or_email, gameName, amount, currency);
                            }
                            else if (sub_option == 7) {
                                System.out.print("Enter username or email (enter to skip): ");
                                String username_or_email = scanner.nextLine();
                                System.out.print("Enter the game that he wants to purchase: ");
                                String gameName = scanner.nextLine();
                                System.out.println("Enter bank number: ");
                                Integer bankNumber = scanner.nextInt();
                                if (bankNumber < 0 || bankNumber > 999999999999L) {
                                    System.out.println("Invalid bank number. Please try again.");
                                    break;
                                }
                                System.out.println("Enter amount: ");
                                Double amount = scanner.nextDouble();
                                if (amount < 0) {
                                    System.out.println("Invalid amount. Please try again.");
                                    break;
                                }
                                System.out.println("Enter a currency US or EUR: ");
                                String currency = scanner.nextLine();
                                this.purchaseAGameWithCard(collection, database, username_or_email, gameName, bankNumber, amount, currency);
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
        Document newuser = new Document()
        .append("surname", surname)
        .append("firstname", firstname)
        .append("age", age)
        .append("email", email)
        .append("username", username)
        .append("password", password)
        .append("games", new ArrayList<>())
        .append("comments", new ArrayList<>())
        .append("ratings", new ArrayList<>())
        .append("purchases", new ArrayList<>());


        collection.insertOne(newuser);
        System.out.println("user created successfully!");
    }

    private void createMany(MongoCollection<Document> collection, ArrayList<Document> users) {
        collection.insertMany(users);
        System.out.println("Users created successfully!");
    }

    private void purchaseAGame(MongoCollection<Document> collection, 
    MongoDatabase database,
    String username_or_email, String gameName, Double amount, String currency){
       Document found_user = collection.find(
           or(
               eq("username", username_or_email),
               eq("email", username_or_email)
           )
           ).first();
       if (found_user != null) {
               Document found_game = collection.find(
                   eq("name", gameName)
               ).first();
               if (found_game != null) {
                   if ((int) found_user.get("age") >= (int) found_game.get("age_restriction")) {

                       Document new_purchase = new Document()
                       .append("amount", amount)
                       .append("currency", currency)
                       .append("date",  new Date());
                       InsertOneResult result = database.getCollection("purchases").insertOne(new_purchase);
                       if (result.wasAcknowledged()) {
                         
                            collection.updateOne(
                                eq("_id", found_user.get("_id")),
                                new Document("$push", new Document("purchases",

                                    new Document("purchase_id", new_purchase.get("_id"))
                                        .append("game_id", found_game.get("_id"))
                                ))
                            );

                       } else {
                           System.out.println("Purchase not created.");
                       }
                  

                   }else {
                       System.out.println("not old enough to buy this game.");
                   }
               } else {
                   System.out.println("Game not found.");
               }
       } else {
           System.out.println("user not found.");
       }
   }

    private void purchaseAGameWithCard(MongoCollection<Document> collection, MongoDatabase database, String username_or_email, String gameName,
      Integer bankNumber, Double amount, String currency){
        Document found_user = collection.find(
            or(
                eq("username", username_or_email),
                eq("email", username_or_email)
            )
            ).first();
        if (found_user != null) {
            Document found_game = collection.find(
                    eq("name", gameName)
                ).first();
                if (found_game != null) {
                    if ((int) found_user.get("age") >= (int) found_game.get("age_restriction")) {

                        Document new_purchase = new Document()
                        .append("amount", amount)
                        .append("currency", currency)
                        .append("number", bankNumber)
                        .append("date",  new Date());
                        InsertOneResult result = collection.insertOne(new_purchase);
                        if (result.wasAcknowledged()) {
                            collection.updateOne(
                                eq("_id", found_user.get("_id")),
                                new Document("$push", new Document("purchases",

                                    new Document("purchase_id", new_purchase.get("_id"))
                                        .append("game_id", found_game.get("_id"))
                                ))
                            );
                        } else {
                            System.out.println("Transaction not created.");
                        }
                   

                    }else {
                        System.out.println("not old enough to buy this game.");
                    }
                } else {
                    System.out.println("Game not found.");
                }
        } else {
            System.out.println("user not found.");
        }
    }

    private void read(MongoCollection<Document> collection, int skipDocuments, int pageSize) {
        FindIterable<Document> pageusers = collection.find()
        .skip(skipDocuments)
        .limit(pageSize);
        for (Document user : pageusers) {
            Object id = user.get("_id");
            System.out.printf("%-29s %-20s %-20s %-5d %-40s %-20s %-40s\n",
                    id.toString(),
                    user.getString("surname"),
                    user.getString("firstname"),
                    user.getInteger("age"),
                    user.getString("email"),
                    user.getString("username"),
                    user.getString("password")
            );
        }
    }

    private void find(MongoCollection<Document> collection, MongoDatabase database, String value){
        Document found = collection.find(or(
            eq("username", value),
            eq("email", value),
            eq("_id", value)
        )).first();
        if (found != null) {
            System.out.println(found.getString("surname") + " " 
            + found.getString("firstname") + " " + found.getInteger("age")
            + " " + found.getString("email") + " " + found.getString("username")
            + " " + found.getString("password")
            + " " + database.getCollection("games").find(eq("user_id", found.get("_id")))
            + " " + database.getCollection("comments").find(eq("user_id", found.get("_id")))
            + " " + database.getCollection("ratings").find(eq("user_id", found.get("_id")))
            + " " + database.getCollection("transactions").find(eq("user_id", found.get("_id")))
            );
        } else {
            System.out.println("user not found.");
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
