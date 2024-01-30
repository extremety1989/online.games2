package com.online.games2.app;

import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.client.documents.session.IRawDocumentQuery;

public class Reader {

        public <T> void read(Scanner scanner, IDocumentSession session, Class<T> modelClass, String modelClassString) {
        {
            System.out.println("\n");
            int pageSize = 5;
            IRawDocumentQuery<T> results = session.advanced().rawQuery(modelClass, "from "+modelClassString)
            .waitForNonStaleResults();
            long totalDocuments = results
                    .toList()
                    .size();
            session.saveChanges();
            int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
            System.out.printf("Total "+modelClassString+": %d\n", totalDocuments);
            if (totalPages == 0) {
                System.out.println("No "+modelClassString+" found.");
            } else {
                int currentPage = 1; // Start with page 1
                boolean paginating = true;

                while (paginating) {

                    System.out.println("\n");
                    System.out.println(
                            "----------------------------------------------------------------------------");

                    int skipDocuments = (currentPage - 1) * pageSize;
                    session.advanced().rawQuery(modelClass, "from "+modelClassString)
                            .waitForNonStaleResults()
                            .skip(skipDocuments)
                            .take(pageSize)
                            .toList()
                            .forEach(x -> {
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                String json = gson.toJson(x);
                                System.out.println(json);
                            });
                    
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
    }
}
