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
                            System.out.println("3: Delete All comments by user");
                            System.out.println("4: List All comments");

                            System.out.println("5: List All comments by date");
                            System.out.println("6: List All comments by user or game");
                          
                
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 
                            if (sub_option == 1) 
                            {
                                System.out.print("Enter id or username or email of user: ");
                                String id_or_username_or_email = scanner.nextLine();
                                System.out.print("Enter name or id of game: ");
                                String gameName_or_gameId = scanner.nextLine();
                                System.out.print("Enter comment: ");
                                String comment = scanner.nextLine();
                                this.createComment(database, id_or_username_or_email, gameName_or_gameId, comment);
                            }
                            else if (sub_option == 2) {

                                System.out.print("Enter id of comment to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(database, delete);
                              
                            } 
                            else if (sub_option == 3){
                                System.out.print("Enter id, username or email of user to delete all his/her comments: ");
                                String delete = scanner.nextLine();
                                this.deleteByUserIdOrUsernameOrEmail(database, delete);
                            }
                            
                            else if (sub_option == 4) {
                                this.read(scanner, database);
                            } 
                            else if (sub_option == 5) {
                                this.readByDate(scanner, database);
                            } 
                            else if (sub_option == 6){
                                this.readCommentSByUserORGame(scanner, database);
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

    private void readCommentSByUserORGame(Scanner scanner, MongoDatabase database){
        
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
                System.out.printf("%-29s %-29s %-29s %-20s %-s\n", "Id", "Game Id", "User Id", "Comment", "Date");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                Document foundUser = database.getCollection("users").find(
                    or(
                        eq("username", username_or_email_or_gamename),
                        eq("email", username_or_email_or_gamename)
                    )
                ).first();
                Document foundGame =  database.getCollection("games").find(
                    eq("name", username_or_email_or_gamename)
                ).first();
               
                FindIterable<Document> page = database.getCollection("comments")
                .find(
                   or(
                        eq("user_id", foundUser.get("_id").toString()),
                        eq("game_id", foundGame.get("_id").toString())
                   )
                ).skip(skipDocuments).limit(pageSize);
        
                for (Document p : page) {
                    Object id = p.get("_id");
                    System.out.printf("%-29s %-29s %-29s %-20s %-s\n",
                            id.toString(),
                            p.getString("game_id"),
                            p.getString("user_id"),
                            p.getString("comment"),
                            p.getString("created_at"));
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
                System.out.printf("%-29s %-29s %-29s %-20s %-5\n", "Id", "Game Id", "User Id", "Comment", "Date");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                FindIterable<Document> page = database.getCollection("comments").find()
                .skip(skipDocuments)
                .limit(pageSize);
                for (Document p : page) {
                    Object id = p.get("_id");
                    System.out.printf("%-29s %-29s %-29s  %-20s %-s\n",
                            id.toString(),
                            p.getString("game_id"),
                            p.getString("user_id"),
                        
                            p.getString("comment"),
                            p.getString("created_at"));
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
    private void createComment(MongoDatabase database, String id_or_username_or_email, String gameName_or_gameId, String comment){
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
                    Document new_comment = new Document();
                    new_comment.append("comment", comment)
                    .append("date",  new Date())
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
                System.out.printf("%-29s %-29s %-20s %-20s %-5s\n", "Id", "Game Id","User Id", "Comment", "Date");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                FindIterable<Document> page = database.getCollection("comments").find(eq("date", date)).skip(skipDocuments).limit(pageSize);
                for (Document p : page) {
                    Object id = p.get("_id");
                    System.out.printf("%-29s %-20s %-20s %-5s\n",
                            id.toString(),
                            p.getString("game_id"),
                            p.getString("user_id"),
                            p.getString("comment"),
                            p.getString("created_at"));
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
                eq("user_id", new ObjectId(delete)),
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
