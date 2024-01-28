package com.online.games2.app;


import java.sql.Date;

import lombok.Data;


@Data
public class PurchaseModel {
    private String id;
    private String game_id;
    private String user_id;
    private String bankName;
    private long bankNumber;
    private Date created_at;
    private Double amount;
    private String currency;
}

