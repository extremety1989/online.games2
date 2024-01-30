package com.online.games2.app;


import java.util.ArrayList;

import lombok.Data;

@Data
public class GameModel {
    private String id;
    private String name;
    private CategoryModel category;
    private Double price;
    private int ageRestriction;
    private int total;
    private ArrayList<String> comments;
    private ArrayList<String> ratings;
}
