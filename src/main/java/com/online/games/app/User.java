package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.lookup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.DeleteResult;

import com.mongodb.client.result.UpdateResult;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.util.Collections.singletonList;

public class User {

    private static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("\\p{XDigit}+");

    private static boolean isHexadecimal(String input) {
        final Matcher matcher = HEXADECIMAL_PATTERN.matcher(input);
        return matcher.matches();
    }

    public void run(Scanner scanner, MongoDatabase database, Reader reader){
                        // Users management
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create user");
                            System.out.println("2: View user");
                            System.out.println("3: Update user");
                            System.out.println("4: Delete user");
                            System.out.println("5: List All users");
                            System.out.println("6: List All comments by user");
                            System.out.println("7: List All ratings by user");
                            System.out.println("8: List All purchases by user");
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
                            
                            } 
                            else if (sub_option == 2) {

                           
                                System.out.print(
                                        "Enter user-id or username or email of user (or press enter to skip): ");

                                this.update(scanner, database, false);
                            } 
                            else if (sub_option == 3) {

                           
                                System.out.print(
                                        "Enter user-id or username or email of user (or press enter to skip): ");

                                this.update(scanner, database, true);
                            } 
                            
                            else if (sub_option == 4) {
                             
                                System.out.print("Enter id, username or email of user to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(database, delete);
                             
                            } else if (sub_option == 5) {
                              
                                reader.read(scanner, database, "users");
                            } 
                            else if (sub_option == 6){
                                
                                System.out.print("Enter user_id, username or email to find: ");
                            
                                this.readAll(scanner, database, "comments");
                            }
                            else if (sub_option == 7){
                                
                                System.out.print("Enter user_id, username or email to find: ");
                            
                                this.readAll(scanner, database, "ratings");
                            }
                            else if (sub_option == 8){
                                
                                System.out.print("Enter user_id, username or email to find: ");
                            
                                this.readAll(scanner, database, "purchases");
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
                .append("password", passwordHash)
                .append("created_at", new Date());
            database.getCollection("users").createIndex(
                new Document("username", 1).append("email", 1).append("_id", 1).append("firstname", 1).append("lastname", 1), 
                new IndexOptions().unique(true));
            database.getCollection("users").insertOne(newuser);
            System.out.println("user created successfully!");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    private void readAll(Scanner scanner, MongoDatabase database, String what) {
    
        String value = scanner.nextLine();
        System.out.println("\n");
        int pageSize = 5;
  
        Bson matchStage;
        if(isHexadecimal(value)){
            matchStage = Aggregates.match(eq("_id", new ObjectId(value)));
        } else{
            matchStage = Aggregates.match(
                or(
                                        eq("username", value),
                                        eq("email", value))
                                    
            );
        }

        Bson lookupStage = Aggregates.lookup(what, what, "_id", what);      
        List<Bson> aggregationPipeline = Arrays.asList(matchStage, lookupStage);
        long totalDocuments = database.getCollection("users").aggregate(aggregationPipeline)
                .into(new ArrayList<>()).size();
   
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total "+what+": %d\n", totalDocuments);

        if (totalPages == 0) {
            System.out.println("No "+what+" found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------------------------------------");
            
                int skipDocuments = (currentPage - 1) * pageSize;

               
                Bson limitStage = Aggregates.limit(pageSize); // set your desired limit here
                Bson skipStage = Aggregates.skip(skipDocuments); // set your desired skip value here

               
                lookupStage = Aggregates.lookup(what, what, "_id", what);
                
                aggregationPipeline = Arrays.asList(matchStage, lookupStage, skipStage, limitStage);
                
                database.getCollection("users").aggregate(aggregationPipeline)
                        .into(new ArrayList<>())
                        .forEach(document -> System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build())));


                // Pagination controls
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------------------------------------");
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




    private void delete(MongoDatabase database, String delete) {
         DeleteResult deleteResult;
         if(isHexadecimal(delete)){
           deleteResult = database.getCollection("users").deleteOne(new Document("_id", new ObjectId(delete)));
         } else{
            deleteResult = database.getCollection("users").deleteOne(or(
                                        eq("username", delete),
                                        eq("email", delete))
                                    );
         }

        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("user deleted successfully!");
        } else {
            System.out.println("No user deleted.");
        }
    }

    private void update(Scanner scanner, MongoDatabase database, Boolean ok){
        Document updateDoc = new Document();
                                String update = scanner.nextLine();
            Document found;
            if (isHexadecimal(update)) {
                found = database.getCollection("users").find(eq("_id", new ObjectId(update))).first();
            } else {
                found = database.getCollection("users").find(or(
                                            eq("username", update),
                                            eq("email", update))
                                        ).first();
            }
            if(found == null){
                System.out.println("No user found.");
                return;
            }

            if(!ok){
                System.out.println(found.toJson(JsonWriterSettings.builder().indent(true).build()));
                return;
            }



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
            UpdateResult updateResult;
            
            if (isHexadecimal(update)) {
                updateResult = database.getCollection("users").updateOne(
                    eq("_id", new ObjectId(update)), new Document("$set", updateDoc));
            } else {
                updateResult = database.getCollection("users").updateOne(
                    or(
                        eq("email", update),
                        eq("username", update)
                    ),
                    new Document("$set", updateDoc));
            }

            if (updateResult.getModifiedCount() > 0) {
                System.out.println("user updated successfully!");
            } else {
                System.out.println("No user found.");
            }
    }
}
