package com.online.games2.app;

import java.util.Scanner;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.session.IDocumentSession;

public class Purchase {
    public void run(Scanner scanner, DocumentStore store, Reader reader) {

        // Users management
        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Delete purchase");
            System.out.println("2: List All purchases");
            System.out.println("0: Return to main menu");
            System.out.print("Enter option: ");

            int sub_option = scanner.nextInt();
            scanner.nextLine();
            if (sub_option == 1) {
                try (IDocumentSession session = store.openSession()){
                    System.out.print("Enter id of purchase to delete: ");
                    String delete = scanner.nextLine();
                    try {
                        session.delete(delete);
                        session.saveChanges();
                    } catch (Exception e) {
                        System.out.println("purchase not found.");
                    }
                }

            } 
            

            else if (sub_option == 2) {
                try (IDocumentSession session = store.openSession()){
                    reader.read(scanner, session, PurchaseModel.class, "PurchaseModels");
                }
         
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

}
