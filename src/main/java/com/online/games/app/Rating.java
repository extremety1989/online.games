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

public class Rating {


    public void run(Scanner scanner, MongoDatabase database, Reader reader){
              
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create rating");
                            System.out.println("2: Delete rating");
                            System.out.println("3: List All ratings");
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

                                System.out.print("Enter id of rating to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(database, delete);
                              
                            } 
                            
                            else if (sub_option == 3) {
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

    
    private void createRating(MongoDatabase database, String username_or_email, String gameName, Integer rating){
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
                    Document new_rating = new Document();
                    ObjectId gameId = found_game.getObjectId("_id");
                    new_rating.append("game_id", gameId);
                    new_rating.append("rating", rating)
                    .append("date",  new Date());
                    InsertOneResult result = database.getCollection("ratings").insertOne(new_rating);

                    if (result.wasAcknowledged()) {
                        
                    ObjectId userId = found_user.getObjectId("_id"); 
                  
                    ObjectId ratingId = new_rating.getObjectId("_id");
                    Bson filter = Filters.eq("_id", userId);
                    Bson push = Updates.push("ratings", ratingId);
                    database.getCollection("users").updateOne(filter, push);

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


    private void delete(MongoDatabase database, String delete) {
        DeleteResult deleteResult = database.getCollection("ratings").deleteOne( eq("_id", new ObjectId(delete)));
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("rating deleted successfully!");
        } else {
            System.out.println("No rating deleted.");
        }
    }
}
