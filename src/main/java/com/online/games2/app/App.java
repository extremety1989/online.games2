package com.online.games2.app;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.IDocumentStore;

import net.ravendb.client.documents.operations.GetStatisticsOperation;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.client.exceptions.ConcurrencyException;
import net.ravendb.client.exceptions.database.DatabaseDoesNotExistException;

import net.ravendb.client.serverwide.DatabaseRecord;
import net.ravendb.client.serverwide.operations.CreateDatabaseOperation;

import java.util.Scanner;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class App {

    public static void ensureDatabaseExists(IDocumentStore store, String database, boolean createDatabaseIfNotExists) {
    database = ObjectUtils.firstNonNull(database, store.getDatabase());

    if (StringUtils.isBlank(database)) {
        throw new IllegalArgumentException("Value cannot be null or whitespace");
    }

    try {
        store.maintenance().forDatabase(database).send(new GetStatisticsOperation());
    } catch (DatabaseDoesNotExistException e) {
        if (!createDatabaseIfNotExists) {
            throw e;
        }

        try {
            DatabaseRecord databaseRecord = new DatabaseRecord();
            databaseRecord.setDatabaseName(database);
            store.maintenance().server().send(new CreateDatabaseOperation(databaseRecord));
        } catch (ConcurrencyException ce) {
            // The database was already created before calling CreateDatabaseOperation
        }
    }
}

    public static void main(String[] args) {
        try (DocumentStore store = new DocumentStore()) {
            
            store.setUrls(new String[]{ "http://localhost:8080" });

            store.setDatabase("OnlineGames");
            store.initialize();
            
          


            // This process establishes the connection with the Server
            // and downloads various configurations
            // e.g. cluster topology or client configuration
 
            
            Scanner scanner = new Scanner(System.in);
            Category category = new Category();
            Game game = new Game();
            User user = new User();
            Comment comment = new Comment();
          
            Rating rating = new Rating();
            Purchase purchase = new Purchase();
            PopulateData populate = new PopulateData();

            Reader reader = new Reader();
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
                System.out.println("7: Populate ravendb");
                System.out.println("0: Exit");
                System.out.print("Enter option: ");
                int option = scanner.nextInt();
                scanner.nextLine(); 
                if (option == 1) {
                    category.run(scanner, store, reader);
                } else if (option == 2) {
                    game.run(scanner, store, reader);
                } else if (option == 3) {
                
                    user.run(scanner, store, reader);
                }

                else if (option == 4) {
                  comment.run(scanner, store, reader);
                } else if (option == 5) {
                   rating.run(scanner, store, reader);
                } else if (option == 6) {
                   purchase.run(scanner, store, reader);
                } else if (option == 7) {

                    populate.createMock(store);
                }
                
                else if (option == 0) {
                    exit = true;
                    System.out.println("Exiting...");
                    break;
                }

            }
            scanner.close();
        }
    }
}
