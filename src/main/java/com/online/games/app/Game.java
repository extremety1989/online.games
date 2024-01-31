package com.online.games.app;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;


import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
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
            System.out.println("2: View game");
            System.out.println("3: Update game");
            System.out.println("4: Delete game");
            System.out.println("5: List All games");
            System.out.println("6: List All games by category");
            System.out.println("7: List All games by price");
            System.out.println("8: Purchase a game");
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
        
                this.create(database, name, category, age_limit, price);

            } 
            else if (sub_option == 2) {

                System.out.print(
                        "Enter game-id or game-name to view: ");

                this.updateOrView(scanner, database, false);
            } 
            else if (sub_option == 3) {

                System.out.print(
                        "Enter game-id or game-name to update (or press enter to skip): ");

                this.updateOrView(scanner, database, true);
            } 
            else if (sub_option == 4) {
             
                System.out.print("Enter id or name of game to delete: ");
                String delete = scanner.nextLine();
                this.delete(database, delete);

            } else if (sub_option == 5) {
                reader.read(scanner, database, "games");

            } else if (sub_option == 6) {
                this.findByCategory(scanner, database);

            } else if (sub_option == 7) {
                this.findByPrice(scanner, database);
            }
            else if (sub_option == 8) {
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

    private void findByCategory(Scanner scanner, MongoDatabase database) {
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.println("\n");
        Document findCategory = database.getCollection("categories").find(eq("name", category)).first();
        System.out.println("Games in " + category + ":");
        if (findCategory != null) {
        int pageSize = 5;

        long totalDocuments = database.getCollection("games").countDocuments(eq("category.name", category));
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total games: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No games found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
 
                
                database.getCollection("games").
                find(eq("category.name", findCategory.get("name"))).skip(skipDocuments).limit(pageSize)
                        .into(new ArrayList<>())
                        .forEach(document -> System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build())));
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------------------------------------");
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
           
        } else {
            System.out.println("Category not found.");
        }
    }

    private void findByPrice(Scanner scanner, MongoDatabase database) {
        System.out.println("Enter minimum price: ");
        String price_string = scanner.nextLine();
        Double minPrice = Double.parseDouble(price_string);
        System.out.println("Enter maximum price: ");
        String maxPrice_string = scanner.nextLine();
        Double maxPrice = Double.parseDouble(maxPrice_string);
        System.out.println("\n");
        System.out.println("Games between " + minPrice + " and " + maxPrice + ":");
        System.out.println("\n");

  
        int pageSize = 5;

        long totalDocuments = database.getCollection("games").countDocuments(and(eq("price", new Document("$gte", minPrice)),
        eq("price", new Document("$lte", maxPrice))));
      
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total games: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No games found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
 
                
                database.getCollection("games").find(and(eq("price", new Document("$gte", minPrice)),
                eq("price", new Document("$lte", maxPrice))))
                .skip(skipDocuments).limit(pageSize)
                        .forEach(document -> System.out.println(document.toJson(JsonWriterSettings.builder().indent(true).build())));
            
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------------------------------------");
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

        
        if (found_user == null) {
            System.out.println("User not found.");
            return;
        } 

        System.out.print("Enter the game-name or game-id that he wants to purchase: ");
        String gameName_or_gameId = scanner.nextLine();
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

        if (found_game == null) {
            System.out.println("Game not found.");
            return;
        }


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
        System.out.println("Enter bank number: ");

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
  
        if ((int) found_user.get("age") <= (int) found_game.get("age_restriction")) {
            System.out.println("User is not old enough to purchase this game.");
            return;
        }

        Document new_purchase = new Document();

        new_purchase.append("amount", amount)
                .append("currency", currency);
        if (bankName != null && bankNumber != null) {
            new_purchase.append("bank", new Document()
                    .append("name", bankName)
                    .append("number", bankNumber));
        }
        
        ObjectId userId = found_user.getObjectId("_id"); 
        ObjectId gameId = found_game.getObjectId("_id");
        new_purchase.append("user_id", userId);
        new_purchase.append("game_id", gameId);
        new_purchase.append("created_at", new Date());

        InsertOneResult result = database.getCollection("purchases").insertOne(new_purchase);

        if (result.wasAcknowledged()) {
            System.out.println("Transaction created successfully!");
            found_game.put("total", (int) found_game.get("total") + 1);
        } else {
            System.out.println("Transaction not created.");
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



    private void updateOrView(Scanner scanner, MongoDatabase database, Boolean ok) {
        String id_or_name = scanner.nextLine();
        if(!ok){
            Document found_game;
            if(isHexadecimal(id_or_name)){
                found_game = database.getCollection("games").find(eq("_id",
                 new ObjectId(id_or_name))).first();
            }else{
                found_game = database.getCollection("games").find(eq("name",
                 id_or_name)).first();
            }
            if (found_game != null) {
                System.out.println(found_game.toJson(JsonWriterSettings.builder().indent(true).build()));
            } else {
                System.out.println("No game found.");
            }
            return;
        }
       

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
