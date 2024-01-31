package com.online.games2.app;

import java.util.Scanner;

import com.github.javafaker.Company;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.session.IDocumentSession;

public class Category {

    public void run(Scanner scanner, DocumentStore store, Reader reader) {

        boolean sub_exit = false;

        while (!sub_exit) {

            System.out.println("\n");
            System.out.println("Choose an operation:");
            System.out.println("1: Create category");
            System.out.println("2: View category");
            System.out.println("3: Update category");
            System.out.println("4: Delete category");
            System.out.println("5: List All categories");
            System.out.println("0: Return to main menu");
            System.out.print("Enter option: ");

            int sub_option = scanner.nextInt();
            scanner.nextLine();

            if (sub_option == 1) {
                try (IDocumentSession session = store.openSession()) {

                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    if (name.isEmpty()) {
                        System.out.println("Please enter the field.");
                        return;
                    }
                    System.out.print("Enter description: ");
                    String description = scanner.nextLine();
                    if (description.isEmpty()) {
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
                    category.setDescription(description);
                    session.store(category);
                    session.saveChanges();
    
                    System.out.println("Category created successfully!");
                }

            } 

            else if (sub_option == 2) {
                try (IDocumentSession session = store.openSession()) {
                    System.out.print("Enter id of category to view: ");
                    String id = scanner.nextLine();
                    if (id.isEmpty()) {
                        System.out.println("Please enter the field.");
                        return;
                    }
    
                    CategoryModel category = session.load(CategoryModel.class, "CategoryModels/" + id);
                    if (category == null) {
                        System.out.println("Category not found.");
                        return;
                    }

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(category);
                    System.out.println(json);
                }

            } 
            else if (sub_option == 3) {
                try (IDocumentSession session = store.openSession()){
     
                    System.out.print(
                        "Enter category id to update: ");

                String update = scanner.nextLine();
                CategoryModel category = session.load(CategoryModel.class, "CategoryModels/" + update);
                if (category == null) {
                    System.out.println("Category not found.");
                    return;
                }
                System.out.print("Enter new name: ");
                String newName = scanner.nextLine();
                
                    if (!newName.isEmpty()) {

                     
  
                        category.setName(newName);
           
                    }
                    System.out.print("Enter new description: ");
                    String newDescription = scanner.nextLine();
                    if (!newDescription.isEmpty()) {
                        category.setDescription(newDescription);
                    }

                    session.saveChanges();
                    System.out.println("Category updated successfully!");
                }

            } 
            else if (sub_option == 4) {
                try (IDocumentSession session = store.openSession()) {
                    System.out.print("Enter id of category to delete: ");
                    String delete = scanner.nextLine();
                    if (delete.isEmpty()) {
                        System.out.println("Please enter the field.");
                        return;
                    }
    
                    session.delete("CategoryModels/" + delete);
                    session.saveChanges();
                }

            } 
            
            else if (sub_option == 5) {
                try (IDocumentSession session = store.openSession()){
                    reader.read(scanner, session, CategoryModel.class, "CategoryModels");
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

}
