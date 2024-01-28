package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.Date;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.client.FindIterable;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

public class Rating {


    public void run(Scanner scanner, MongoDatabase database){
              
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create rating");
                            System.out.println("2: Delete rating");
                            System.out.println("3: Delete All ratings by user");
                            System.out.println("4: List All ratings");
                            System.out.println("5: List All ratings by user or game");
                          
                
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 
                            if (sub_option == 1) {
                                System.out.print("Enter user-id or username or email: ");
                                String id_or_username_or_email = scanner.nextLine();
                                System.out.print("Enter the game-name or game-id that he wants to rate: ");
                                String gameName_or_gameId = scanner.nextLine();
                                System.out.print("Enter rating: (1-5)");
                                Integer rating = scanner.nextInt();
                                if (rating < 1 || rating > 5) {
                                    System.out.println("Invalid rating. Please try again.");
                                    break;
                                }
                                this.createRating(database, id_or_username_or_email, gameName_or_gameId, rating);
                            }
                            else if (sub_option == 2) {

                                System.out.print("Enter id of rating to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(database, delete);
                              
                            } 
                            else if (sub_option == 3){
                                System.out.print("Enter id, username or email of user to delete all his/her ratings: ");
                                String delete = scanner.nextLine();
                                this.deleteAllByUserIdOrUsernameOrEmail(database, delete);
                            }
                            
                            else if (sub_option == 4) {
                                this.read(scanner, database);
                            } 
                            else if (sub_option == 5){
                               this.readByUserOrGame(scanner, database);
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

    
    private void createRating(MongoDatabase database, String id_or_username_or_email, String gameName_or_gameId, Integer rating){
        Document found_user = database.getCollection("users").find(
            or(
                eq("_id", id_or_username_or_email),
                eq("username", id_or_username_or_email),
                eq("email", id_or_username_or_email)
            )
            ).first();

        if (found_user != null) {

            Document found_game = database.getCollection("games").find(
                  or(  
                    eq("_id", gameName_or_gameId),
                    eq("name", gameName_or_gameId)
                  )
                ).first();
                if (found_game != null) {
                    Document new_rating = new Document();
                    new_rating.append("rating", rating)
                    .append("date",  new Date())
                    .append("user_id", found_user.get("_id"))
                    .append("game_id", found_game.get("_id"));
                    InsertOneResult result = database.getCollection("ratings").insertOne(new_rating);

                    if (result.wasAcknowledged()) {
                        System.out.println("Rating created successfully!");
                    } else {
                        System.out.println("Rating not created.");
                    }
                } else {
                    System.out.println("Game not found.");
                }

        } else {
            System.out.println("user not found.");
        }
    }

    private void read(Scanner scanner, MongoDatabase database) {
        System.out.println("\n");
        int pageSize = 5;
        long totalDocuments = database.getCollection("ratings").countDocuments();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total ratings: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No ratings found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.printf("%-29s %-29s %-29s %-i1 %-s\n", "Id", "User Id", "Game id", "Rating", "Date");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                FindIterable<Document> page = database.getCollection("ratings").find()
                .skip(skipDocuments)
                .limit(pageSize);
                for (Document p : page) {
                    Object id = p.get("_id");
                    System.out.printf("%-29s %-29s %-29s %-1i %-s\n",
                            id.toString(),
                            p.getString("user_id"),
                            p.getString("game_id"),
                            p.getString("rating"),
                            p.getInteger("date"));
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
    private void readByUserOrGame(Scanner scanner, MongoDatabase database) {

        System.out.println("\n");
        System.out.print("Enter username or email or game-name of ratings to search: ");
        String username_or_email_or_gamename = scanner.nextLine();

        int pageSize = 5;
        long totalDocuments = database
                        .getCollection("ratings")
                        .countDocuments(or(
                            eq("user_id", username_or_email_or_gamename),
                            eq("username", username_or_email_or_gamename),
                            eq("email", username_or_email_or_gamename)
                        ));
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total ratings: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No ratings found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.printf("%-29s %-29s %-29s %-i1 %-s\n", "Id", "User Id", "Game id", "Rating", "Date");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                Document foundUser = database.getCollection("users").find(
                    or(
                        eq("username", username_or_email_or_gamename),
                        eq("email", username_or_email_or_gamename)
                    )
                ).first();
                Document foundGame = null;
                if (foundUser == null) {
                    foundGame = database.getCollection("games").find(
                        eq("name", username_or_email_or_gamename)
                    ).first();
                    if (foundGame == null) {
                        System.out.println("User or game not found.");
                        return;
                    }
                }
        
                FindIterable<Document> page = database.getCollection("ratings")
                .find(
                   or(
                        eq("user_id", foundUser.get("_id").toString()),
                        eq("game_id", foundGame.get("_id").toString())
                   )
                ).skip(skipDocuments).limit(pageSize);
        
                for (Document p : page) {
                    Object id = p.get("_id");
                    System.out.printf("%-29s %-29s %-29s %-1i %-s\n",
                            id.toString(),
                            p.getString("user_id"),
                            p.getString("game_id"),
                            p.getString("rating"),
                            p.getInteger("date"));
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


    private void delete(MongoDatabase database, String delete) {
        DeleteResult deleteResult = database.getCollection("ratings").deleteOne( eq("_id", delete));
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("rating deleted successfully!");
        } else {
            System.out.println("No rating deleted.");
        }
    }

    private void deleteAllByUserIdOrUsernameOrEmail(MongoDatabase database, String delete){
        DeleteResult deleteResult = database.getCollection("ratings").deleteMany( 
            or(
                eq("_id", delete),
                eq("user_id", delete),
                eq("game_id", delete)
            )                
         );
         if (deleteResult.getDeletedCount() > 0) {
             System.out.println("ratings deleted successfully!");
         } else {
             System.out.println("No ratings deleted.");
         }
    }
}
