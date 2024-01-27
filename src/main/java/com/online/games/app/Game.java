package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.print.Doc;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.internal.operation.FindAndUpdateOperation;

public class Game {

    public void run(Scanner scanner, MongoDatabase database) {

        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Create game");
            System.out.println("2: Read games");
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
                Double price = scanner.nextDouble();

                System.out.print("Enter age limit: ");
                Integer age_limit = scanner.nextInt();
                scanner.nextLine();
                this.create(database, name, category, age_limit, price);

            } else if (sub_option == 2) {

                System.out.print("Enter the beginning of the game-name to find: ");

                this.findByName(scanner, database);

            } else if (sub_option == 3) {

                System.out.print(
                        "Enter game-id or game-name to update (or press enter to skip): ");

                this.update(scanner, database);
            } else if (sub_option == 4) {
                // Delete a game
                System.out.print("Enter id or name of game to delete: ");
                String delete = scanner.nextLine();
                this.delete(database, delete);

            } else if (sub_option == 5) {
                this.read(scanner, database);

            } else if (sub_option == 6) {
                this.findByCategory(scanner, database);
            } else if (sub_option == 7) {
                this.findByPrice(scanner, database);
            } else if (sub_option == 8) {
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

        System.out.println("Enter bank (0 to skip): ");
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

        int bankChoice = scanner.nextInt();
        if (bankChoice > 10) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }
        if (bankChoice != 0) {

        }
        String bankName = bankNames.get(bankChoice - 1);
        System.out.println("Enter bank number (enter to skip): ");
        Integer bankNumber = scanner.nextInt();
        if (bankNumber != null && (bankNumber < 0 || bankNumber > 999999999999L)) {
            System.out.println("Invalid bank number. Please try again.");
            return;
        }
        System.out.println("Enter amount: ");
        Double amount = scanner.nextDouble();
        if (amount < 0) {
            System.out.println("Invalid amount. Please try again.");
            return;
        }
        System.out.println("Enter a currency US or EUR: ");
        String currency = scanner.nextLine();
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
                        new_purchase.append("bank", new Document()
                                .append("name", bankName)
                                .append("number", bankNumber));
                    }

                    new_purchase.append("date", new Date())
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
        long totalDocuments = database.getCollection("games").countDocuments();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total games: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No game found.");
        } else {
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {

                System.out.println("\n");
                System.out.printf("%-29s %-30s %-5d %-20s %-2i %-9i\n", "Id", "Name", "Price", "Category",
                        "Age Restriction", "Total");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                FindIterable<Document> pagegames = database.getCollection("games").find()
                        .skip(skipDocuments)
                        .limit(pageSize);

                for (Document game : pagegames) {
                    System.out.printf("%-29s %-30s %-5d %-20s %-2i %-9i\n",
                            game.getString("_id"),
                            game.getString("name"),
                            game.getString("price"),
                            game.getString("category"),
                            game.getInteger("age_restriction"),
                            game.getInteger("total"));
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

    private void findByName(Scanner scanner, MongoDatabase database) {
        String name = scanner.nextLine();
        System.out.println("\n");
        int pageSize = 5;
        FindIterable<Document> games = database.getCollection("games").find(text(name));
        long totalDocuments = games.into(new ArrayList<>()).size();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total games: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No game found.");
        } else {
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {

                System.out.println("\n");
                System.out.printf("%-29s %-30s %-5d %-20s %-2i %-9i\n", "Id", "Name", "Price", "Category",
                        "Age Restriction", "Total");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                FindIterable<Document> pagegames = database.getCollection("games").find(text(name))
                        .skip(skipDocuments)
                        .limit(pageSize);

                for (Document game : pagegames) {
                    System.out.printf("%-29s %-30s %-5d %-20s %-2i %-9i\n",
                            game.getString("_id"),
                            game.getString("name"),
                            game.getString("price"),
                            game.getString("category"),
                            game.getInteger("age_restriction"),
                            game.getInteger("total"));
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

    private void findByCategory(Scanner scanner, MongoDatabase database) {
        String category = scanner.nextLine();
        System.out.println("\n");
        int pageSize = 5;
        FindIterable<Document> games = database.getCollection("games").find(text(category));
        long totalDocuments = games.into(new ArrayList<>()).size();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total games: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No game found.");
        } else {
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {

                System.out.println("\n");
                System.out.printf("%-29s %-30s %-5d %-20s %-2i %-9i\n", "Id", "Name", "Price", "Category",
                        "Age Restriction", "Total");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                FindIterable<Document> pagegames = database.getCollection("games").find(text(category))
                        .skip(skipDocuments)
                        .limit(pageSize);

                for (Document game : pagegames) {
                    System.out.printf("%-29s %-30s %-5d %-20s %-2i %-9i\n",
                            game.getString("_id"),
                            game.getString("name"),
                            game.getString("price"),
                            game.getString("category"),
                            game.getInteger("age_restriction"),
                            game.getInteger("total"));
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

    private void findByPrice(Scanner scanner, MongoDatabase database) {
        String price = scanner.nextLine();
        System.out.println("\n");
        int pageSize = 5;
        FindIterable<Document> games = database.getCollection("games").find(text(price));
        long totalDocuments = games.into(new ArrayList<>()).size();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total games: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No game found.");
        } else {
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {

                System.out.println("\n");
                System.out.printf("%-29s %-30s %-5d %-20s %-2i %-9i\n", "Id", "Name", "Price", "Category",
                        "Age Restriction", "Total");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                FindIterable<Document> pagegames = database.getCollection("games").find(text(price))
                        .skip(skipDocuments)
                        .limit(pageSize);

                for (Document game : pagegames) {
                    System.out.printf("%-29s %-30s %-5d %-20s %-2i %-9i\n",
                            game.getString("_id"),
                            game.getString("name"),
                            game.getString("price"),
                            game.getString("category"),
                            game.getInteger("age_restriction"),
                            game.getInteger("total"));
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
        DeleteResult deleteResult = database.getCollection("games").deleteOne(or(
                eq("name", delete),
                eq("_id", delete)));
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

        Double newPrice = 0.0;
        if (!priceInput.isEmpty()) {
            newPrice = Double.parseDouble(priceInput);
        }
        if (!newName.isEmpty()) {
            updateDoc.append("name", newName);
        }
        if (newPrice != 0.0) {
            updateDoc.append("price", newPrice);
        }

        if (!age_restrictionInput.isEmpty()) {
            updateDoc.append("age_restriction", age_restrictionInput);
        }
        if (!totalInput.isEmpty()) {
            updateDoc.append("total", totalInput);
        }
        if (!updateDoc.isEmpty()) {

            if (!categoryInput.isEmpty()) {
                Document findCategory = database.getCollection("categories").find(eq("name", categoryInput)).first();
                if (findCategory != null) {
                    updateDoc.append("category", findCategory);
                }
            }

            Document findAndUpdateResult = database.getCollection("games").findOneAndUpdate(or(
                    eq("name", id_or_name),
                    eq("_id", id_or_name)), new Document("$set", updateDoc));
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
