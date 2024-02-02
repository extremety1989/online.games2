package com.online.games2.app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.checkerframework.checker.units.qual.s;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.session.IDocumentQuery;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.client.documents.session.IRawDocumentQuery;

public class User {
    public void run(Scanner scanner, DocumentStore store, Reader reader) {
        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Create user");
            System.out.println("2: View user");
            System.out.println("3: Update user");
            System.out.println("4: Delete user");
            System.out.println("5: List All users");
            System.out.println("6: List All comments by user");
            System.out.println("7: List All ratings by user");
            System.out.println("8: List All purchases by user");
            System.out.println("0: Return to main menu");
            System.out.print("Enter option: ");

            int sub_option = scanner.nextInt();
            scanner.nextLine();

            if (sub_option == 1) {
                try (IDocumentSession session = store.openSession()) {
                    System.out.print("Enter lastname: ");
                    String lastname = scanner.nextLine();
                    System.out.print("Enter firstname: ");
                    String firstname = scanner.nextLine();
                    System.out.print("Enter age: ");
                    int age = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    MessageDigest messageDigest;
                    try {
                        messageDigest = MessageDigest.getInstance("SHA-256");
                        messageDigest.update(password.getBytes());
                        String passwordHash = new String(messageDigest.digest());
                        password = passwordHash;
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    if (lastname.isEmpty() || firstname.isEmpty() || age == 0 || email.isEmpty() || username.isEmpty()
                            || password.isEmpty()) {
                        System.out.println("Please fill all fields");
                        break;
                    }

                    UserModel user = new UserModel();

                    user.setLastname(lastname);
                    user.setFirstname(firstname);
                    user.setAge(age);
                    user.setEmail(email);
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setCreated_at(new Date());
                    session.store(user);
                    session.saveChanges();
                }

            } else if (sub_option == 2) {

                try (IDocumentSession session = store.openSession()) {
                    System.out.print("Enter user_id, username or email to find: ");
                    String id_or_username_or_email = scanner.nextLine();

                    UserModel found_user = session.query(UserModel.class)
                    .whereEquals("id", "UserModels/" + id_or_username_or_email)
                    .orElse()
                    .whereEquals("username", id_or_username_or_email)
                    .orElse()
                    .whereEquals("email", id_or_username_or_email)
                    .firstOrDefault();
                    if (found_user == null) {
                        System.out.println("User not found");
                        break;
                    }
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(found_user);
                    System.out.println(json);
                }

            } else if (sub_option == 3) {

                try (IDocumentSession session = store.openSession()) {
                    System.out.print(
                            "Enter id or username or email of user to update: ");

                    String id_or_username_or_emal = scanner.nextLine();

                    System.out.print("Enter new lastname: ");
                    String newlastname = scanner.nextLine();
                    System.out.print("Enter new firstname: ");
                    String newFirstname = scanner.nextLine();
                    System.out.print("Enter new age: ");
                    int newAge = 0;
                    String ageInput = scanner.nextLine();
                    System.out.print("Enter new email: ");
                    String newEmail = scanner.nextLine();

                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine();

                    UserModel found_user = session.query(UserModel.class)
                    .whereEquals("id", id_or_username_or_emal)
                    .orElse()
                    .whereEquals("username", id_or_username_or_emal)
                    .orElse()
                    .whereEquals("email", id_or_username_or_emal)
                    .firstOrDefault();

                    if (found_user == null) {
                        System.out.println("User not found");
                        break;
                    }
                    if (!ageInput.isEmpty()) {
                        newAge = Integer.parseInt(ageInput);
                        found_user.setAge(newAge);
                    }

                    if (!newlastname.isEmpty()) {
                        found_user.setLastname(newlastname);
                    }

                    if (!newFirstname.isEmpty()) {
                        found_user.setFirstname(newFirstname);
                    }

                    if (newAge > 0) {
                        found_user.setAge(newAge);
                    }

                    if (!newEmail.isEmpty()) {
                        found_user.setEmail(newEmail);
                    }

                    if (!newPassword.isEmpty()) {
                        MessageDigest messageDigest;
                        try {
                            messageDigest = MessageDigest.getInstance("SHA-256");
                            messageDigest.update(newPassword.getBytes());
                            String passwordHash = new String(messageDigest.digest());
                            found_user.setPassword(passwordHash);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }

                    session.saveChanges();
                }

            } else if (sub_option == 4) {
                try (IDocumentSession session = store.openSession()) {
                    System.out.print("Enter id, username or email of user to delete: ");
                    String id_or_username_or_emal = scanner.nextLine();

                    UserModel found_user = session.query(UserModel.class)
                    .whereEquals("id", id_or_username_or_emal)
                    .orElse()
                    .whereEquals("username", id_or_username_or_emal)
                    .orElse()
                    .whereEquals("email", id_or_username_or_emal)
                    .firstOrDefault();
                    if (found_user == null) {
                        System.out.println("User not found");
                        break;
                    }
                    session.delete(found_user);
                    session.saveChanges();
                }

            }

            else if (sub_option == 5) {
                try (IDocumentSession session = store.openSession()) {
                    reader.read(scanner, session, UserModel.class, "UserModels");
                }

            }

            else if (sub_option == 6) {
                try (IDocumentSession session = store.openSession()) {
                    System.out.print("Enter user_id, username or email to find: ");
                    this.readAll(scanner, session, "CommentModels",CommentModel.class, "comments");
                }

            }
            else if (sub_option == 7) {
                try (IDocumentSession session = store.openSession()) {
                    System.out.print("Enter user_id, username or email to find: ");
                    this.readAll(scanner, session,"RatingModels", RatingModel.class, "ratings");
                }

            }
            else if (sub_option == 8) {
                try (IDocumentSession session = store.openSession()) {
                    System.out.print("Enter user_id, username or email to find: ");
                    this.readAll(scanner, session, "PurchaseModels",PurchaseModel.class, "purchases");
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

    private <T> void readAll(Scanner scanner, IDocumentSession session, String Model, Class<T> ModelClass, String What) {

        System.out.println("\n");
        String value = scanner.nextLine();
        int pageSize = 5;
      
        UserModel found_user = session.query(UserModel.class)
        .whereEquals("id", "UserModels/"+value)
        .orElse()
        .whereEquals("username", value)
        .orElse()
        .whereEquals("email", value)
        .firstOrDefault();

        if (found_user == null) {
            System.out.println("User not found");
            return;
        }

        List<T> results = session.query(ModelClass)
        .whereEquals("user_id", found_user.getId())
        .toList();

        long totalDocuments = results.size();

        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total "+What+": %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No "+What+" found.");
        } else {
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {

                System.out.println("\n");

                System.out.println(
                        "------------------------------------------------------------------------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
            
               
                session.query(ModelClass)
                            .whereEquals("user_id", found_user.getId())
                            .waitForNonStaleResults()
                            .skip(skipDocuments)
                            .take(pageSize)
                            .toList()
                            .forEach(x -> {
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                String json = gson.toJson(x);
                                System.out.println(json);
                            });

                System.out.println(
                        "------------------------------------------------------------------------------------------------------------------------------------------");
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
