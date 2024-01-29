package com.online.games2.app;


import java.sql.Date;
import java.util.ArrayList;

import lombok.Data;

@Data
public class UserModel {
    private String id;
    private String firstname;
    private String lastname;
    private int age;
    private String username;
    private String email;
    private String password;
    private Date created_at;
    private ArrayList<CommentModel> comments;
    private ArrayList<RatingModel> ratings;
    private ArrayList<PurchaseModel> purchases;
}
