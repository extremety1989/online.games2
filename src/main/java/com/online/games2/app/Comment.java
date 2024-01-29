package com.online.games2.app;

import java.util.Date;
import java.util.Scanner;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.session.IDocumentSession;

public class Comment {
    public void run(Scanner scanner, DocumentStore store, Reader reader) {
        // Users management
        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Create comment");
            System.out.println("2: Delete comment");
            System.out.println("3: List All comments");
            System.out.println("0: Return to main menu");
            System.out.print("Enter option: ");

            int sub_option = scanner.nextInt();
            scanner.nextLine();
            if (sub_option == 1) {
                try (IDocumentSession session = store.openSession())
                {
                    System.out.print("Enter id or username or email of user: ");
                    String id_or_username_or_email = scanner.nextLine();
                    System.out.print("Enter name or id of game: ");
                    String gameName_or_gameId = scanner.nextLine();
                    System.out.print("Enter comment: ");
                    String comment = scanner.nextLine();
    
                    CommentModel commentModel = new CommentModel();
                    commentModel.setComment(comment);
                    commentModel.setGame_id(gameName_or_gameId);
                    commentModel.setUser_id(id_or_username_or_email);
                    commentModel.setCreated_at(new Date());
                    session.store(commentModel);
                    session.saveChanges();
                }

            } else if (sub_option == 2) {
                try (IDocumentSession session = store.openSession())
                {
                    System.out.print("Enter id of comment to delete: ");
                    String delete = scanner.nextLine();
                    try {
                        session.delete(delete);
                        session.saveChanges();
                    } catch (Exception e) {
                        System.out.println("Comment not found.");
                    }
                }
            } 
            

            else if (sub_option == 3) {
                try (IDocumentSession session = store.openSession()){
                    reader.read(scanner, session, CommentModel.class, "CommentModels");
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
