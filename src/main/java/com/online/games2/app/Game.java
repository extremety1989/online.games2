package com.online.games2.app;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;



import net.ravendb.client.documents.session.IDocumentSession;

public class Game {
    public void run(Scanner scanner, IDocumentSession session){
        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Create game");
            System.out.println("2: Update game");
            System.out.println("3: Delete game");
            System.out.println("4: List All games");
            System.out.println("5: List All games by name");
            System.out.println("6: List All games by category");
            System.out.println("7: List All games by price");
            System.out.println("8: Purchase a game");
            System.out.println("0: Return to main menu");
            System.out.print("Enter option: ");

            int sub_option = scanner.nextInt();
            scanner.nextLine();

            if (sub_option == 1) {

                System.out.print("Enter name: ");
                String name = scanner.nextLine();

                System.out.print("Enter category: ");
                String category = scanner.nextLine();

                System.out.print("Enter price: ");
                Double price = scanner.nextDouble();

                System.out.print("Enter age limit: ");
                Integer age_limit = scanner.nextInt();
                scanner.nextLine();
                GameModel game = new GameModel();
                if (session.advanced().rawQuery(GameModel.class, "from GameModels where name = '" + name + "'")
                        .waitForNonStaleResults()
                        .toList()
                        .size() > 0) {
                    System.out.println("Game already exists.");
                    return;
                }
  
                //find category
                GameModel GameModel = session.advanced().rawQuery(GameModel.class, "from GameModels where category.name = '" + category + "'")
                        .waitForNonStaleResults()
                        .toList()
                        .get(0);
                if (GameModel != null){
                    System.out.println("Category not found.");
                    return;
                }
                game.setName(name);
                game.setPrice(price);
                game.setAgeLimit(age_limit);
                game.setCategory((CategoryModel) session.advanced().rawQuery(CategoryModel.class, "from CategoryModels where name = '" + category + "'"));
                game.setTotal(0);
                session.store(game);

            }
            else if (sub_option == 2) {

                System.out.print(
                        "Enter game-id or game-name to update (or press enter to skip): ");

                String update = scanner.nextLine();

                System.out.print("Enter new name: ");
                String newName = scanner.nextLine();

                System.out.print("Enter new category: ");
                String newCategory = scanner.nextLine();

                System.out.print("Enter new price: ");
                Double newPrice = scanner.nextDouble();

                System.out.print("Enter new age limit: ");
                Integer newAgeLimit = scanner.nextInt();
                scanner.nextLine();

           
                session.advanced().rawQuery(GameModel.class, "from GameModels where id() = 'GameModels/" + update + "'"
                + " or name = '" + update + "'")
                .waitForNonStaleResults()
                .toList()
                .forEach(x -> 
                {
                    if (!newName.isEmpty()) {
                        x.setName(newName);
                    }
                   if (newPrice != null){
                      x.setPrice(newPrice);
                   }
             
                    if( newAgeLimit != null){
                        x.setAgeLimit(newAgeLimit);
                    }
                    
                    if (!newCategory.isEmpty()) {
                        x.setCategory((CategoryModel) session.advanced().rawQuery(CategoryModel.class, "from CategoryModels where name = '" + newCategory + "'")
                        .waitForNonStaleResults()
                        .toList()
                        .get(0));
                    }
                });   
                session.saveChanges();
            } else if (sub_option == 3) {
                // Delete a game
                System.out.print("Enter id or name of game to delete: ");
                String delete = scanner.nextLine();
               
                session.advanced().rawQuery(GameModel.class, "from GameModels where id() = 'GameModels/" + delete + "'"
                        + " or name = '" + delete + "'")
                        .waitForNonStaleResults()
                        .toList()
                        .forEach(x -> session.delete(x));
                session.saveChanges();
            } else if (sub_option == 4) {
                
                this.read(scanner, session); 

            } else if (sub_option == 5) {
               this.findByName(scanner, session);
            } else if (sub_option == 6) {
               this.findByCategory(scanner, session);
            } else if (sub_option == 7) {
               this.findByPrice(scanner, session);
            } else if (sub_option == 8) {
                this.purchaseAGame(scanner, session);
            } else if (sub_option == 0) {
                sub_exit = true;
                break;
            } else {
                System.out.println("Invalid option. Please try again.");
                break;
            }
        }
    }

    private void findByCategory(Scanner scanner, IDocumentSession session) {
        String category = scanner.nextLine();
        System.out.println("\n");
        int pageSize = 5;
        long totalDocuments = session.advanced().rawQuery(GameModel.class, "from GameModels where category.name = '" + category + "'")
                .waitForNonStaleResults()
                .toList()
                .size();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total games: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No game found.");
        } else {
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {

                System.out.println("\n");
                System.out.printf("%-29s %-30s %-5s %-20s %-2s %-9s\n", "Id", "Name", "Price", "Category",
                        "Age Restriction");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                session.advanced().rawQuery(GameModel.class, "from GameModels where category.name = '" + category + "'")
                        .waitForNonStaleResults()
                        .skip(skipDocuments)
                        .take(pageSize)
                        .toList()
                        .forEach((x) -> System.out.printf("%-30s %-30s %-5s %-20s %-2s %-9s\n", x.getId(), x.getName(), x.getPrice(), x.getCategory(), x.getAgeLimit(), x.getTotal()));

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

    private void findByPrice(Scanner scanner, IDocumentSession session) {
        System.out.println("Enter minimum price: ");
        Double minPrice = scanner.nextDouble();
        System.out.println("Enter maximum price: ");
        Double maxPrice = scanner.nextDouble();
        System.out.println("\n");
        int pageSize = 5;
        long totalDocuments = session.advanced().rawQuery(GameModel.class, "from GameModels where price >= " + minPrice + " and price <= " + maxPrice)
                .waitForNonStaleResults()
                .toList()
                .size();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total games: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No game found.");
        } else {
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {

                System.out.println("\n");
                System.out.printf("%-29s %-30s %-5s %-20s %-2s %-9s\n", "Id", "Name", "Price", "Category",
                        "Age Restriction");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                session.advanced().rawQuery(GameModel.class, "from GameModels where price >= " + minPrice + " and price <= " + maxPrice)
                        .waitForNonStaleResults()
                        .skip(skipDocuments)
                        .take(pageSize)
                        .toList()
                        .forEach((x) -> System.out.printf("%-30s %-30s %-5s %-20s %-2s %-9s\n", x.getId(), x.getName(), x.getPrice(), x.getCategory(), x.getAgeLimit(), x.getTotal()));

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

    private void purchaseAGame(Scanner scanner, IDocumentSession session) {
        System.out.print("Enter user-id or username or email: ");
        String id_or_username_or_email = scanner.nextLine();
        System.out.print("Enter the game-name or game-id that he wants to purchase: ");
        String gameName_or_gameId = scanner.nextLine();

        List<String> bankNames = Arrays.asList(
                "Bank of America",
                "JPMorgan Chase",
                "Wells Fargo",
                "Citigroup",
                "Goldman Sachs",
                "Morgan Stanley",
                "HSBC",
                "Barclays",
                "Royal Bank of Canada",
                "BNP Paribas");

        System.out.println("Enter bank (0 to skip): ");
        System.out.println("[1] Bank of America");
        System.out.println("[2] JPMorgan Chase");
        System.out.println("[3] Wells Fargo");
        System.out.println("[4] Citigroup");
        System.out.println("[5] Goldman Sachs");
        System.out.println("[6] Morgan Stanley");
        System.out.println("[7] HSBC");
        System.out.println("[8] Barclays");
        System.out.println("[9] Royal Bank of Canada");
        System.out.println("[10] BNP Paribas");

        int bankChoice = scanner.nextInt();
        if (bankChoice > 10) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }

        String bankName = bankNames.get(bankChoice - 1);
        System.out.println("Enter bank number (enter to skip): ");
        String bankNumber_string = scanner.nextLine();
        Long bankNumber = Long.parseLong(bankNumber_string);
        if (bankNumber != null && (bankNumber < 0 || bankNumber > 999999999999L)) {
            System.out.println("Invalid bank number. Please try again.");
            return;
        }
        System.out.println("Enter amount: ");
        String amount_String = scanner.nextLine();
        Double amount = Double.parseDouble(amount_String);
        if (amount < 0) {
            System.out.println("Invalid amount. Please try again.");
            return;
        }
        System.out.println("Enter a currency US or EUR: ");
        String currency = scanner.nextLine();
        if (!currency.equals("US") && !currency.equals("EUR")) {
            System.out.println("Invalid currency. Please try again.");
            return;
        }

        UserModel found_user = session.advanced().rawQuery(UserModel.class, "from UserModels where id() = 'UserModels/" + id_or_username_or_email + "'"
                + " or username = '" + id_or_username_or_email + "'"
                + " or email = '" + id_or_username_or_email + "'")
                .waitForNonStaleResults()
                .toList()
                .get(0);

        if (found_user != null) {

            GameModel found_game = session.advanced().rawQuery(GameModel.class, "from GameModels where id() = 'GameModels/" + gameName_or_gameId + "'"
                    + " or name = '" + gameName_or_gameId + "'")
                    .waitForNonStaleResults()
                    .toList()
                    .get(0);
            if (found_game != null) {
                if ((int) found_user.getAge() >= found_game.getAgeLimit()){

                   
                    PurchaseModel new_purchase = new PurchaseModel();
                    new_purchase.setAmount(amount);
                    new_purchase.setCurrency(currency);
               
                    if (bankName != null && bankNumber != null) {
                        new_purchase.setBankName(bankName);
                        new_purchase.setBankNumber(bankNumber);
                    }
                    new_purchase.setGame_id(found_game.getId());
                    new_purchase.setUser_id(found_user.getId());
                    new_purchase.setCreated_at(new java.sql.Date(new Date().getTime()));
                   
                    try {
                        session.store(new_purchase);
                        session.saveChanges();
                        System.out.println("Transaction created successfully!");
                        session.advanced().rawQuery(GameModel.class, "from GameModels where id() = 'GameModels/" + gameName_or_gameId + "'"
                        + " or name = '" + gameName_or_gameId + "'")
                        .waitForNonStaleResults()
                        .toList()
                        .forEach(x -> x.setTotal((int) x.getTotal() + 1));
                    } catch (Exception e) {
                        System.out.println("Transaction not created.");
                    }
                    
                } else {
                    System.out.println("not old enough to buy this game.");
                }

            } else {
                System.out.println("Game not found.");
            }

        } else {
            System.out.println("user not found.");
        }
    }

    private void findByName(Scanner scanner, IDocumentSession session) {
        String name = scanner.nextLine();
        System.out.println("\n");
        int pageSize = 5;
        long totalDocuments = session.advanced().rawQuery(GameModel.class, "from GameModels where name like '" + name + "%'")
                .waitForNonStaleResults()
                .toList()
                .size();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total games: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No game found.");
        } else {
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {

                System.out.println("\n");
                System.out.printf("%-29s %-30s %-5s %-20s %-2s %-9s\n", "Id", "Name", "Price", "Category",
                        "Age Restriction");
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                session.advanced().rawQuery(GameModel.class, "from GameModels where name like '" + name + "%'")
                        .waitForNonStaleResults()
                        .skip(skipDocuments)
                        .take(pageSize)
                        .toList()
                        .forEach((x) -> System.out.printf("%-30s %-30s %-5s %-20s %-2s %-9s\n", x.getId(), x.getName(), x.getPrice(), x.getCategory(), x.getAgeLimit(), x.getTotal()));

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

    private void read(Scanner scanner, IDocumentSession session) {
        {
            System.out.println("\n");
            int pageSize = 5;
            long totalDocuments = session.advanced().rawQuery(GameModel.class, "from GameModels")
                    .waitForNonStaleResults()
                    .toList()
                    .size();
            int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
            System.out.printf("Total games: %d\n", totalDocuments);
            if (totalPages == 0) {
                System.out.println("No game found.");
            } else {
                int currentPage = 1; // Start with page 1
                boolean paginating = true;

                while (paginating) {

                    System.out.println("\n");
                    System.out.printf("%-29s %-30s %-5s %-20s %-2s\n", "Id", "Name", "Price", "Category",
                    "Age Restriction");
                    System.out.println(
                            "----------------------------------------------------------------------------");

                    int skipDocuments = (currentPage - 1) * pageSize;
                    session.advanced().rawQuery(GameModel.class, "from GameModels")
                            .waitForNonStaleResults()
                            .skip(skipDocuments)
                            .take(pageSize)
                            .toList()
                            .forEach((x) -> System.out.printf("%-30s %-30s %-5s %-20s %-2s\n", x.getId(), x.getName(), x.getPrice(), x.getCategory(), x.getAgeLimit()));
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
