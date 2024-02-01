package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;


import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

public class Comment {

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
                            System.out.println("1: Create comment");
                            System.out.println("2: View comment");
                            System.out.println("3: Update comment");
                            System.out.println("4: Delete comment");
                            System.out.println("5: List All comments");
             
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
                                this.create(database, username_or_email, gameName_or_gameId, comment);
                            }
                            else if (sub_option == 2) {

                                System.out.print("Enter id of comment to view: ");
                               
                                this.updateOrView(scanner, database,  false);
                              
                            } 
                            else if (sub_option == 3) {

                                System.out.print("Enter id of comment to update: ");
                       
                                this.updateOrView(scanner, database,  true);
                              
                            } 
                            else if (sub_option == 4) {

                                System.out.print("Enter id of comment to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(database, delete);
                              
                            } 
                
                            else if (sub_option == 5) {
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

        private void updateOrView(Scanner scanner, MongoDatabase database, Boolean ok){
        Document updateDoc = new Document();
           String update = scanner.nextLine();
            Document found = null;
            if (isHexadecimal(update)) {
                found = database.getCollection("comments").find(eq("_id", new ObjectId(update))).first();
            } 
            if(found == null){
                System.out.println("No comment found.");
                return;
            }

            if(!ok){
                System.out.println(found.toJson(JsonWriterSettings.builder().indent(true).build()));
                return;
            }

            System.out.print("Enter new comments: ");
            String newcomment = scanner.nextLine();

            if (!newcomment.isEmpty()) {
                updateDoc.append("text", newcomment);
            }
                                
            UpdateResult updateResult = null;
            
            if (isHexadecimal(update)) {
                updateResult = database.getCollection("comments").updateOne(
                    eq("_id", new ObjectId(update)), new Document("$set", updateDoc));
            }

            if (updateResult.getModifiedCount() > 0) {
                System.out.println("comment updated successfully!");
            } else {
                System.out.println("No comment found.");
            }
    }


    private void create(MongoDatabase database, String username_or_email, String gameName,String comment){
        Document found_user = database.getCollection("users").find(
            or(
                eq("username", username_or_email),
                eq("email", username_or_email)
            )
            ).first();

        if (found_user == null) {
            System.out.println("User not found.");
            return;
        }
        Document found_game = database.getCollection("games").find(
            eq("name", gameName)
        ).first();

        if (found_game == null) {
            System.out.println("Game not found.");
            return;
        }

        ObjectId userId = found_user.getObjectId("_id"); 
        ObjectId gameId = found_game.getObjectId("_id");
        Document new_comment = new Document();

        new_comment.append("user_id", userId);
        new_comment.append("game_id", gameId);
        new_comment.append("text", comment)
        .append("created_at",  new Date());
        InsertOneResult result = database.getCollection("comments").insertOne(new_comment);
        if (result.wasAcknowledged()) {
            System.out.println("Comment created successfully!");
        } else {
            System.out.println("Comment not created.");
        }
    }



    private void delete(MongoDatabase database, String delete) {
        ObjectId commentId = new ObjectId(delete);
        DeleteResult deleteResult = database.getCollection("comments").deleteOne( 
            eq("_id", commentId));
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("comment deleted successfully!")
        } else {
            System.out.println("No comment deleted.");
        }
    }

}
