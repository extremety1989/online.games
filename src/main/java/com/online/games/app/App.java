package com.online.games.app;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import com.mongodb.client.MongoDatabase;

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
                Reader reader = new Reader();
                User user = new User();
                Game game = new Game();
                Category category = new Category();
                Purchase purchase = new Purchase();
                Comment comment = new Comment();
                Rating rating = new Rating();
                PopulateData populate = new PopulateData();

                boolean exit = false;
                while (!exit) {
                    System.out.println("\n");
                    System.out.println("Management system:");
                    System.out.println("1: Category management");
                    System.out.println("2: Games management");
                    System.out.println("3: Users management");
                    System.out.println("4: Comments management");
                    System.out.println("5: Ratings management");
                    System.out.println("6: Purchases management");
                    System.out.println("7: Bulk mongodb");
                    System.out.println("0: Exit");
                    System.out.print("Enter option: ");
                    int option = scanner.nextInt();
                    scanner.nextLine(); 
                    if (option == 1) {
                       
                        category.run(scanner, database, reader);
                    } else if (option == 2) {
                  
                        game.run(scanner, database, reader);
                    } else if (option == 3) {
                    
                        user.run(scanner, database, reader);
                    }

                    else if (option == 4) {
                      comment.run(scanner, database, reader);
                    } else if (option == 5) {
                       rating.run(scanner, database, reader);
                    } else if (option == 6) {
                       purchase.run(scanner, database, reader);
                    } else if (option == 7) {
                        populate.createMock(database);
                    }
                    
                    else if (option == 0) {
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