package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;


import java.util.Scanner;

import org.bson.Document;

import com.mongodb.client.FindIterable;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

public class Comment {


    public void run(Scanner scanner, MongoDatabase database){
                        // Users management
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Delete comment");
                            System.out.println("2: Delete All comments by user");
                            System.out.println("3: List All comments");

                            System.out.println("4: List All comments by date");
                            System.out.println("5: List All comments by user or game");
                          
                
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 

                            if (sub_option == 1) {

                                System.out.print("Enter id of comment to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(database, delete);
                              
                            } 
                            else if (sub_option == 2){
                                System.out.print("Enter id, username or email of user to delete all his/her comments: ");
                                String delete = scanner.nextLine();
                                this.deleteByUserIdOrUsernameOrEmail(database, delete);
                            }
                            
                            else if (sub_option == 3) {
                                System.out.println("\n");
                                int pageSize = 5;
                                long totalDocuments = database.getCollection("comments").countDocuments();
                                int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
                                System.out.printf("Total comments: %d\n", totalDocuments);
                                if (totalPages == 0) {
                                    System.out.println("No comments found.");
                                }else{
                                    int currentPage = 1; // Start with page 1
                                    boolean paginating = true;
    
                                    while (paginating) {
                                       
                                        System.out.println("\n");
                                        System.out.printf("%-29s %-20s %-20s %-5s\n", "Id", "User Id", "Text", "Date");
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
                            else if (sub_option == 4) {
                                System.out.println("\n");
                                System.out.print("Enter date of comment to search: ");
                                String date = scanner.nextLine();

                                int pageSize = 5;
                                long totalDocuments = database
                                                .getCollection("comments")
                                                .countDocuments(eq("date", date));
                                int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
                                System.out.printf("Total comments: %d\n", totalDocuments);
                                if (totalPages == 0) {
                                    System.out.println("No comments found.");
                                }else{
                                    int currentPage = 1; // Start with page 1
                                    boolean paginating = true;
    
                                    while (paginating) {
                                       
                                        System.out.println("\n");
                                        System.out.printf("%-29s %-29s %-29s %-40s %-9i %-5d %-3s %-s\\n", "Id", "User Id", "Game Id", "Bank Name", "Bank Number", "Amount", "Currency", "Date");
                                        System.out.println(
                                                "----------------------------------------------------------------------------");
    
                                        int skipDocuments = (currentPage - 1) * pageSize;
                                        this.readByDate(database, date, skipDocuments, pageSize);
    
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
                            else if (sub_option == 5){
                                System.out.println("\n");
                                System.out.print("Enter username or email or game-name of comments to search: ");
                                String username_or_email_or_gamename = scanner.nextLine();

                                int pageSize = 5;
                                long totalDocuments = database
                                                .getCollection("comments")
                                                .countDocuments(or(
                                                    eq("user_id", username_or_email_or_gamename),
                                                    eq("username", username_or_email_or_gamename),
                                                    eq("email", username_or_email_or_gamename)
                                                ));
                                int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
                                System.out.printf("Total comments: %d\n", totalDocuments);
                                if (totalPages == 0) {
                                    System.out.println("No comments found.");
                                }else{
                                    int currentPage = 1; // Start with page 1
                                    boolean paginating = true;
    
                                    while (paginating) {
                                       
                                        System.out.println("\n");
                                        System.out.printf("%-29s %-40s %-5d %-3s %-s\\n"
                                                , "Id", "Bank Name", "Bank Number", "Amount", "Currency", "Date");
                                        System.out.println(
                                                "----------------------------------------------------------------------------");
    
                                        int skipDocuments = (currentPage - 1) * pageSize;
                                        this.readByUserOrGame(database, username_or_email_or_gamename, skipDocuments, pageSize);
    
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


    private void read(MongoDatabase database, int skipDocuments, int pageSize) {
        FindIterable<Document> page = database.getCollection("comments").find()
        .skip(skipDocuments)
        .limit(pageSize);
        for (Document p : page) {
            Object id = p.get("_id");
            System.out.printf("%-29s %-29s %-29s %-40s %-5d %-3s %-s\n",
                    id.toString(),
                    p.getString("user_id"),
                    p.getString("game_id"),
                    p.getString("bankName"),
                    p.getInteger("bankNumber"),
                    p.getDouble("amount"),
                    p.getString("currency"),
                    p.getString("date"));
        }
    }
    private void readByUserOrGame(MongoDatabase database, String username_or_email_or_gameName, int skipDocuments, int pageSize) {

        Document foundUser = database.getCollection("users").find(
            or(
                eq("username", username_or_email_or_gameName),
                eq("email", username_or_email_or_gameName)
            )
        ).first();
        Document foundGame = null;
        if (foundUser == null) {
            foundGame = database.getCollection("games").find(
                eq("name", username_or_email_or_gameName)
            ).first();
            if (foundGame == null) {
                System.out.println("User or game not found.");
                return;
            }
        }

        FindIterable<Document> page = database.getCollection("comments")
        .find(
           or(
                eq("user_id", foundUser.get("_id").toString()),
                eq("game_id", foundGame.get("_id").toString())
           )
        ).skip(skipDocuments).limit(pageSize);

        for (Document p : page) {
            Object id = p.get("_id");
            System.out.printf("%-29s %-40s %-5d %-3s %-s\n",
                    id.toString(),
                    p.getString("bankName"),
                    p.getInteger("bankNumber"),
                    p.getDouble("amount"),
                    p.getString("currency"),
                    p.getString("date"));
        }
    }

    private void readByDate(MongoDatabase database, String date, int skipDocuments, int pageSize) {
        FindIterable<Document> page = database.getCollection("comments").find(eq("date", date)).skip(skipDocuments).limit(pageSize);
        for (Document p : page) {
            Object id = p.get("_id");
            System.out.printf("%-29s %-29s %-29s %-40s %-9i %-5d %-3s %-s\n",
                    id.toString(),
                    p.getString("user_id"),
                    p.getString("game_id"),
                    p.getString("bankName"),
                    p.getInteger("bankNumber"),
                    p.getDouble("amount"),
                    p.getString("currency"),
                    p.getString("date"));
        }
   }

    private void delete(MongoDatabase database, String delete) {
        DeleteResult deleteResult = database.getCollection("comments").deleteOne( eq("_id", delete));
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("comment deleted successfully!");
        } else {
            System.out.println("No comment deleted.");
        }
    }

    private void deleteByUserIdOrUsernameOrEmail(MongoDatabase database, String delete){
        DeleteResult deleteResult = database.getCollection("comments").deleteMany( 
            or(
                eq("user_id", delete),
                eq("username", delete),
                eq("email", delete)
            )                
         );
         if (deleteResult.getDeletedCount() > 0) {
             System.out.println("comments deleted successfully!");
         } else {
             System.out.println("No comments deleted.");
         }
    }
}
