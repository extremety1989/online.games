package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class Category {
            private static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("\\p{XDigit}+");

            private static boolean isHexadecimal(String input) {
                final Matcher matcher = HEXADECIMAL_PATTERN.matcher(input);
                return matcher.matches();
            }

            public void run(Scanner scanner, MongoDatabase database, Reader reader){
                       
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create category");
                            System.out.println("2: Update category");
                            System.out.println("3: Delete category");
                            System.out.println("4: List All categories");
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 

                            if (sub_option == 1) {

              
                                System.out.print("Enter name: ");
                                String name = scanner.nextLine();
                      
                                this.create(database, name);
                            
                            }  
                            else if (sub_option == 2) {

                         
                                System.out.print(
                                        "Enter id of category to view: ");

            
                                this.updateOrView(scanner, database, true);
                            } 
                            else if (sub_option == 2) {

                         
                                System.out.print(
                                        "Enter id of category to update: ");
     
                                this.updateOrView(scanner, database, true);
                            } 
                            else if (sub_option == 3) {
                              
                                System.out.print("Enter id of category to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(database, delete);
                             
                            } else if (sub_option == 4) {
                                reader.read(scanner, database, "categories");
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
    private void create(MongoDatabase database, String name){
        if (name.isEmpty()) {
            System.out.println("Please enter the field.");
            return;
        }

        Document category = new Document()
        .append("name", name);
        database.getCollection("categories").createIndex(
            new Document("name", 1).append("_id", 1),
            new IndexOptions().unique(true));
        database.getCollection("categories").insertOne(category);
        System.out.println("category created successfully!");
    }



    private void delete(MongoDatabase database, String delete) {
        DeleteResult deleteResult;
        if(isHexadecimal(delete)){
          deleteResult = database.getCollection("categories").deleteOne(new Document("_id", new ObjectId(delete)));

        }else{
            deleteResult = database.getCollection("categories").deleteOne(eq("name", delete));
        }
                   
         if (deleteResult.getDeletedCount() > 0) {
            System.out.println("category deleted successfully!");
        } else {
            System.out.println("No category deleted.");
        }
}

    private void updateOrView(Scanner scanner, MongoDatabase database, boolean ok){
        String update = scanner.nextLine();
        if (!ok){
            FindIterable<Document> found = null;
            if (isHexadecimal(update)) {
                found = database.getCollection("categories").find(eq("_id", new ObjectId(update)));
            }
            if(found == null){
                System.out.println("No category found.");
                return;
            }
            for (Document doc : found) {
                System.out.println(doc.toJson(JsonWriterSettings.builder().indent(true).build()));
            }
            return;
        }
        Document updateDoc = new Document();
    

        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();

        if (!newName.isEmpty()) {
            updateDoc.append("name", newName);
        }
        UpdateResult updateResult = null;
        if(isHexadecimal(update)){
            updateResult = database.getCollection("categories").updateOne(
                    eq("_id", new ObjectId(update)), new Document("$set", updateDoc));
                
        }

            if (updateResult.getModifiedCount() > 0) {
                System.out.println("category updated successfully!");
            } else {
                System.out.println("No category found.");
            }
    }
}
