package com.online.games2.app;


import lombok.Data;

@Data
public class GameModel {
    private String id;
    private String name;
    private String description;
    private CategoryModel category;
    private Double price;
    private int ageRestriction;
    private int total;
}
