package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

public class Comment {


    public void run(Scanner scanner, MongoDatabase database, Reader reader){
                        // Users management
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create comment");
                            System.out.println("2: Delete comment");
                            System.out.println("3: List All comments");
             
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 
                            if (sub_option == 1) 
                            {
                                System.out.print("Enter id or username or email of user: ");
                                String username_or_email = scanner.nextLine();
                                System.out.print("Enter name of game: ");
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
                
                            else if (sub_option == 3) {
                                reader.read(scanner, database, "comments");
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


    private void createComment(MongoDatabase database, String username_or_email, String gameName,String comment){
        Document found_user = database.getCollection("users").find(
            or(
                eq("username", username_or_email),
                eq("email", username_or_email)
            )
            ).first();

        if (found_user != null) {

            Document found_game = database.getCollection("games").find(
                    eq("name", gameName)
                ).first();

                if (found_game != null) {
                    ObjectId userId = found_user.getObjectId("_id"); 
                    ObjectId gameId = found_game.getObjectId("_id");
                    Document new_comment = new Document();

                    new_comment.append("game_id", gameId);
                    new_comment.append("comment", comment)
                    .append("created_at",  new Date());
                    InsertOneResult result = database.getCollection("comments").insertOne(new_comment);
                    if (result.wasAcknowledged()) {

             
                    ObjectId commentId = new_comment.getObjectId("_id");
                    Bson filter = Filters.eq("_id", userId);
                    Bson push = Updates.push("comments", commentId);
                    database.getCollection("users").updateOne(filter, push);

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



    private void delete(MongoDatabase database, String delete) {
        DeleteResult deleteResult = database.getCollection("comments").deleteOne( eq("_id", new ObjectId(delete)));
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("comment deleted successfully!");
        } else {
            System.out.println("No comment deleted.");
        }
    }

}
