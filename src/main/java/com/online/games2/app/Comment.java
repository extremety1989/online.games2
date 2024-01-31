package com.online.games2.app;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.session.IDocumentQuery;
import net.ravendb.client.documents.session.IDocumentSession;

public class Comment {
    public void run(Scanner scanner, DocumentStore store, Reader reader) {
        // Users management
        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Create comment");
            System.out.println("2: View comment");
            System.out.println("3: Update comment");
            System.out.println("4: Delete comment");
            System.out.println("5: List All comments");
            System.out.println("0: Return to main menu");
            System.out.print("Enter option: ");

            int sub_option = scanner.nextInt();
            scanner.nextLine();
            if (sub_option == 1) {
                try (IDocumentSession session = store.openSession())
                {
                    System.out.print("Enter id or username or email of user: ");
                    String user = scanner.nextLine();

                    UserModel userModel = session.query(UserModel.class)
                    .whereEquals("id", user)
                    .orElse()
                    .whereEquals("username", user)
                    .orElse()
                    .whereEquals("email", user)
                    .firstOrDefault();
                    if (userModel == null) {
                        System.out.println("User not found.");
                        break;
                    }
                    System.out.print("Enter id or name of game: ");
                    String game = scanner.nextLine();
                    GameModel gameModel = session.query(GameModel.class)
                    .whereEquals("id", game)
                    .orElse()
                    .whereEquals("name", game)
                    .firstOrDefault();

                    if(gameModel == null) {
                        System.out.println("Game not found.");
                        break;
                    }
                    System.out.print("Enter comment: ");
                    String comment = scanner.nextLine();
    
                    CommentModel commentModel = new CommentModel();
                    commentModel.setText(comment);
                    commentModel.setUser_id(userModel.getId());
                    commentModel.setGame_id(gameModel.getId());
                    commentModel.setCreated_at(new Date());
                    session.store(commentModel);
                    session.saveChanges();
                }

            }
            else if (sub_option == 2) {
                try (IDocumentSession session = store.openSession())
                {
                    System.out.print("Enter id of comment to view: ");
                    String id = scanner.nextLine();
                    try {
                        CommentModel comment = session.load(CommentModel.class, "CommentModels/" + id);
                        
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(comment);
                    System.out.println(json);
               
                    } catch (Exception e) {
                        System.out.println("Comment not found.");
                    }
                }
            } 
            else if (sub_option == 3) {
                try (IDocumentSession session = store.openSession())
                {
                    System.out.print("Enter id of comment to update: ");
                    String id = scanner.nextLine();
                    try {
                        CommentModel comment = session.load(CommentModel.class, "CommentModels/" + id);
                        System.out.print("Enter new comment: ");
                        String newcomment = scanner.nextLine();
                        comment.setText(newcomment);
                        session.saveChanges();
                    } catch (Exception e) {
                        System.out.println("Comment not found.");
                    }
                }
            } 
            else if (sub_option == 4) {
                try (IDocumentSession session = store.openSession())
                {
                    System.out.print("Enter id of comment to delete: ");
                    String delete = scanner.nextLine();
                    try {
                        session.delete("CommentModels/" + delete);
                        session.saveChanges();

                    } catch (Exception e) {
                        System.out.println("Comment not found.");
                    }
                }
            } 
            

            else if (sub_option == 5) {
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
