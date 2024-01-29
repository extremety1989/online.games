package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
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
                            
                            }  else if (sub_option == 2) {

                         
                                System.out.print(
                                        "Enter category-id or category-name to update: ");
                                Document updateDoc = new Document();
                                String update = scanner.nextLine();

                                System.out.print("Enter new name: ");
                                String newName = scanner.nextLine();

                                if (!newName.isEmpty()) {
                                    updateDoc.append("name", newName);
                                }
            
                                if (!updateDoc.isEmpty()) {
                                    this.update(database, update, updateDoc);
                                }
                 
                            } else if (sub_option == 3) {
                              
                                System.out.print("Enter id or name of category to delete: ");
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

    private void read(Scanner scanner, MongoDatabase database){ {
        System.out.println("\n");
        int pageSize = 5;
        long totalDocuments = database.getCollection("categories").countDocuments();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total categories: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No category found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.printf("%-29s %-30s\n", "Id", "Name");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                FindIterable<Document> categories = database.getCollection("categories").find().skip(skipDocuments).limit(pageSize);

                for (Document category : categories) {
                    System.out.printf("%-30s %-30s\n", category.get("_id"), category.get("name"));
                }
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

    private void update(MongoDatabase database, String update, Document updateDoc){
        UpdateResult updateResult;
        if(isHexadecimal(update)){
            updateResult = database.getCollection("categories").updateOne(
                    eq("_id", new ObjectId(update)), new Document("$set", updateDoc));
                
        }else{
            updateResult = database.getCollection("categories").updateOne(
                    eq("name", update), new Document("$set", updateDoc));
        }
        

            if (updateResult.getModifiedCount() > 0) {
                System.out.println("category updated successfully!");
            } else {
                System.out.println("No category found.");
            }
    }
}
