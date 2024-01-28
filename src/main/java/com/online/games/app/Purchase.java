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

public class Purchase {

    private static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("\\p{XDigit}+");

    private static boolean isHexadecimal(String input) {
        final Matcher matcher = HEXADECIMAL_PATTERN.matcher(input);
        return matcher.matches();
    }

    public void run(Scanner scanner, MongoDatabase database) {
        // Users management
        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Create purchase");
            System.out.println("2: Delete purchase");
            System.out.println("3: Delete All purchases by user");
            System.out.println("4: List All purchases");

            System.out.println("5: List All purchases by date");
            System.out.println("6: List All purchases by user or game");

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
            } else if (sub_option == 2) {

                System.out.print("Enter id of purchase to delete: ");
                String delete = scanner.nextLine();
                this.delete(database, delete);

            } else if (sub_option == 3) {
                System.out.print("Enter id, username or email of user to delete all purchases: ");
                String delete = scanner.nextLine();
                this.deleteByUserIdOrUsernameOrEmail(database, delete);
            }

            else if (sub_option == 4) {
                this.read(scanner, database);
            } else if (sub_option == 5) {
                this.readByDate(scanner, database);
            } else if (sub_option == 6) {
                this.readByUserOrGame(scanner, database);
            } else if (sub_option == 0) {
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
                        eq("_id", id_or_username_or_email),
                        eq("username", id_or_username_or_email),
                        eq("email", id_or_username_or_email)))
                .first();

        if (found_user != null) {

            Document found_game = database.getCollection("games").find(
                    or(
                            eq("_id", gameName_or_gameId),
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

                    new_purchase.append("created_at", new Date())
                            .append("user_id", found_user.get("_id"))
                            .append("game_id", found_game.get("_id"));

                    InsertOneResult result = database.getCollection("purchases").insertOne(new_purchase);

                    if (result.wasAcknowledged()) {
                        System.out.println("Transaction created successfully!");
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

    private void read(Scanner scanner, MongoDatabase database) {
        System.out.println("\n");
        int pageSize = 5;
        long totalDocuments = database.getCollection("purchases").countDocuments();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total purchases: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No purchases found.");
        } else {
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {

                System.out.println("\n");
                System.out.printf("%-30s %-30s %-5s %-3s %-6s\n" + //
                        "", "User Id", "Bank Name", "Bank Number", "Currency", "created_at");
                System.out.println(
                        "----------------------------------------------------------------------------------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                FindIterable<Document> page = database.getCollection("purchases").find()
                .skip(skipDocuments)
                .limit(pageSize);
        for (Document p : page) {
            Document temp = p.get("bank", Document.class);
            System.out.print(p.get("user_id") + " ");
            System.out.print(temp.get("name") + " ");
            System.out.print(temp.get("number") + " ");
            System.out.print(p.get("currency") + " ");
            System.out.print(p.get("created_at")    + " ");
            System.out.println();
            }

                // Pagination controls
                System.out.println(
                        "----------------------------------------------------------------------------------------------------------------------------------------------------");
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

    private void readByUserOrGame(Scanner scanner, MongoDatabase database) {
        System.out.println("\n");
        System.out.print("Enter user-id or username or email or game-name of purchases to search: ");
        String id_or_username_or_email_or_gamename = scanner.nextLine();
        Document found = null;
        if(isHexadecimal(id_or_username_or_email_or_gamename)){
            found = database.getCollection("users").find(eq("_id", new ObjectId(id_or_username_or_email_or_gamename))).first();
        } else{
            found = database.getCollection("users").find(or(
                                        eq("username", id_or_username_or_email_or_gamename),
                                        eq("email", id_or_username_or_email_or_gamename))
                           ).first();
        }
        long totalDocuments;
        if(found == null){
            found = database.getCollection("games").find(eq("name", id_or_username_or_email_or_gamename)).first();
            totalDocuments = database
            .getCollection("purchases")
            .countDocuments(
    

                        eq("game_id", found.get("_id"))
                        
            );
        }else{
            totalDocuments = database
            .getCollection("purchases")
            .countDocuments(

                        eq("user_id", found.get("_id"))
         
                        
            );
        }
        int pageSize = 5;


      
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total purchases: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No purchases found.");
        } else {
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {

                System.out.println("\n");
                System.out.printf("%-29s %-40s %-5s %-3s %-6s\n", "Id", "Bank Name", "Bank Number", "Amount",
                        "Currency", "Date");
                System.out.println(
                        "----------------------------------------------------------------------------------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;

                Document foundUser; 
                FindIterable<Document> page;
                if (isHexadecimal(id_or_username_or_email_or_gamename)){
                    foundUser = database.getCollection("users").find(
                    
                                eq("_id", new ObjectId(id_or_username_or_email_or_gamename))
                               )
                        .first();
                } else{
                    foundUser = database.getCollection("users").find(
                        or(
                                eq("username", id_or_username_or_email_or_gamename),
                                eq("email", id_or_username_or_email_or_gamename)))
                        .first();
                }
              

                Document foundGame = null;
                if (foundUser == null) {
                    foundGame = database.getCollection("games").find(
                            eq("name", id_or_username_or_email_or_gamename)).first();
                    if (foundGame == null) {
                        System.out.println("User or game not found.");
                        return;
                    }
                    FindIterable<Document> dd = database.getCollection("purchases")
                    .find(
                        
             
                                    eq("game_id", foundGame.get("_id")));
           
                    page = dd.skip(skipDocuments).limit(pageSize);
                    
                }else{
                    FindIterable<Document> dd = database.getCollection("purchases")
                    .find(
                  
                                    eq("user_id", foundUser.get("_id")));
                        
                    page = dd.skip(skipDocuments).limit(pageSize);
                }

         

                for (Document p : page) {
                    Object id = p.get("_id");
                    Document temp = p.get("bank", Document.class);
                    System.out.printf("%-29s %-20s %-5s %-3s %-6s\n",
                            id.toString(),
                            temp.get("name"),
                            temp.get("number"),
                            p.getDouble("amount"),
                            p.getString("currency"),
                            p.getDate("created_at"));
                }
                
                // Pagination controls
                System.out.println(
                        "----------------------------------------------------------------------------------------------------------------------------------------------------");
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

    private void readByDate(Scanner scanner, MongoDatabase database) {
        System.out.println("\n");
        System.out.print("Enter date of purchase to search: ");
        String date = scanner.nextLine();

        int pageSize = 5;
        long totalDocuments = database
                .getCollection("purchases")
                .countDocuments(eq("created_at", date));
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total purchases: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No purchases found.");
        } else {
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {

                System.out.println("\n");
                System.out.printf("%-29s %-29s %-29s %-40s %-9i %-5s %-3s %-6s\n", "Id", "User Id", "Game Id",
                        "Bank Name", "Bank Number", "Amount", "Currency", "Date");
                System.out.println(
                        "----------------------------------------------------------------------------------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                FindIterable<Document> page = database.getCollection("purchases").find(eq("created_at", date))
                        .skip(skipDocuments)
                        .limit(pageSize);
                for (Document p : page) {
                    Object id = p.get("_id");
                    Document temp = p.get("bank", Document.class);
                    System.out.printf("%-29s %-29s %-29s %-40s %-9i %-5s %-3s %-6s\n",
                            id.toString(),
                            p.getString("user_id"),
                            p.getString("game_id"),
                            temp.get("name"),
                            temp.get("number"),
                            p.getDouble("amount"),
                            p.getString("currency"),
                            p.getDate("created_at"));
                }

                // Pagination controls
                System.out.println(
                        "----------------------------------------------------------------------------------------------------------------------------------------------------");
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
        DeleteResult deleteResult = database.getCollection("purchases").deleteOne(eq("_id", delete));
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("purchase deleted successfully!");
        } else {
            System.out.println("No purchase deleted.");
        }
    }

    private void deleteByUserIdOrUsernameOrEmail(MongoDatabase database, String delete) {
        DeleteResult deleteResult;
        if (isHexadecimal(delete)){
            deleteResult = database.getCollection("purchases").deleteMany(eq("user_id", delete));
        }else{
            deleteResult = database.getCollection("purchases").deleteMany(
                    or(
                            eq("username", delete),
                            eq("email", delete)));
        }

        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("purchases deleted successfully!");
        } else {
            System.out.println("No purchases deleted.");
        }
    }
}
