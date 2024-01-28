package com.online.games2.app;


import java.sql.Date;

import lombok.Data;

@Data
public class CommentModel {
    private String id;
    private String game_id;
    private String user_id;
    private String comment;
    private Date created_at;
}
