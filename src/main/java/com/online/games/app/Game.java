package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.text;

import java.util.ArrayList;
import java.util.Scanner;

import javax.print.Doc;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.internal.operation.FindAndUpdateOperation;

public class Game {

            public void run(Scanner scanner, MongoDatabase database){
                       
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create game");
                            System.out.println("2: Read games");
                            System.out.println("3: Update game");
                            System.out.println("4: Delete game");
                            System.out.println("5: List All games");
                            System.out.println("6: List All games by category");
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
                                this.create(database, name, age_limit, price);
                            
                            } else if (sub_option == 2) {

                                // Read a user
                                System.out.print("Enter the beginning of the game-name to find: ");
                              
                                this.find(scanner, database);
                     

                            } else if (sub_option == 3) {

                         
                                System.out.print(
                                        "Enter game-id or game-name to update (or press enter to skip): ");
                                
                                this.update(scanner, database);
                            } else if (sub_option == 4) {
                                // Delete a game
                                System.out.print("Enter id or name of game to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(database, delete);
                             
                            } else if (sub_option == 5) {
                                System.out.println("\n");
                                int pageSize = 5;
                                long totalDocuments = database.getCollection("games").countDocuments();
                                int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
                                System.out.printf("Total games: %d\n", totalDocuments);
                                if (totalPages == 0) {
                                    System.out.println("No game found.");
                                }else{
                                    int currentPage = 1; // Start with page 1
                                    boolean paginating = true;
    
                                    while (paginating) {
                                       
                                        System.out.println("\n");
                                        System.out.printf("%-29s %-30s %-5d %-20s %-2i %-9i\n", "Id", "Name", "Price", "Category", "Age Restriction", "Total");
                                        System.out.println(
                                                "----------------------------------------------------------------------------");
    
                                        int skipDocuments = (currentPage - 1) * pageSize;
                                        this.read(database, skipDocuments, pageSize);
    
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
                                this.readByCategory(scanner, database);
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
        private void create(MongoDatabase database, String name, String categoryName, Integer age_restriction, Double price){
        if (name.isEmpty() || price == null || age_restriction == null || categoryName.isEmpty()) {
            System.out.println("Please enter all fields.");
            return;
        }



        Document findCategory = database.getCollection("categories").find(eq("name", categoryName)).first();
        if (findCategory != null) {
            Document newgame = new Document()
            .append("name", name)
            .append("price", price)
            .append("age_restriction", age_restriction);
            newgame.append("category", findCategory);
            database.getCollection("games").insertOne(newgame);
            System.out.println("game created successfully!");
        } else {
            System.out.println("Category not found.");
            return;
        }
    }

    private void read(MongoDatabase database, int skipDocuments, int pageSize) {
        FindIterable<Document> pagegames = database.getCollection("games").find()
        .skip(skipDocuments)
        .limit(pageSize);

        for (Document game : pagegames) {
            System.out.printf("%-29s %-30s %-5d %-20s %-2i %-9i\n",
                game.getString("_id"),
                game.getString("name"),
                game.getString("price"),
                game.getString("category"),
                game.getInteger("age_restriction"),
                game.getInteger("total")
            );
        }
    }

    private void find(Scanner scanner, MongoDatabase database) {
        String name = scanner.nextLine();
        System.out.println("\n");
        int pageSize = 5;
        FindIterable<Document> games = database.getCollection("games").find(text(name));
        long totalDocuments = games.into(new ArrayList<>()).size();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total games: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No game found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.printf("%-29s %-30s %-5d %-20s %-2i %-9i\n", "Id", "Name", "Price", "Category", "Age Restriction", "Total");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                this.read(database, skipDocuments, pageSize);

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

    private void readByCategory(Scanner scanner, MongoDatabase database) {
        String category = scanner.nextLine();
        System.out.println("\n");
        int pageSize = 5;
        FindIterable<Document> games = database.getCollection("games").find(text(category));
        long totalDocuments = games.into(new ArrayList<>()).size();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total games: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No game found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.printf("%-29s %-30s %-5d %-20s %-2i %-9i\n", "Id", "Name", "Price", "Category", "Age Restriction", "Total");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                this.read(database, skipDocuments, pageSize);

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

    private void delete(MongoDatabase database, String delete) {
         DeleteResult deleteResult = database.getCollection("games").deleteOne(or(
                                        eq("name", delete),
                                        eq("_id", delete)));
                                if (deleteResult.getDeletedCount() > 0) {
                                    System.out.println("game deleted successfully!");
                                } else {
                                    System.out.println("No game deleted.");
                                }
    }

    private void update( Scanner scanner, MongoDatabase database){
                                 String id_or_name = scanner.nextLine();

                                Document updateDoc = new Document();
            
                                System.out.print("Enter new name: ");
                                String newName = scanner.nextLine();

                                System.out.print("Enter new category: ");
                                String categoryInput = scanner.nextLine();

                                System.out.print("Enter new price: ");
                              
                                String priceInput = scanner.nextLine();

                                System.out.print("Enter new age restriction: ");
                                String age_restrictionInput = scanner.nextLine();

                                System.out.print("Enter new total: ");
                                String totalInput = scanner.nextLine();

                                Double newPrice = 0.0;
                                if (!priceInput.isEmpty()) {
                                    newPrice = Double.parseDouble(priceInput);
                                }
                                if (!newName.isEmpty()) {
                                    updateDoc.append("name", newName);
                                }
                                if (newPrice != 0.0) {
                                    updateDoc.append("price", newPrice);
                                }
                          
                                if (!age_restrictionInput.isEmpty()) {
                                    updateDoc.append("age_restriction", age_restrictionInput);
                                }
                                if (!totalInput.isEmpty()) {
                                    updateDoc.append("total", totalInput);
                                }
                                if (!updateDoc.isEmpty()) {

                                    if(!categoryInput.isEmpty()){
                                        Document findCategory = database.getCollection("categories").find(eq("name", categoryInput)).first();
                                        if (findCategory != null) {
                                            updateDoc.append("category", findCategory);
                                        }    
                                    }
                                
                                    Document findAndUpdateResult = database.getCollection("games").findOneAndUpdate(or(
                                        eq("name", id_or_name),
                                        eq("_id", id_or_name)
                                    ), new Document("$set", updateDoc));
                                    if (findAndUpdateResult != null) {
                                        System.out.println("game updated successfully!");
                                    } else {
                                        System.out.println("No game found to update.");
                                    }
                                } else {
                                    System.out.println("No updates entered.");
                                }
    }
}
