package com.online.games2.app;


import java.util.Date;

import lombok.Data;

@Data
public class RatingModel {
    private String id;
    private String user_id;
    private String game_id;
    private int score;
    private Date created_at;
}
