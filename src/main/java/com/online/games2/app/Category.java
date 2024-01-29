package com.online.games2.app;

import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.ravendb.client.documents.session.IDocumentSession;

public class Category {

    public void run(Scanner scanner, IDocumentSession session, Reader reader) {

        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Create category");
            System.out.println("2: Update category");
            System.out.println("3: Delete category");
            System.out.println("4: List All categories");
            System.out.println("0: Return to main menu");
            System.out.print("Enter option: ");

            int sub_option = scanner.nextInt();
            scanner.nextLine();

            if (sub_option == 1) {

                System.out.print("Enter name: ");
                String name = scanner.nextLine();
                if (name.isEmpty()) {
                    System.out.println("Please enter the field.");
                    return;
                }
                CategoryModel category = new CategoryModel();
                if (session.advanced().rawQuery(CategoryModel.class, "from CategoryModels where name = '" + name + "'")
                        .waitForNonStaleResults()
                        .toList()
                        .size() > 0) {
                    System.out.println("Category already exists.");
                    return;
                }
                category.setName(name);
                session.store(category);
                session.saveChanges();

                System.out.println("Category created successfully!");

            } else if (sub_option == 2) {

                System.out.print(
                        "Enter category-id or category-name: ");

                String update = scanner.nextLine();

                System.out.print("Enter new name: ");
                String newName = scanner.nextLine();

                if (!newName.isEmpty()) {

                    session.advanced()
                            .rawQuery(CategoryModel.class,
                                    "from CategoryModels where id() = 'CategoryModels/1" + update + "'"
                                            + " or name = '" + update + "'")
                            .waitForNonStaleResults()
                            .toList()
                            .forEach(x -> x.setName(newName));

                    session.saveChanges();
                }

            } else if (sub_option == 3) {

                System.out.print("Enter id or name of category to delete: ");
                String delete = scanner.nextLine();
                if (delete.isEmpty()) {
                    System.out.println("Please enter the field.");
                    return;
                }

                session.advanced()
                        .rawQuery(CategoryModel.class,
                                "from CategoryModels where id() = 'CategoryModels/" + delete + "'"
                                        + " or name = '" + delete + "'")
                        .waitForNonStaleResults()
                        .toList()
                        .forEach(x -> session.delete(x));
                session.saveChanges();
            } else if (sub_option == 4) {
                reader.read(scanner, session, CategoryModel.class, "Category");
            } else if (sub_option == 0) {
                sub_exit = true;
                break;
            } else {
                System.out.println("Invalid option. Please try again.");
                break;
            }
        }
    }

}
