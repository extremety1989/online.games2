package com.online.games2.app;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.checkerframework.checker.units.qual.s;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.client.documents.session.IRawDocumentQuery;

public class Game {
    public void run(Scanner scanner, DocumentStore store, Reader reader){
        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Create game");
            System.out.println("2: View game");
            System.out.println("3: Update game");
            System.out.println("4: Delete game");
            System.out.println("5: List All games");
            System.out.println("6: List All games by category");
            System.out.println("7: List All games by price");
            System.out.println("8: Purchase a game");
            System.out.println("0: Return to main menu");
            System.out.print("Enter option: ");

            int sub_option = scanner.nextInt();
            scanner.nextLine();

            if (sub_option == 1) {
                try (IDocumentSession session = store.openSession()){
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();

                    if(name.isEmpty()){
                        System.out.println("Please enter the field.");
                        return;
                    }

                    System.out.print("Enter description: ");
                    String description = scanner.nextLine();

                    if(description.isEmpty()){
                        System.out.println("Please enter the field.");
                        return;
                    }

                    GameModel game = new GameModel();
                    if (session.advanced().rawQuery(GameModel.class, "from GameModels where name = '" + name + "'")
                            .waitForNonStaleResults()
                            .toList()
                            .size() > 0) {
                        System.out.println("Game already exists.");
                        return;
                    }
    
                    System.out.print("Enter category: ");
                    String category = scanner.nextLine();

                    CategoryModel categoryModel = session.advanced().rawQuery(CategoryModel.class, "from CategoryModels where name = '" + category + "'")
                    .waitForNonStaleResults()
                    .toList()
                    .get(0);

                    if (categoryModel == null){
                        System.out.println("Category does not exists.");
                      return;
                    }
    
                    System.out.print("Enter price: ");
                    String price_string = scanner.nextLine();

                    if(price_string.isEmpty()){
                        System.out.println("Please enter the field.");
                        return;
                    }

                    Double price = Double.parseDouble(price_string);
    
                    System.out.print("Enter age restriction: ");
                    String age_restriction_string = scanner.nextLine();
                    if(age_restriction_string.isEmpty()){
                        System.out.println("Please enter the field.");
                        return;
                    }
                    Integer age_restriction = Integer.parseInt(age_restriction_string);
                 

 
                    game.setName(name);
                    game.setDescription(description);
                    game.setPrice(price);
                    game.setAgeRestriction(age_restriction);
                    game.setCategory(categoryModel);
                    game.setTotal(0);
                    session.store(game);
                    session.saveChanges();
                }

            }
            else if (sub_option == 2) {
                try (IDocumentSession session = store.openSession()){
                  
                    System.out.print("Enter id or name to view: ");
                    String id_or_name = scanner.nextLine();

                   try {
                    GameModel game = session.advanced().rawQuery(GameModel.class, 
                    "from GameModels where id() = 'GameModels/" + id_or_name + "'"
                    + " or name = '" + id_or_name + "'").toList().get(0);
                   

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(game);
                    System.out.println(json);
                   } catch (Exception e) {
                    System.out.println("Game not found.");
                   }
                }
                
            } 
            else if (sub_option == 3) {
                try (IDocumentSession session = store.openSession()){
                    System.out.print(
                        "Enter game-id or game-name to update (or press enter to skip): ");

                
                String update = scanner.nextLine();

                GameModel gameModel = null;

                try {
                    gameModel = session.advanced().rawQuery(GameModel.class, 
                "from GameModels where id() = 'GameModels/" + update + "'"
                + " or name = '" + update + "'").toList().get(0);
                } catch (Exception e) {
                    System.out.println("Game not found.");
                    return;
                }


                System.out.print("Enter new name: ");
                String newName = scanner.nextLine();

                if (!newName.isEmpty()) {
                    gameModel.setName(newName);
                }

                System.out.print("Enter new description: ");
                String newDescription = scanner.nextLine();

                if (!newDescription.isEmpty()) {
                    gameModel.setDescription(newDescription);
                }



                System.out.print("Enter new category: ");
                String newCategory = scanner.nextLine();

                System.out.print("Enter new price: ");
                String newPrice_string = scanner.nextLine();
                Double newPrice = null;
                if (!newPrice_string.isEmpty()) {
                    newPrice = Double.parseDouble(newPrice_string);
                }
          

                System.out.print("Enter new age restriction: ");
                String newAgeLimit_string = scanner.nextLine();
                Integer newAgeLimit = null;
                if (!newAgeLimit_string.isEmpty()) {
                    newAgeLimit = Integer.parseInt(newAgeLimit_string);
                }
               
                if (newPrice != null){
                    gameModel.setPrice(newPrice);
                }
         
                if( newAgeLimit != null){
                    gameModel.setAgeRestriction(newAgeLimit);
                }
                
                if (!newCategory.isEmpty()) {
                    gameModel.setCategory((CategoryModel) session.advanced().rawQuery(CategoryModel.class, "from CategoryModels where name = '" + newCategory + "'")
                    .waitForNonStaleResults()
                    .toList()
                    .get(0));
                }  
                session.saveChanges();
                }

            } 
            else if (sub_option == 4) {
                try (IDocumentSession session = store.openSession()){
                                    // Delete a game
                    System.out.print("Enter id of the game to delete: ");
                    String delete = scanner.nextLine();
               
                    try {
                        session.delete("GameModels/" + delete);
                        session.saveChanges();

                    } catch (Exception e) {
                        System.out.println("Comment not found.");
                    }
                    session.saveChanges();
                }
                
            } 
            
            else if (sub_option == 5) {
                try (IDocumentSession session = store.openSession()){
                    reader.read(scanner, session, GameModel.class, "GameModels"); 
                }
          

            }else if (sub_option == 6) {
                try (IDocumentSession session = store.openSession())
                {
                    System.out.println("Enter category: ");
                    this.findByCategory(scanner, session);
                }
     
            } else if (sub_option == 7) {
                try (IDocumentSession session = store.openSession())
                {
                    this.findByPrice(scanner, session);
                }
          
            } else if (sub_option == 8) {
                try (IDocumentSession session = store.openSession())
                {
                    this.purchaseAGame(scanner, session);
                }
           
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
        IRawDocumentQuery<GameModel> results = session.advanced().rawQuery(GameModel.class, 
        "from GameModels where category.name = '" + category + "'")
                .waitForNonStaleResults();
        long totalDocuments = results
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
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                session.advanced().rawQuery(GameModel.class, 
        "from GameModels where category.name = '" + category + "'")
                .waitForNonStaleResults()
                        .skip(skipDocuments)
                        .take(pageSize)
                        .toList()
                        .forEach((x) -> {
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

    private void findByPrice(Scanner scanner, IDocumentSession session) {
        System.out.println("Enter minimum price: ");
        String price_string = scanner.nextLine();
        Double minPrice = Double.parseDouble(price_string);
        System.out.println("Enter maximum price: ");
        String maxPrice_string = scanner.nextLine();
        Double maxPrice = Double.parseDouble(maxPrice_string);
        System.out.println("\n");
        int pageSize = 5;
        IRawDocumentQuery<GameModel> results = session.advanced().rawQuery(GameModel.class, "from GameModels where price >= " + minPrice + " and price <= " + maxPrice)
        .waitForNonStaleResults();
        long totalDocuments = results
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
  
                System.out.println(
                        "----------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                session.advanced().rawQuery(GameModel.class, "from GameModels where price >= " + minPrice + " and price <= " + maxPrice)
        .waitForNonStaleResults()
                        .skip(skipDocuments)
                        .take(pageSize)
                        .toList()
                        .forEach((x) -> {
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

    private void purchaseAGame(Scanner scanner, IDocumentSession session) {
        System.out.print("Enter user-id or username or email: ");
        String id_or_username_or_email = scanner.nextLine();
        UserModel found_user = session.advanced().rawQuery(UserModel.class, "from UserModels where id() = 'UserModels/" + id_or_username_or_email + "'"
        + " or username = '" + id_or_username_or_email + "'"
        + " or email = '" + id_or_username_or_email + "'")
        .waitForNonStaleResults()
        .toList()
        .get(0);
        if (found_user == null) {
            System.out.println("User not found.");
            return;
        }
        System.out.print("Enter the game-name or game-id that he wants to purchase: ");
        String gameName_or_gameId = scanner.nextLine();

        List<GameModel> results = session.advanced().rawQuery(GameModel.class, "from GameModels where id() = 'GameModels/" + gameName_or_gameId + "'"
        + " or name = '" + gameName_or_gameId + "'")
        .waitForNonStaleResults().toList();
        GameModel found_game = results.get(0);

        if(found_game == null){
            System.out.println("Game not found.");
            return;
        }

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

        System.out.println("Enter bank: ");
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

        String bankChoice_string = scanner.nextLine();
        Integer bankChoice = Integer.parseInt(bankChoice_string);
        if (0 < bankChoice && bankChoice > 10) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }

        String bankName = bankNames.get(bankChoice - 1);
        System.out.println("Enter bank number (12-length long): ");

        String bankNumber = scanner.nextLine();
        if (bankNumber.isEmpty() || bankNumber.length() != 12) {
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

        if ((int) found_user.getAge() <= found_game.getAgeRestriction()){
            System.out.println("You are not old enough to purchase this game.");
            return;   
        }
        PurchaseModel new_purchase = new PurchaseModel();
        new_purchase.setAmount(amount);
        new_purchase.setCurrency(currency);
        BankModel bank = new BankModel();
        if (bankName != null && bankNumber != null) {
            bank.setName(bankName);
            bank.setNumber(bankNumber);
        }
        new_purchase.setBank(bank);
        new_purchase.setUser_id(found_user.getId());
        new_purchase.setGame_id(found_game.getId());
        new_purchase.setCreated_at(new Date());
       
        try {
            session.store(new_purchase);
   
            System.out.println("Transaction created successfully!");
            found_game.setTotal((int) found_game.getTotal() + 1);
            session.saveChanges();

        } catch (Exception e) {
            System.out.println("Transaction not created.");
        }
    }
}
