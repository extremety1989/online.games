package com.online.games.app;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class Transaction {

    private final List<String> bankNames = Arrays.asList(
        "Bank of America",
        "JPMorgan Chase",
        "Wells Fargo",
        "Citigroup",
        "Goldman Sachs",
        "Morgan Stanley",
        "HSBC",
        "Barclays",
        "Royal Bank of Canada",
        "BNP Paribas"
    );

    public void run(MongoCollection<Document> collection, Scanner scanner, MongoDatabase database){
                        // Users management
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create transaction");
                            System.out.println("2: Read transaction");
                            System.out.println("3: Update transaction");
                            System.out.println("4: Delete transaction");
                            System.out.println("5: List All transactions");
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 

                            if (sub_option == 1) {

                                // Create a new transaction
                                System.out.print("Enter userId: ");
                                String userId = scanner.nextLine();
                                System.out.println("Enter bank name from following: ");
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
                                if(bankChoice < 1 || bankChoice > 10){
                                    System.out.println("Invalid choice. Please try again.");
                                    break;
                                }
                                String bankName = bankNames.get(bankChoice - 1);
                                System.out.println("Enter bank number: ");
                                Integer bankNumber = scanner.nextInt();
                                if (bankNumber < 0 || bankNumber > 999999999999L) {
                                    System.out.println("Invalid bank number. Please try again.");
                                    break;
                                }
                                System.out.println("Enter amount: ");
                                Double amount = scanner.nextDouble();
                                if (amount < 0) {
                                    System.out.println("Invalid amount. Please try again.");
                                    break;
                                }
                                String currency = "EUR";
                                
                                scanner.nextLine(); 
                                this.create(database.getCollection("transactions"), userId, bankName, bankNumber, amount, currency);
                            
                            } else if (sub_option == 2) {

                                // Read a user
                                System.out.print("Enter username to find: ");
                                String username = scanner.nextLine();
                                Document founduser = collection.find(eq("surname", username)).first();
                                if (founduser != null) {
                                    System.out.println(founduser.getString("surname") + " " 
                                    + founduser.getString("firstname") + " " + founduser.getInteger("age"));
                                } else {
                                    System.out.println("user not found.");
                                }

                            } else if (sub_option == 3) {

                                // Update a user
                                System.out.print(
                                        "Enter surname or firstname of user to update (or press enter to skip): ");
                                Document updateDoc = new Document();
                                String update = scanner.nextLine();

                                System.out.print("Enter new surname: ");
                                String newSurname = scanner.nextLine();
                                System.out.print("Enter new firstname: ");
                                String newFirstname = scanner.nextLine();
                                System.out.print("Enter new age: ");
                                int newAge = 0;
                                String ageInput = scanner.nextLine();

                                if (!ageInput.isEmpty()) {
                                    newAge = Integer.parseInt(ageInput);
                                }

                                if (!newSurname.isEmpty()) {
                                    updateDoc.append("surname", newSurname);
                                }

                                if (!newFirstname.isEmpty()) {
                                    updateDoc.append("firstname", newFirstname);
                                }

                                if (newAge > 0) {
                                    updateDoc.append("age", newAge);
                                }

                                if (!updateDoc.isEmpty()) {
                                    this.update(collection, update, updateDoc);
                                }
                 
                            } else if (sub_option == 4) {
                                // Delete a user
                                System.out.print("Enter id, username or email of user to delete: ");
                                String delete = scanner.nextLine();
                                this.delete(collection, delete);
                             
                            } else if (sub_option == 5) {
                                System.out.println("\n");
                                int pageSize = 5;
                                long totalDocuments = collection.countDocuments();
                                int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
                                System.out.printf("Total transactions: %d\n", totalDocuments);
                                if (totalPages == 0) {
                                    System.out.println("No transactions found.");
                                }else{
                                    int currentPage = 1; // Start with page 1
                                    boolean paginating = true;
    
                                    while (paginating) {
                                       
                                        System.out.println("\n");
                                        System.out.printf("%-29s %-20s %-20s %-5s\n", "Id", "Surname", "Firstname", "Age");
                                        System.out.println(
                                                "----------------------------------------------------------------------------");
    
                                        int skipDocuments = (currentPage - 1) * pageSize;
                                        this.read(collection, skipDocuments, pageSize);
    
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
                            else if (sub_option == 0) {
                                sub_exit = true;
                                break;
                            }else {
                                System.out.println("Invalid option. Please try again.");
                                break;
                            }
                        }
    }

    private void create(MongoCollection<Document> collection, String userId, String bankName, Integer bankNumber, Double amount, String currency) {
        if (userId.isEmpty() || bankName.isEmpty() || bankNumber == null || amount == null || currency.isEmpty()) {
            System.out.println("Please enter all fields.");
            return;
        }

        Document transaction = new Document()
        .append("userId", userId)
        .append("bankName", bankName)
        .append("bankNumber", bankNumber)
        .append("amount", amount)
        .append("currency", currency);

        collection.insertOne(transaction);
        System.out.println("user created successfully!");
    }

    private void read(MongoCollection<Document> collection, int skipDocuments, int pageSize) {
        FindIterable<Document> page = collection.find()
        .skip(skipDocuments)
        .limit(pageSize);
        for (Document p : page) {
            Object id = p.get("_id");
            System.out.printf("%-30s %-5d\n",
                    id.toString(),
                    p.getString("userId"),
                    p.getString("bankName"),
                    p.getInteger("bankNumber"),
                    p.getDouble("amount"),
                    p.getString("currency"));
        }
    }

    private void delete(MongoCollection<Document> collection, String delete) {
         DeleteResult deleteResult = collection.deleteOne(or(
          eq("userId", delete),
          eq("bankNumber", delete)
          ));
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("transaction deleted successfully!");
        } else {
            System.out.println("No transaction deleted.");
        }
    }

    private void update(MongoCollection<Document> collection, String update, Document updateDoc){
            UpdateResult updateResult = collection.updateOne(
                    or(eq("surname", update), eq("firstname", update)),
                    new Document("$set", updateDoc));

            if (updateResult.getModifiedCount() > 0) {
                System.out.println("transaction updated successfully!");
            } else {
                System.out.println("No transaction found.");
            }
    }
}
