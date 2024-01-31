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

import com.mongodb.client.FindIterable;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

public class Rating {


    public void run(Scanner scanner, MongoDatabase database, Reader reader){
              
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create rating");
                            System.out.println("2: View rating");
                            System.out.println("3: Update rating");
                            System.out.println("4: Delete rating");
                            System.out.println("5: List All ratings");
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 
                            if (sub_option == 1) {
                                System.out.print("Enter user-id or username or email: ");
                                String username_or_email = scanner.nextLine();
                                System.out.print("Enter the game-name or game-id that he wants to rate: ");
                                String gameName = scanner.nextLine();
                                System.out.print("Enter rating: (1-5)");
                                Integer rating = scanner.nextInt();
                                if (rating < 1 || rating > 5) {
                                    System.out.println("Invalid rating. Please try again.");
                                    break;
                                }
                                this.createRating(database, username_or_email, gameName, rating);
                            }
                            else if (sub_option == 2) {

                                System.out.print("Enter id of rating to view: ");
                             
                                this.updateOrView(scanner, database, false);
                              
                            } 
                            else if (sub_option == 3) {

                                System.out.print("Enter id of rating to update: ");
                                this.updateOrView(scanner, database, true);
                              
                            } 
                            else if (sub_option == 4) {

                                System.out.print("Enter id of rating to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(database, delete);
                              
                            } 
                            
                            else if (sub_option == 5) {
                                reader.read(scanner, database, "ratings");
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
            found = database.getCollection("ratings").find(eq("_id", new ObjectId(update))).first();
            if(found == null){
                System.out.println("No rating found.");
                return;
            }

            if(!ok){
                System.out.println(found.toJson(JsonWriterSettings.builder().indent(true).build()));
                return;
            }

            System.out.print("Enter new ratings: ");
            String newrating = scanner.nextLine();

            if (!newrating.isEmpty()) {
                updateDoc.append("score", newrating);
            }
                                
            UpdateResult updateResult = database.getCollection("ratings").updateOne(
                eq("_id", new ObjectId(update)), new Document("$set", updateDoc));

            if (updateResult.getModifiedCount() > 0) {
                System.out.println("user updated successfully!");
            } else {
                System.out.println("No user found.");
            }
    }

    
    private void createRating(MongoDatabase database, String username_or_email, String gameName, Integer rating){
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
            Document new_rating = new Document();
            ObjectId gameId = found_game.getObjectId("_id");
            ObjectId userId = found_user.getObjectId("_id");
            new_rating.append("game_id", gameId);
            new_rating.append("user_id", userId);
            new_rating.append("score", rating)
            .append("date",  new Date());
            InsertOneResult result = database.getCollection("ratings").insertOne(new_rating);

            if (result.wasAcknowledged()) {
        
                System.out.println("Rating created successfully!");
            } else {
                System.out.println("Rating not created.");
            }
    }


    private void delete(MongoDatabase database, String delete) {
    
        DeleteResult deleteResult = database.getCollection("ratings").deleteOne( eq("_id",
         new ObjectId(delete)));
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("rating deleted successfully!");
        } else {
            System.out.println("No rating deleted.");
        }
    }
}
