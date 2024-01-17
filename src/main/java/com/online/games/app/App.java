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
import com.mongodb.client.model.Collation;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.ArrayList;
import java.util.Scanner;

public class App {



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
                User user = new User();
                Game game = new Game();
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
                        game.run(collection, scanner, database);
                    } else if (option == 3) {
                        user.run(collection, scanner, database);
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