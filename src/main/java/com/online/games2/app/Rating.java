package com.online.games2.app;

import java.util.Scanner;

import net.ravendb.client.documents.session.IDocumentSession;

public class Rating {
        public void run(Scanner scanner, IDocumentSession session){
            boolean sub_exit = false;

            while (!sub_exit) {

                System.out.println("\n");
                System.out.println("Choose an operation:");
                System.out.println("1: Create rating");
                System.out.println("2: Delete rating");
                System.out.println("3: Delete All ratings by user");
                System.out.println("4: List All ratings");
                System.out.println("5: List All ratings by user or game");
              
    
                System.out.println("0: Return to main menu");
                System.out.print("Enter option: ");

                int sub_option = scanner.nextInt();
                scanner.nextLine(); 
                if (sub_option == 1) {
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
                else if (sub_option == 2) {

                    System.out.print("Enter id of rating to delete: ");
                    String delete = scanner.nextLine();
                    session.delete(delete);
                    session.saveChanges();
                } 
                else if (sub_option == 3){
                    System.out.print("Enter id, username or email of user to delete all his/her ratings: ");
                    String delete = scanner.nextLine();
                    UserModel find_user = session.advanced().rawQuery(UserModel.class, "from UserModels where id() = '/UserModels" + delete + "'" +
                    "or username = '" + delete + "'" + "or email = '" + delete + "'").firstOrDefault();
                    if (find_user == null) {
                        System.out.println("User not found. Please try again.");
                        break;
                    }
                    session.query(RatingModel.class).whereEquals("user_id", find_user.getId()).toList().forEach(session::delete);
                    session.saveChanges();
                }
                
                else if (sub_option == 4) {
                    this.read(scanner, session);
                } 
                else if (sub_option == 5){
                   this.readByUserOrGame(scanner, session);
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

    private void read(Scanner scanner, IDocumentSession session) {
        System.out.println("\n");
        int pageSize = 5;
        long totalDocuments = session.query(RatingModel.class).count();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total ratings: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No ratings found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.printf("%-29s %-29s %-29s %-1s %-6s\n", "Id", "User Id", "Game id", "Rating", "Date");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                session.query(RatingModel.class).skip(skipDocuments).take(pageSize).toList().forEach(p -> {
                    System.out.printf("%-29s %-29s %-29s %-1i %-6s\n",
                            p.getId(),
                            p.getUser_id(),
                            p.getGame_id(),
                            p.getRating(),
                            p.getDate());
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
    private void readByUserOrGame(Scanner scanner, IDocumentSession session) {

        System.out.println("\n");
        System.out.print("Enter username or email or game-name of ratings to search: ");
        String username_or_email_or_gamename = scanner.nextLine();

        int pageSize = 5;
        UserModel found_user = null;
        GameModel found_game = null;
        
        found_user = session.advanced().rawQuery(UserModel.class,
        "from UserModels where username = '" + username_or_email_or_gamename + "'" +
                "or email = '" + username_or_email_or_gamename + "'")
        .firstOrDefault();
        found_game = session.advanced().rawQuery(GameModel.class,
        "from GameModels where name = '" + username_or_email_or_gamename + "'").firstOrDefault();

        long totalDocuments = session.advanced().rawQuery(RatingModel.class,
        "from RatingModels where user_id = '" + found_user.getId() + "'" + "or game_id = '" + found_game.getId() + "'").count();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total ratings: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No ratings found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.printf("%-29s %-29s %-29s %-1s %-6s\n", "Id", "User Id", "Game id", "Rating", "Date");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;

                
        
                Iterable<RatingModel> page = session.advanced().rawQuery(RatingModel.class, "from RatingModels where user_id = '" + found_user.getId() + "'" + "or game_id = '" 
                + found_game.getId() + "'").skip(skipDocuments).take(pageSize).toList();
        
                for (RatingModel p : page) {
                    System.out.printf("%-29s %-29s %-29s %-1i %-6s\n",
                            p.getId(),
                            p.getUser_id(),
                            p.getGame_id(),
                            p.getRating(),
                            p.getDate());
                }
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
