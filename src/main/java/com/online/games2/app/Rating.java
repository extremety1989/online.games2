package com.online.games2.app;

import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.session.IDocumentSession;

public class Rating {
        public void run(Scanner scanner, DocumentStore store, Reader reader){
            boolean sub_exit = false;

            while (!sub_exit) {

                System.out.println("\n");
                System.out.println("Choose an operation:");
                System.out.println("1: Create rating");
                System.out.println("2: View rating");
                System.out.println("3: Update rating");
                System.out.println("4: Delete rating");
                System.out.println("5: List All ratings");
                System.out.println("0: Return to main menu");
                System.out.print("Enter option: ");

                int sub_option = scanner.nextInt();
                scanner.nextLine(); 
                if (sub_option == 1) {
                    try (IDocumentSession session = store.openSession()){
                        System.out.print("Enter user-id or username or email: ");
                        String id_or_username_or_email = scanner.nextLine();
                        System.out.print("Enter the game-name or game-id that he wants to rate: ");
                        String gameName_or_gameId = scanner.nextLine();
                        System.out.print("Enter rating: (1-5)");
                        Integer rating = scanner.nextInt();
                        if (rating < 1 || rating > 5) {
                            System.out.println("Invalid rating. Please try again.");
                            break;
                        }
                        UserModel find_user = session.query(UserModel.class).whereEquals("id or username or email", 
                        id_or_username_or_email).firstOrDefault();
                        if (find_user == null) {
                            System.out.println("User not found. Please try again.");
                            break;
                        }
                        GameModel find_game = session.query(GameModel.class).whereEquals("name or id",
                         gameName_or_gameId).firstOrDefault();
                        if (find_game == null) {
                            System.out.println("Game not found. Please try again.");
                            break;
                        }
                        RatingModel ratingModel = new RatingModel();
                        ratingModel.setGame_id(find_game.getId());
                        ratingModel.setUser_id(find_user.getId());
                        ratingModel.setRating(rating);
                        session.store(ratingModel);
                        session.saveChanges();
                    }

                }
                else if (sub_option == 2) {
                    try (IDocumentSession session = store.openSession()){
                        System.out.print("Enter id of rating to view: ");
                        String id = scanner.nextLine();
                        try {
                            RatingModel rating = session.load(RatingModel.class, "RatingModels/" + id);
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            String json = gson.toJson(rating);
                            System.out.println(json);
                        } catch (Exception e) {
                            System.out.println("Rating not found.");
                        }
                    }
                } 
                else if (sub_option == 3) {
                    try (IDocumentSession session = store.openSession()){
                        System.out.print("Enter id of rating to update: ");
                        String id = scanner.nextLine();
                        try {
                            RatingModel rating = session.load(RatingModel.class, "RatingModels/" + id);
                            System.out.print("Enter new rating: ");
                            Integer newrating = scanner.nextInt();
                            if (newrating < 1 || newrating > 5) {
                                System.out.println("Invalid rating. Please try again.");
                                break;
                            }
                            rating.setRating(newrating);
                            session.saveChanges();
                        } catch (Exception e) {
                            System.out.println("Rating not found.");
                        }
                    }
                } 
                else if (sub_option == 4) {
                    try (IDocumentSession session = store.openSession()){
                        System.out.print("Enter id of rating to delete: ");
                        String delete = scanner.nextLine();
                        session.delete("RatingModels/" + delete);
                        session.saveChanges();
                    }
                } 
                else if (sub_option == 5) {
                    try (IDocumentSession session = store.openSession()){
                        reader.read(scanner, session, RatingModel.class, "RatingModels");
                    }
             
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
}
