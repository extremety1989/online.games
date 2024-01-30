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

public class Purchase {


    public void run(Scanner scanner, MongoDatabase database, Reader reader) {
        // Users management
        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Create purchase");
            System.out.println("2: View purchase");
            System.out.println("3: Delete purchase");
            System.out.println("4: List All purchases");

            System.out.println("0: Return to main menu");
            System.out.print("Enter option: ");

            int sub_option = scanner.nextInt();
            scanner.nextLine();
            if (sub_option == 1) {
                System.out.print("Enter id or username or email of user: ");
                String id_or_username_or_email = scanner.nextLine();
                System.out.print("Enter name or id of game: ");
                String gameName_or_gameId = scanner.nextLine();
                System.out.print("Enter bank name: ");
                String bankName = scanner.nextLine();
                System.out.print("Enter bank number: ");
                Integer bankNumber = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Enter amount: ");
                Double amount = scanner.nextDouble();
                scanner.nextLine();
                System.out.print("Enter currency: ");
                String currency = scanner.nextLine();
                this.purchaseAGame(database, id_or_username_or_email, gameName_or_gameId, bankName, bankNumber, amount,
                        currency);
            } 
            
            else if (sub_option == 2) {

                System.out.print("Enter id of purchase to view: ");
                String delete = scanner.nextLine();
                this.deleteOrView(database, delete, false);

            } 

            else if (sub_option == 3){
                System.out.print("Enter id of purchase to delete: ");
                String delete = scanner.nextLine();
                this.deleteOrView(database, delete, true);
            }

            else if (sub_option == 4) {
                reader.read(scanner, database, "purchases");
            }
            else if (sub_option == 0) {
                sub_exit = true;
                break;
            } else {
                System.out.println("Invalid option. Please try again.");
                break;
            }
        }
    }

    private void purchaseAGame(MongoDatabase database,
            String id_or_username_or_email, String gameName_or_gameId,
            String bankName, Integer bankNumber, Double amount, String currency) {

        Document found_user = database.getCollection("users").find(
                or(
       
                        eq("username", id_or_username_or_email),
                        eq("email", id_or_username_or_email)))
                .first();

        if (found_user != null) {

            Document found_game = database.getCollection("games").find(
                    or(
          
                            eq("name", gameName_or_gameId)))
                    .first();
            if (found_game != null) {
                if ((int) found_user.get("age") >= (int) found_game.get("age_restriction")) {

                    Document new_purchase = new Document();

                    new_purchase.append("amount", amount)
                            .append("currency", currency);
                    if (bankName != null && bankNumber != null) {
                        new_purchase.append("bank", new Document().append("name", bankName).append("number",
                         bankNumber));
                    }
                    ObjectId gameId = found_game.getObjectId("_id");
                    new_purchase.append("game_id", gameId);
                    new_purchase.append("created_at", new Date());

                    InsertOneResult result = database.getCollection("purchases").insertOne(new_purchase);

                    if (result.wasAcknowledged()) {

                    ObjectId userId = found_user.getObjectId("_id"); 
                    ObjectId purchaseId = new_purchase.getObjectId("_id");

                    Bson filter = Filters.eq("_id", userId);
                    Bson push = Updates.push("purchases", purchaseId);
                    database.getCollection("users").updateOne(filter, push);

                    } else {
                        System.out.println("Transaction not created.");
                    }

                } else {
                    System.out.println("not old enough to buy this game.");
                }

            } else {
                System.out.println("Game not found.");
            }

        } else {
            System.out.println("user not found.");
        }
    }


    private void deleteOrView(MongoDatabase database, String delete, boolean ok) {
        if(!ok){
            Document found = database.getCollection("purchases").find(
             
       
                        eq("_id", new ObjectId(delete)))
                
                .first();
                if (found != null){
                    System.out.println(found.toJson(JsonWriterSettings.builder().indent(true).build()));
                    return;
                }
                return;
        }
        DeleteResult deleteResult = database.getCollection("purchases").deleteOne(
            eq("_id", new ObjectId(delete));
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("purchase deleted successfully!");
          
            database.getCollection("users").deleteOne(eq("purchases", new ObjectId(delete)));
     
        } else {
            System.out.println("No purchase deleted.");
        }
    }

}
