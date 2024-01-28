package com.online.games2.app;


import java.sql.Date;

import lombok.Data;

@Data
public class RatingModel {
    private String id;
    private String game_id;
    private String user_id;
    private int rating;
    private Date date;
}
