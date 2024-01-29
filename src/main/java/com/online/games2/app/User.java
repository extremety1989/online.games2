package com.online.games2.app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import net.ravendb.client.documents.session.IDocumentSession;

public class User {
        public void run(Scanner scanner, IDocumentSession session, Reader reader){
                        boolean sub_exit = false;

                        while (!sub_exit) {

                            System.out.println("\n");
                            System.out.println("Choose an operation:");
                            System.out.println("1: Create user");
                            System.out.println("2: Read user");
                            System.out.println("3: Update user");
                            System.out.println("4: Delete user");
                            System.out.println("5: List All users");
                            System.out.println("0: Return to main menu");
                            System.out.print("Enter option: ");

                            int sub_option = scanner.nextInt();
                            scanner.nextLine(); 

                            if (sub_option == 1) {

                        
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

                                if( lastname.isEmpty() || firstname.isEmpty() || age == 0 || email.isEmpty() || username.isEmpty() || password.isEmpty()){
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
                                user.setComments(new ArrayList<String>());
                                user.setRatings(new ArrayList<String>());
                                user.setPurchases(new ArrayList<String>());
                                session.store(user);
                                session.saveChanges();
                            
                            } else if (sub_option == 2) {

                        
                                System.out.print("Enter user_id, username or email to find: ");
                                String id_or_username_or_email = scanner.nextLine();
                                
                                session.advanced().rawQuery(UserModel.class, "from Users where id = '" + id_or_username_or_email + "' or username = '" + id_or_username_or_email + "' or email = '" + id_or_username_or_email + "'").toList();

                                if(session.advanced().rawQuery(UserModel.class, "from Users where id = '" + id_or_username_or_email + "' or username = '" + id_or_username_or_email + "' or email = '" + id_or_username_or_email + "'").toList().size() == 0){
                                    System.out.println("User not found");
                                    break;
                                }

                                UserModel found_user = session.advanced().rawQuery(UserModel.class,
                                "from UserModels where id = '" + id_or_username_or_email
                                 + "' or username = '" + id_or_username_or_email + "' or email = '" + id_or_username_or_email + "'")
                                .firstOrDefault();

                                if(found_user == null){
                                    System.out.println("User not found");
                                    break;
                                }

                                long totalComments = session.advanced().rawQuery(CommentModel.class, "from CommentModels where user_id = '" + found_user.getId() + "'").toList().size();
                                long totalRatings = session.advanced().rawQuery(RatingModel.class, "from RatingModels where user_id = '" + found_user.getId() + "'").toList().size();
                                long totalPurchases = session.advanced().rawQuery(PurchaseModel.class, "from PurchaseModels where user_id = '" + found_user.getId() + "'").toList().size();
                                
                                System.out.println("\n");
                                System.out.println("User details:");
                                System.out.println("Id: " + found_user.getId());
                                System.out.println("Lastname: " + found_user.getLastname());
                                System.out.println("Firstname: " + found_user.getFirstname());
                                System.out.println("Age: " + found_user.getAge());
                                System.out.println("Email: " + found_user.getEmail());
                                System.out.println("Username: " + found_user.getUsername());
                                System.out.println("Password: " + found_user.getPassword());
                                System.out.println("Created at: " + found_user.getCreated_at());
                                System.out.println("Total comments: " + totalComments);
                                System.out.println("Total ratings: " + totalRatings);
                                System.out.println("Total purchases: " + totalPurchases);
                                System.out.println("\n");

                            } else if (sub_option == 3) {

                           
                                System.out.print(
                                        "Enter lastname or firstname of user to update (or press enter to skip): ");
                               
                                String update = scanner.nextLine();

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

                                UserModel found_user = session.advanced().rawQuery(UserModel.class,
                                "from UserModels where id = '" + update
                                 + "' or username = '" + update + "' or email = '" + update + "'")
                                .firstOrDefault();

                                if(found_user == null){
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
                 
                            } else if (sub_option == 4) {
                             
                                System.out.print("Enter id, username or email of user to delete: ");
                                String delete = scanner.nextLine();
                                
                                UserModel found_user = session.advanced().rawQuery(UserModel.class,
                                "from UserModels where id = '" + delete
                                 + "' or username = '" + delete + "' or email = '" + delete + "'")
                                .firstOrDefault();

                                if(found_user == null){
                                    System.out.println("User not found");
                                    break;
                                }

                                session.delete(found_user);
                                session.saveChanges();
                            } else if (sub_option == 5) {
                               reader.read(scanner, session, UserModel.class, "UserModels");
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
       
        long totalDocuments =  session.advanced().rawQuery(UserModel.class, "from UserModels").toList().size();
        int totalPages = (int) Math.ceil((double) totalDocuments / pageSize);
        System.out.printf("Total users: %d\n", totalDocuments);
        if (totalPages == 0) {
            System.out.println("No users found.");
        }else{
            int currentPage = 1; // Start with page 1
            boolean paginating = true;

            while (paginating) {
               
                System.out.println("\n");
                System.out.printf("%-29s %-20s %-20s %-5s %-20s %-20s\n", "Id", "Lastname", "Firstname", "Age", "Email", "Username");
                System.out.println(
                        "------------------------------------------------------------------------------------------------------------------------------------------");

                int skipDocuments = (currentPage - 1) * pageSize;
                List <UserModel> pageusers = session.advanced().rawQuery(UserModel.class, "from UserModels").skip(skipDocuments).take(pageSize).toList();
                for (UserModel user : pageusers) {
                    System.out.printf("%-29s %-20s %-20s %-5s %-20s %-20s\n", user.getId(), user.getLastname(), user.getFirstname(), user.getAge(), user.getEmail(), user.getUsername());
                }

                // Pagination controls
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
