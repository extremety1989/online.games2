package com.online.games2.app;

import java.util.Scanner;

import net.ravendb.client.documents.session.IDocumentSession;

public class Comment {
    public void run(Scanner scanner, IDocumentSession session) {
        // Users management
        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Create comment");
            System.out.println("2: Delete comment");
            System.out.println("3: Delete All comments by user");
            System.out.println("4: List All comments");
            System.out.println("5: List All comments by user or game");

            System.out.println("0: Return to main menu");
            System.out.print("Enter option: ");

            int sub_option = scanner.nextInt();
            scanner.nextLine();
            if (sub_option == 1) {
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
                commentModel.setCreated_at(new java.sql.Date(System.currentTimeMillis()));
                session.saveChanges();
            } else if (sub_option == 2) {

                System.out.print("Enter id of comment to delete: ");
                String delete = scanner.nextLine();
                try {
                    session.delete(delete);
                    session.saveChanges();
                } catch (Exception e) {
                    System.out.println("Comment not found.");
                }
            } else if (sub_option == 3) {
                System.out.print("Enter id, username or email of user to delete all his/her comments: ");
                String delete = scanner.nextLine();
                try {

                    UserModel found_user = null;
                    GameModel found_game = null;
                    found_user = session.advanced().rawQuery(UserModel.class,
                            "from Users where username = '" + delete + "'" +
                                    "or email = '" + delete + "'")
                            .firstOrDefault();
                    found_game = session.advanced().rawQuery(GameModel.class,
                            "from Games where name = '" + delete + "'").firstOrDefault();
                    
                    String user_id = found_user.getId();
                    String game_id = found_game.getId();
                 
                    session.advanced().rawQuery(CommentModel.class,
                            "from CommentModels where user_id = '" + user_id + "'" + "or game_id = '" + game_id + "'")
                            .waitForNonStaleResults()
                            .toList()
                            .forEach(x -> session.delete(x));
                    session.saveChanges();
                } catch (Exception e) {
                    System.out.println("User not found.");
                }
            }

            else if (sub_option == 4) {
                this.read(scanner, session);
            } 
          else if (sub_option == 5) {
                this.readCommentSByUserORGame(scanner, session);
            } else if (sub_option == 0) {
                sub_exit = true;
                break;
            } else {
                System.out.println("Invalid option. Please try again.");
                break;
            }
        }
    }

    public void read(Scanner scanner, IDocumentSession session) {
        System.out.println("\n");
        int pageSize = 5;
        long totalDocuments = session.advanced().rawQuery(CommentModel.class, "from CommentModels").count();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total comments: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No comments found.");
        } else {
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {

                System.out.println("\n");
                System.out.printf("%-29s %-29s %-29s %-20s %-5\n", "Id", "User Id", "Game Id", "Comment", "Date");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                CommentModel[] page = session.advanced().rawQuery(CommentModel.class, "from CommentModels")
                        .skip(skipDocuments).take(pageSize).toList().toArray(new CommentModel[0]);

                for (CommentModel commentModel : page) {
                    System.out.printf("%-29s %-29s %-29s %-20s %-5s\n",
                            commentModel.getId(), commentModel.getUser_id(), commentModel.getGame_id(),
                            commentModel.getComment(), commentModel.getCreated_at());
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

    
    private void readCommentSByUserORGame(Scanner scanner, IDocumentSession session) {

        System.out.println("\n");
        System.out.print("Enter username or email or game-name of comments to search: ");
        String username_or_email_or_gamename = scanner.nextLine();
        UserModel found_user = null;
        GameModel found_game = null;
        int pageSize = 5;
        found_user = session.advanced().rawQuery(UserModel.class,
                "from Users where username = '" + username_or_email_or_gamename + "'" +
                        "or email = '" + username_or_email_or_gamename + "'")
                .firstOrDefault();
        found_game = session.advanced().rawQuery(GameModel.class,
                "from Games where name = '" + username_or_email_or_gamename + "'").firstOrDefault();

        if (found_user == null && found_game == null) {
            System.out.println("User or game not found.");
        } else {
            String user_id = found_user.getId();
            String game_id = found_game.getId();

            long totalDocuments = session.advanced().rawQuery(CommentModel.class,
                    "from CommentModels where user_id = '" + user_id + "'" + "or game_id = '" + game_id + "'").count();
            int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
            System.out.printf("Total comments: %d\n", totalDocuments);
            if (totalPages == 0) {
                System.out.println("No comments found.");
            } else {
                int currentPage = 1; // Start with page 1
                boolean paginating = true;

                while (paginating) {

                    System.out.println("\n");
                    System.out.printf("%-29s %-29s %-29s %-20s %-s\n", "Id", "Game Id", "User Id", "Comment", "Date");
                    System.out.println(
                            "----------------------------------------------------------------------------");

                    int skipDocuments = (currentPage - 1) * pageSize;
                    CommentModel[] page = session.advanced().rawQuery(CommentModel.class,
                            "from CommentModels where user_id = '" + user_id + "'" + "or game_id = '" + game_id + "'")
                            .skip(skipDocuments).take(pageSize).toList().toArray(new CommentModel[0]);
                    
                    for (CommentModel commentModel : page) {
                        System.out.printf("%-29s %-29s %-29s %-20s %-s\n",
                                commentModel.getId(), commentModel.getGame_id(), commentModel.getUser_id(),
                                commentModel.getComment(), commentModel.getCreated_at());
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
}
