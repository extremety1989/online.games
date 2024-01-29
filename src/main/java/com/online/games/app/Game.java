package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.text;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;


public class Game {

        private static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("\\p{XDigit}+");

    private static boolean isHexadecimal(String input) {
        final Matcher matcher = HEXADECIMAL_PATTERN.matcher(input);
        return matcher.matches();
    }

    public void run(Scanner scanner, MongoDatabase database, Reader reader) {

        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Create game");
            System.out.println("2: Update game");
            System.out.println("3: Delete game");
            System.out.println("4: List All games");
            System.out.println("5: Purchase a game");
            System.out.println("0: Return to main menu");
            System.out.print("Enter option: ");

            int sub_option = scanner.nextInt();
            scanner.nextLine();

            if (sub_option == 1) {

                System.out.print("Enter name: ");
                String name = scanner.nextLine();

                System.out.print("Enter category: ");
                String category = scanner.nextLine();

                System.out.print("Enter price: ");
                String price_string = scanner.nextLine();
                Double price = Double.parseDouble(price_string);
                System.out.print("Enter age limit: ");
                String age_limit_string = scanner.nextLine();
                Integer age_limit = Integer.parseInt(age_limit_string);
                scanner.nextLine();
                this.create(database, name, category, age_limit, price);

            } 
            else if (sub_option == 2) {

                System.out.print(
                        "Enter game-id or game-name to update (or press enter to skip): ");

                this.update(scanner, database);
            } 
            else if (sub_option == 3) {
             
                System.out.print("Enter id or name of game to delete: ");
                String delete = scanner.nextLine();
                this.delete(database, delete);

            } else if (sub_option == 4) {
                reader.read(scanner, database, "games");

            } else if (sub_option == 5) {
                this.purchaseAGame(scanner, database);
            } else if (sub_option == 0) {
                sub_exit = true;
                break;
            } else {
                System.out.println("Invalid option. Please try again.");
                break;
            }
        }
    }

    private void create(MongoDatabase database, String name, String categoryName, Integer age_restriction,
            Double price) {
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
            newgame.append("total", 0);
            database.getCollection("games").createIndex(
                    new Document("name", 1).append("_id", 1).append("category", 1).append("price", 1).append("price",
                            1),
                    new IndexOptions().unique(true));
            database.getCollection("games").insertOne(newgame);
            System.out.println("game created successfully!");
        
        } else {
            System.out.println("Category not found.");
            return;
        }
    }

    private void purchaseAGame(Scanner scanner, MongoDatabase database) {
        System.out.print("Enter user-id or username or email: ");
        String id_or_username_or_email = scanner.nextLine();

        if( id_or_username_or_email.isEmpty() ){
            System.out.println("Empty field.");
            return;
        }
        System.out.print("Enter the game-name or game-id that he wants to purchase: ");
        String gameName_or_gameId = scanner.nextLine();

        List<String> bankNames = Arrays.asList(
                "Bank of America",
                "JPMorgan Chase",
                "Wells Fargo",
                "Citigroup",
                "Goldman Sachs",
                "Morgan Stanley",
                "HSBC",
                "Barclays",
                "Royal Bank of Canada",
                "BNP Paribas");

        System.out.println("Enter bank: ");
        System.out.println("[1] Bank of America");
        System.out.println("[2] JPMorgan Chase");
        System.out.println("[3] Wells Fargo");
        System.out.println("[4] Citigroup");
        System.out.println("[5] Goldman Sachs");
        System.out.println("[6] Morgan Stanley");
        System.out.println("[7] HSBC");
        System.out.println("[8] Barclays");
        System.out.println("[9] Royal Bank of Canada");
        System.out.println("[10] BNP Paribas");

        String bankChoice_string = scanner.nextLine();
        Integer bankChoice = Integer.parseInt(bankChoice_string);
        if (0 < bankChoice && bankChoice > 10) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }

        String bankName = bankNames.get(bankChoice - 1);
        System.out.println("Enter bank number (enter to skip): ");

        String bankNumber_string = scanner.nextLine();
        Long bankNumber = null;
        if (!bankNumber_string.isEmpty()) {
            bankNumber = Long.parseLong(bankNumber_string);
        }
        if (bankNumber != null && (bankNumber < 0 || bankNumber > 9999_9999_9999L)) {
            System.out.println("Invalid bank number. Please try again.");
            return;
        }
        System.out.println("Enter amount: ");
        String amount_string = scanner.nextLine();
        Double amount = Double.parseDouble(amount_string);
        if (amount < 0) {
            System.out.println("Invalid amount. Please try again.");
            return;
        }
        System.out.println("Enter a currency US or EUR: ");
        String currency = scanner.nextLine();
        Document found_user;
        if (isHexadecimal(id_or_username_or_email)){
            found_user = database.getCollection("users").find(eq("_id",
             new ObjectId(id_or_username_or_email))).first();
        }else {
            found_user = database.getCollection("users").find(
                    or(
                            eq("username", id_or_username_or_email),
                            eq("email", id_or_username_or_email)))
                    .first();
        }


        if (found_user != null) {

            Document found_game = null;
            
            if(isHexadecimal(gameName_or_gameId)){
                found_game = database.getCollection("games").find(eq("_id", 
                new ObjectId(gameName_or_gameId))).first();
            }else{
                found_game = database.getCollection("games").find(
                      
                                eq("name", gameName_or_gameId)
                                )
                        .first();
            }
    
            if (found_game != null) {
                if ((int) found_user.get("age") >= (int) found_game.get("age_restriction")) {

                    Document new_purchase = new Document();

                    new_purchase.append("amount", amount)
                            .append("currency", currency);
                    if (bankName != null && bankNumber != null) {
                        new_purchase.append("bank", new Document()
                                .append("name", bankName)
                                .append("number", bankNumber));
                    }
                    
                    new_purchase.append("created_at", new Date());

                    InsertOneResult result = database.getCollection("purchases").insertOne(new_purchase);

                    if (result.wasAcknowledged()) {
                        System.out.println("Transaction created successfully!");
                        found_game.put("total", (int) found_game.get("total") + 1);

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


    private void delete(MongoDatabase database, String delete) {
      
        DeleteResult deleteResult;
        if(isHexadecimal(delete)){
            deleteResult = database.getCollection("games").deleteOne(new Document("_id", new ObjectId(delete)));
        }else{
            deleteResult = database.getCollection("games").deleteOne(eq("name", delete));
        }

        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("game deleted successfully!");
        } else {
            System.out.println("No game deleted.");
        }

    }



    private void update(Scanner scanner, MongoDatabase database) {
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

        Double newPrice = null;
        if (!priceInput.isEmpty()) {
            newPrice = Double.parseDouble(priceInput);
        }
        if (!newName.isEmpty()) {
            updateDoc.append("name", newName);
        }
        if (newPrice != null) {
            updateDoc.append("price", newPrice);
        }

        if (!age_restrictionInput.isEmpty()) {
            updateDoc.append("age_restriction", Integer.parseInt(age_restrictionInput));
        }
        if (!totalInput.isEmpty()) {
            updateDoc.append("total", Integer.parseInt(totalInput));
        }
        if (!updateDoc.isEmpty()) {

            if (!categoryInput.isEmpty()) {
                Document findCategory = database.getCollection("categories").find(eq("name", categoryInput)).first();
                if (findCategory != null) {
                    updateDoc.append("category", findCategory);
                }
            }

            Document findAndUpdateResult;
            if ( isHexadecimal(id_or_name) ) {
                findAndUpdateResult = database.getCollection("games").findOneAndUpdate(eq("_id", new ObjectId(id_or_name)), new Document("$set", updateDoc));
            } else {
                findAndUpdateResult = database.getCollection("games").findOneAndUpdate(eq("name", id_or_name), new Document("$set", updateDoc));
            }
          
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
