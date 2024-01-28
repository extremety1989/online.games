package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

public class Comment {
            private static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("\\p{XDigit}+");

    private static boolean isHexadecimal(String input) {
        final Matcher matcher = HEXADECIMAL_PATTERN.matcher(input);
        return matcher.matches();
    }

    public void run(Scanner scanner, MongoDatabase database){
                        // Users management
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create comment");
                            System.out.println("2: Delete comment");
                            System.out.println("3: Delete All comments by user-id or username or email or game-id or game-name");
                            System.out.println("4: List All comments");
                            System.out.println("5: List All comments by username or email or game-name");
                            System.out.println("6: List All comments by date");
                          
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 
                            if (sub_option == 1) 
                            {
                                System.out.print("Enter id or username or email of user: ");
                                String username_or_email = scanner.nextLine();
                                System.out.print("Enter name or id of game: ");
                                String gameName_or_gameId = scanner.nextLine();
                                System.out.print("Enter comment: ");
                                String comment = scanner.nextLine();
                                this.createComment(database, username_or_email, gameName_or_gameId, comment);
                            }
                            else if (sub_option == 2) {

                                System.out.print("Enter id of comment to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(database, delete);
                              
                            } 
                            else if (sub_option == 3){
                                System.out.print("Enter user-id or username or email or game-id or game-name to delete all his/her comments: ");
                                String delete = scanner.nextLine();
                                this.deleteByUserOrGame(database, delete);
                            }
                            
                            else if (sub_option == 4) {
                                this.read(scanner, database);
                            } 
                            else if (sub_option == 5) {
                                this.readCommentSByIdOrUsernameOrEmailORGame(scanner, database);
                            } 
                            else if (sub_option == 6){
                           
                                this.readByDate(scanner, database);
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

    private void readCommentSByIdOrUsernameOrEmailORGame(Scanner scanner, MongoDatabase database){
        
        System.out.println("\n");
        System.out.print("Enter username or email or  game-name to search: ");
        String username_or_email_or_gamename = scanner.nextLine();

        int pageSize = 5;
        long totalDocuments = 0l;
        Document foundGame = null;
        Document foundUser =  database.getCollection("users").find(
           or(
            eq("name", username_or_email_or_gamename),
            eq("email", username_or_email_or_gamename)
           )
        ).first();

        if(foundUser == null){
            foundGame =  database.getCollection("games").find(
                eq("name", username_or_email_or_gamename)
            ).first();
            totalDocuments = database
                    .getCollection("comments")
                    .countDocuments(eq("game_id", foundGame.get("_id").toString()));
        }else{
            totalDocuments = database
                    .getCollection("comments")
                    .countDocuments(eq("user_id", foundUser.get("_id").toString()));
        }
       

        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total comments: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No comments found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.printf("%-29s %-29s %-29s %-20s %-s\n", "Id", "Game Id", "User Id", "Comment", "Date");
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;

               

   
                FindIterable<Document> page;
                if(foundUser == null){
                    page = database.getCollection("comments")
                    .find(
                    
                            eq("game_id", foundGame.get("_id").toString())
                       
                    ).skip(skipDocuments).limit(pageSize);
                }else{
                    page = database.getCollection("comments")
                    .find(
          
                            eq("game_id", foundGame.get("_id").toString())
                       
                    ).skip(skipDocuments).limit(pageSize);
                }
             
        
                for (Document p : page) {
                    Object id = p.get("_id");
                    System.out.printf("%-29s %-29s %-29s %-20s %-s\n",
                            id,
                            p.getString("game_id"),
                            p.getString("user_id"),
                            p.getString("comment"),
                            p.getString("created_at"));
                }
                // Pagination controls
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------");
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
    private void read(Scanner scanner, MongoDatabase database) {
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
                System.out.printf("%-29s %-29s %-29s %-20s %-5s\n", "Id", "Game Id", "User Id", "Comment", "Date");
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                FindIterable<Document> page = database.getCollection("comments").find()
                .skip(skipDocuments)
                .limit(pageSize);
                for (Document p : page) {
                    Object id = p.get("_id");
                    Object user_id = p.get("user_id");
                    Object game_id = p.get("game_id");
                    System.out.printf("%-29s %-29s %-29s  %-20s %-s\n",
                            id,
                            game_id,
                            user_id,
                        
                            p.getString("comment"),
                            p.getString("created_at"));
                }

                // Pagination controls
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------");
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
    private void createComment(MongoDatabase database, String username_or_email, String gameName_or_gameId, String comment){
        Document found_user = database.getCollection("users").find(
            or(
                eq("username", username_or_email),
                eq("email", username_or_email)
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
                    Document new_comment = new Document();
                    new_comment.append("comment", comment)
                    .append("created_at",  new Date())
                    .append("user_id", found_user.get("_id"))
                    .append("game_id", found_game.get("_id"));
                    InsertOneResult result = database.getCollection("comments").insertOne(new_comment);

                    if (result.wasAcknowledged()) {
                        System.out.println("Comment created successfully!");
                    } else {
                        System.out.println("Comment not created.");
                    }
                } else {
                    System.out.println("Game not found.");
                }

        } else {
            System.out.println("user not found.");
        }
    }

    private void readByDate(Scanner scanner, MongoDatabase database) {
        System.out.println("\n");
        System.out.print("Enter date of comment to search: ");
        String date = scanner.nextLine();

        int pageSize = 5;
        long totalDocuments = database
                        .getCollection("comments")
                        .countDocuments(eq("created_at", date));
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total comments: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No comments found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.printf("%-29s %-29s %-20s %-20s %-5s\n", "Id", "Game Id","User Id", "Comment", "Date");
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                FindIterable<Document> page = database.getCollection("comments").find(eq("created_at", date)).skip(skipDocuments).limit(pageSize);
                for (Document p : page) {
                    Object id = p.get("_id");
                    System.out.printf("%-29s %-20s %-20s %-5s\n",
                            id,
                            p.getString("game_id"),
                            p.getString("user_id"),
                            p.getString("comment"),
                            p.getString("created_at"));
                }

                // Pagination controls
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------");
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
        DeleteResult deleteResult = database.getCollection("comments").deleteOne( eq("_id", new ObjectId(delete)));
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("comment deleted successfully!");
        } else {
            System.out.println("No comment deleted.");
        }
    }

    private void deleteByUserOrGame(MongoDatabase database, String delete){

        Document foundUser; 
        if(isHexadecimal(delete)){
            foundUser = database.getCollection("users").find(
                or(
                    eq("_id", new ObjectId(delete))
                )
            ).first();
        }else{
            foundUser = database.getCollection("users").find(
                or(
                    eq("username", delete),
                    eq("email", delete)
                )
            ).first();
        }
        Document foundGame;
        if(isHexadecimal(delete)){
            foundGame =  database.getCollection("games").find(
                eq("_id", new ObjectId(delete))
            ).first();
        }else{
            foundGame =  database.getCollection("games").find(
                eq("name", delete)
            ).first();
        }
        DeleteResult deleteResult = null;
        if (foundGame != null) {
            deleteResult = database.getCollection("comments").deleteMany( 
                eq("game_id", foundGame.get("_id").toString())
            );
        }else if (foundUser != null) {
            deleteResult = database.getCollection("comments").deleteMany( 
                eq("user_id", foundUser.get("_id").toString())
            );
        }
  

         if (deleteResult.getDeletedCount() > 0) {
             System.out.println("comments deleted successfully!");
         } else {
             System.out.println("No comments deleted.");
         }
    }
}
