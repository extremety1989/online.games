package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.ArrayList;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class Game {

            public void run(MongoCollection<Document> collection, Scanner scanner, MongoDatabase database){
                       
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create game");
                            System.out.println("2: Read game");
                            System.out.println("3: Update game");
                            System.out.println("4: Delete game");
                            System.out.println("5: List All games");
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 

                            if (sub_option == 1) {

                                // Create a new user
                                System.out.print("Enter name: ");
                                String name = scanner.nextLine();
                      
                                System.out.print("Enter price: ");
                                Double price = scanner.nextDouble();

                                System.out.print("Enter age limit: ");
                                Integer age_limit = scanner.nextInt();
                                scanner.nextLine(); 
                                this.create(database.getCollection("games"), name, age_limit, price);
                            
                            } else if (sub_option == 2) {

                                // Read a user
                                System.out.print("Enter name to find: ");
                                String name = scanner.nextLine();
                                Document founduser = collection.find(eq("name", name)).first();
                                if (founduser != null) {
                                    System.out.println("Name: " + founduser.getString("name") + " Price:" + founduser.getInteger("price"));
                                } else {
                                    System.out.println("game not found.");
                                }

                            } else if (sub_option == 3) {

                         
                                System.out.print(
                                        "Enter game-name or price to update (or press enter to skip): ");
                                Document updateDoc = new Document();
                                String update = scanner.nextLine();

                                System.out.print("Enter new name: ");
                                String newName = scanner.nextLine();

                                System.out.print("Enter new price: ");
                              
                                String priceInput = scanner.nextLine();


                                if (!newName.isEmpty()) {
                                    updateDoc.append("name", newName);
                                }
                                Double newPrice = 0.0;
                                if (!priceInput.isEmpty()) {
                                    newPrice = Double.parseDouble(priceInput);
                                }

                                if (!updateDoc.isEmpty()) {
                                    this.update(collection, update, updateDoc);
                                }
                 
                            } else if (sub_option == 4) {
                                // Delete a game
                                System.out.print("Enter id or name of game to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(collection, delete);
                             
                            } else if (sub_option == 5) {
                                System.out.println("\n");
                                int pageSize = 5;
                                long totalDocuments = collection.countDocuments();
                                int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
                                System.out.printf("Total games: %d\n", totalDocuments);
                                if (totalPages == 0) {
                                    System.out.println("No game found.");
                                }else{
                                    int currentPage = 1; // Start with page 1
                                    boolean paginating = true;
    
                                    while (paginating) {
                                       
                                        System.out.println("\n");
                                        System.out.printf("%-29s %-30s %-5d\n", "Id", "Name", "Price");
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
        private void create(MongoCollection<Document> collection, String name, Integer age_restriction, Double price){
        if (name.isEmpty() || price == null) {
            System.out.println("Please enter all fields.");
            return;
        }

        Document newgame = new Document()
        .append("name", name)
        .append("price", price)
        .append("age_restriction", age_restriction);

        collection.insertOne(newgame);
        System.out.println("game created successfully!");
    }

    private void read(MongoCollection<Document> collection, int skipDocuments, int pageSize) {
        FindIterable<Document> pagegames = collection.find()
        .skip(skipDocuments)
        .limit(pageSize);
        for (Document game : pagegames) {
            System.out.printf("%-30s %-5d %-40s %-2i\n",
                game.getString("name"),
                game.getString("price"),
                game.getString("category"),
                game.getString("age_restriction")
            );
        }
    }

    private void findGameBasedOnCategory(MongoCollection<Document> collection, String category, int skipDocuments, int pageSize) {
        FindIterable<Document> pagegames = collection.find(eq("category", category))
        .skip(skipDocuments)
        .limit(pageSize);
        for (Document game : pagegames) {
 
            System.out.printf("%-30s %-5d %-40s %-2i\n",
                    game.getString("name"),
                    game.getString("price"),
                    game.getString("category"),
                    game.getString("age_restriction")
                    );
        }
    }

    private void delete(MongoCollection<Document> collection, String delete) {
         DeleteResult deleteResult = collection.deleteOne(or(
                                        eq("name", delete),
                                        eq("_id", delete)));
                                if (deleteResult.getDeletedCount() > 0) {
                                    System.out.println("game deleted successfully!");
                                } else {
                                    System.out.println("No game deleted.");
                                }
    }

    private void update(MongoCollection<Document> collection, String update, Document updateDoc){
            UpdateResult updateResult = collection.updateOne(
                    or(eq("name", update), eq("price", update)),
                    new Document("$set", updateDoc));

            if (updateResult.getModifiedCount() > 0) {
                System.out.println("game updated successfully!");
            } else {
                System.out.println("No game found.");
            }
    }
}
