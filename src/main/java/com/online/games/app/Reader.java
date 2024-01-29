package com.online.games.app;

import java.util.ArrayList;
import java.util.Scanner;

import org.bson.json.JsonWriterSettings;

import com.mongodb.client.MongoDatabase;

public class Reader {
        public void read(Scanner scanner, MongoDatabase database, String what) {
        
        System.out.println("\n");
        int pageSize = 5;

        long totalDocuments = database.getCollection(what).countDocuments();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total "+what+": %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No "+what+" found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
 
                
                database.getCollection(what).find().skip(skipDocuments).limit(pageSize)
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
    }
}
