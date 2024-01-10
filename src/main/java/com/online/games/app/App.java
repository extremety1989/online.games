package com.online.games.app;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.Scanner;

public class App {

    public static void createUser(collection, surname, firstname, age){
        Document newuser = new Document()
        .append("surname", surname)
        .append("firstname", firstname)
        .append("age", age);
        collection.insertOne(newuser);
        System.out.println("user created successfully!");
    }

    public static void main(String[] args) {
        // String connectionString =
        // "mongodb+srv://andranik020489:r9D0SAvIJ51G4yq5@master1.jrj5jud.mongodb.net/?retryWrites=true&w=majority";
                // ServerApi serverApi = ServerApi.builder()
        // .version(ServerApiVersion.V1)
        // .build();

        String connectionString = "mongodb://localhost:27018";


        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                // .serverApi(serverApi)
                .build();

        try (MongoClient mongoClient = MongoClients.create(settings)) {

            try {

                Scanner scanner = new Scanner(System.in);
                MongoDatabase database = mongoClient.getDatabase("online_games");
                MongoCollection<Document> collection = database.getCollection("users");

                boolean exit = false;
                while (!exit) {
                    System.out.println("\n");
                    System.out.println("Management system:");
                    System.out.println("1: Category management");
                    System.out.println("2: Games management");
                    System.out.println("3: Users management");
                    System.out.println("4: Comments management");
                    System.out.println("5: Ratings management");
                    System.out.println("6: Transactions management");
                    System.out.println("0: Exit");
                    System.out.print("Enter option: ");
                    int option = scanner.nextInt();
                    scanner.nextLine(); 
                    if (option == 1) {
                        // Category management

                        boolean sub_exit = false;

                        while (!sub_exit) {
                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create category");
                            System.out.println("2: Read category");
                            System.out.println("3: Update category");
                            System.out.println("4: Delete category");
                            System.out.println("5: List All categories");
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 
                            if (sub_option == 1){

                            }
                            else if (sub_option == 2) {

                            }
                            else if (sub_option == 3) {

                            }
                            else if (sub_option == 4) {

                            }
                            else if (sub_option == 5) {

                            }
                            else if (sub_option == 5) {
                                
                            }
                            else if (sub_option == 0) {
                                sub_exit = true;
                                break;
                            }
                      }
                    } else if (option == 2) {
                        // Games management
                        boolean sub_exit = false;

                        while (!sub_exit) {
                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create game");
                            System.out.println("2: Read game");
                            System.out.println("3: Update game");
                            System.out.println("4: Delete game");
                            System.out.println("5: List All games");
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 
                            if (sub_option == 1){

                            }
                            else if (sub_option == 2) {

                            }
                            else if (sub_option == 3) {

                            }
                            else if (sub_option == 4) {

                            }
                            else if (sub_option == 5) {

                            }
                            else if (sub_option == 5) {
                                
                            }
                            else if (sub_option == 0) {
                                sub_exit = true;
                                break;
                            }
                      }
           
                    } else if (option == 3) {
                        // Users management
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create user");
                            System.out.println("2: Read user");
                            System.out.println("3: Update user");
                            System.out.println("4: Delete user");
                            System.out.println("5: List All users");
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 

                            if (sub_option == 1) {

                                // Create a new user
                                System.out.print("Enter surname: ");
                                String surname = scanner.nextLine();
                                System.out.print("Enter firstname: ");
                                String firstname = scanner.nextLine();
                                System.out.print("Enter age: ");
                                int age = scanner.nextInt();
                                scanner.nextLine(); 
                                createUser(database.getCollection("users"), surname, firstname, age);
                                break;

                            } else if (sub_option == 2) {

                                // Read a user
                                System.out.print("Enter surname to find: ");
                                String searchSurname = scanner.nextLine();
                                Document founduser = collection.find(eq("surname", searchSurname)).first();
                                if (founduser != null) {
                                    System.out.println(founduser.toJson());
                                } else {
                                    System.out.println("user not found.");
                                }
                                break;

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
                                    UpdateResult updateResult = collection.updateOne(
                                            or(eq("surname", update), eq("firstname", update)),
                                            new Document("$set", updateDoc));

                                    if (updateResult.getModifiedCount() > 0) {
                                        System.out.println("user updated successfully!");
                                    } else {
                                        System.out.println("No user found.");
                                    }
                                    break;
                                }
                                break;
                            } else if (sub_option == 4) {
                                // Delete a user
                                System.out.print("Enter id, surname or fistname of user to delete: ");
                                String delete = scanner.nextLine();

                                DeleteResult deleteResult = collection.deleteOne(or(
                                        eq("surname", delete),
                                        eq("firstname", delete),
                                        eq("_id", delete)));
                                if (deleteResult.getDeletedCount() > 0) {
                                    System.out.println("user deleted successfully!");
                                } else {
                                    System.out.println("No user deleted.");
                                }
                                break;
                            } else if (sub_option == 5) {
                                System.out.println("Listing all users:");
                                int pageSize = 5;
                                long totalDocuments = collection.countDocuments();
                                System.out.println("Total documents: " + totalDocuments);
                                int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
                                System.out.printf("Total users: %d\n", totalDocuments);
                                if (totalPages == 0) {
                                    System.out.println("No users found.");
                                    break;
                                }
                                int currentPage = 1; // Start with page 1
                                boolean paginating = true;

                                while (paginating) {
                                    System.out.printf("Page %d of %d\n", currentPage, totalPages);
                                    System.out.printf("%-29s %-20s %-20s %-5s\n", "Id", "Surname", "Firstname", "Age");
                                    System.out.println(
                                            "----------------------------------------------------------------------------");

                                    int skipDocuments = (currentPage - 1) * pageSize;
                                    FindIterable<Document> pageusers = collection.find()
                                            .skip(skipDocuments)
                                            .limit(pageSize);
                                    for (Document user : pageusers) {
                                        Object id = user.get("_id");
                                        System.out.printf("%-29s %-20s %-20s %-5d\n",
                                                id.toString(),
                                                user.getString("surname"),
                                                user.getString("firstname"),
                                                user.getInteger("age"));
                                    }

                                    // Pagination controls
                                    System.out.println(
                                            "----------------------------------------------------------------------------");
                                    System.out.printf("n: Next page | p: Previous page | q: Quit pagination\n");
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
                                break;
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

                    else if (option == 4) {
                        boolean sub_exit = false;

                        while (!sub_exit) {
                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create comment");
                            System.out.println("2: Read comment");
                            System.out.println("3: Update comment");
                            System.out.println("4: Delete comment");
                            System.out.println("5: List All comments");
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 
                            if (sub_option == 1){

                            }
                            else if (sub_option == 2) {

                            }
                            else if (sub_option == 3) {

                            }
                            else if (sub_option == 4) {

                            }
                            else if (sub_option == 5) {

                            }
                            else if (sub_option == 5) {
                                
                            }
                            else if (sub_option == 0) {
                                sub_exit = true;
                                break;
                            }
                      }
                    } else if (option == 5) {
                        boolean sub_exit = false;

                        while (!sub_exit) {
                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create rating");
                            System.out.println("2: Read rating");
                            System.out.println("3: Update rating");
                            System.out.println("4: Delete rating");
                            System.out.println("5: List All ratings");
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 
                            if (sub_option == 1){

                            }
                            else if (sub_option == 2) {

                            }
                            else if (sub_option == 3) {

                            }
                            else if (sub_option == 4) {

                            }
                            else if (sub_option == 5) {

                            }
                            else if (sub_option == 5) {
                                
                            }
                            else if (sub_option == 0) {
                                sub_exit = true;
                                break;
                            }
                      }
                    } else if (option == 6) {
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
                            if (sub_option == 1){

                            }
                            else if (sub_option == 2) {

                            }
                            else if (sub_option == 3) {

                            }
                            else if (sub_option == 4) {

                            }
                            else if (sub_option == 5) {

                            }
                            else if (sub_option == 5) {
                                
                            }
                            else if (sub_option == 0) {
                                sub_exit = true;
                                break;
                            }
                      }
                    } else if (option == 0) {
                        exit = true;
                        System.out.println("Exiting...");
                        break;
                    }

                }
                scanner.close();
            } catch (MongoException e) {
                System.out.println("An error occurred.");
                System.out.println(e);
            }
        }
    }
}