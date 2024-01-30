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
            System.out.println("1: View purchase");
            System.out.println("2: Delete purchase");
            System.out.println("3: List All purchases");

            System.out.println("0: Return to main menu");
            System.out.print("Enter option: ");

            int sub_option = scanner.nextInt();
            scanner.nextLine();
 
            
            if (sub_option == 1) {

                System.out.print("Enter id of purchase to view: ");
                String delete = scanner.nextLine();
                this.deleteOrView(database, delete, false);

            } 

            else if (sub_option == 2){
                System.out.print("Enter id of purchase to delete: ");
                String delete = scanner.nextLine();
                this.deleteOrView(database, delete, true);
            }

            else if (sub_option == 3) {
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



    private void deleteOrView(MongoDatabase database, String delete, boolean ok) {
        ObjectId purchaseId = new ObjectId(delete);
     
        if(!ok){
            Document found = database.getCollection("purchases").find(
             
       
                        eq("_id", purchaseId))
                
                .first();
                if (found != null){
                    System.out.println(found.toJson(JsonWriterSettings.builder().indent(true).build()));
                    return;
                }
                return;
        }
        DeleteResult deleteResult = database.getCollection("purchases").deleteOne(
            eq("_id", purchaseId));
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("purchase deleted successfully!");
            Bson update = Updates.pull("comments", purchaseId);
            database.getCollection("users").updateMany(eq("purchases", purchaseId), update);
        } else {
            System.out.println("No purchase deleted.");
        }
    }

}
