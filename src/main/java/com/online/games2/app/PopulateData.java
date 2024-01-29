package com.online.games2.app;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import javax.print.Doc;

import net.ravendb.client.documents.session.IDocumentSession;


public class PopulateData {
    private final List<String> gameNames = Arrays.asList(
        "Batman: Arkham City",
        "The Witcher 3: Wild Hunt",
        "The Elder Scrolls V: Skyrim",
        "Grand Theft Auto V",
        "The Last of Us",
        "Red Dead Redemption 2",
        "The Legend of Zelda: Breath of the Wild",
        "God of War",
        "Mass Effect 2",
        "Portal 2",
        "BioShock",
        "Uncharted 2: Among Thieves",
        "Super Mario Galaxy 2",
        "Metal Gear Solid V: The Phantom Pain",
        "The Legend of Zelda: Ocarina of Time",
        "Super Mario Galaxy",
        "Red Dead Redemption",
        "Grand Theft Auto IV",
        "God of War II",
        "The Orange Box",
        "God of War III",
        "The Legend of Zelda: The Wind Waker",
        "Mass Effect 3",
        "Uncharted 4: A Thief's End",
        "Super Mario Odyssey",
        "The Last of Us Remastered",
        "The Elder Scrolls IV: Oblivion",
        "Halo 3",
        "Batman: Arkham Asylum",
        "Metal Gear Solid 4: Guns of the Patriots",
        "The Legend of Zelda: A Link to the Past",
        "Super Mario 3D World",
        "BioShock Infinite",
        "Half-Life 2",
        "Uncharted 3: Drake's Deception",
        "The Legend of Zelda: Majora's Mask",
        "Super Mario World",
        "The Legend of Zelda: Twilight Princess",
        "Halo: Reach",
        "Super Mario 64",
        "The Legend of Zelda: Skyward Sword",
        "Grand Theft Auto: San Andreas",
        "Metal Gear Solid",
        "Mass Effect",
        "The Legend of Zelda: A Link Between Worlds",
        "The Elder Scrolls III: Morrowind",
        "Uncharted: Drake's Fortune",
        "Super Mario Bros. 3",
        "The Legend of Zelda: The Minish Cap",
        "The Legend of Zelda: The Wind Waker HD",
        "The Last of Us Part II",
        "Spiderman",
        "Warface",
        "Counter-Strike: Global Offensive",
        "Dota 2",
        "Apex Legends",
        "Fortnite",
        "League of Legends",
        "Valorant",
        "Overwatch",
        "Call of Duty: Warzone",
        "PlayerUnknown's Battlegrounds",
        "Hearthstone",
        "World of Warcraft",
        "Minecraft",
        "Grand Theft Auto V",
        "Red Dead Redemption 2",
        "Delphinus",
        "Swordfish",
        "Shark Attack",
        "Mortal Kombat",
        "Street Fighter",
        "Tekken",
        "Fight Club",
        "Double Dragon",
        "The King of Fighters",
        "Fatal Fury",
        "Virtua Fighter",
        "Soulcalibur",
        "Dead or Alive",
        "Guilty Gear",
        "Darkstalkers",
        "Marvel vs. Capcom",
        "Injustice",
        "BlazBlue",
        "World of Tanks",
        "World of Warships",
        "World of Warplanes",
        "War Thunder",
        "Armored Warfare",
        "Crossout",
        "Star Conflict",
        "Star Wars: The Old Republic",
        "Star Wars: Squadrons",
        "Star Wars: Battlefront",
        "Star Wars: Jedi Fallen Order",
        "Star Wars: The Force Unleashed",
        "Alone in the Dark",
        "Amnesia: The Dark Descent",
        "Alan Wake",
        "Alien: Isolation",
        "BioShock",
        "Bloodborne",
        "Call of Cthulhu: Dark Corners of the Earth",
        "Condemned: Criminal Origins",
        "Cry of Fear",
        "Cryostasis: Sleep of Reason",
        "Dark Seed",
        "Dead Space",
        "Deadly Premonition",
        "Dementium: The Ward",
        "Dino Crisis",
        "Doom 3",
        "Eternal Darkness: Sanity's Requiem",
        "F.E.A.R.",
        "Fatal Frame",
        "Forbidden Siren",
        "Haunting Ground",
        "Hellblade: Senua's Sacrifice"
    );
        private final List<String> bankNames = Arrays.asList(
            "Bank of America",
            "JPMorgan Chase",
            "Wells Fargo",
            "Citigroup",
            "Goldman Sachs",
            "Morgan Stanley",
            "HSBC",
            "Barclays",
            "Royal Bank of Canada",
            "BNP Paribas"
        );
        private final List<String> categoryNames = Arrays.asList(
            "Action",
            "Adventure",
            "Role-playing",
            "Simulation",
            "Strategy",
            "Sports",
            "Puzzle",
            "Idle",
            "Arcade",
            "Board"
        );
        private final List<Double> prices = Arrays.asList(
            19.99,
            29.99,
            39.99,
            49.99,
            59.99
        );

    public void createMock(IDocumentSession session) {
        
        this.createMockCategory(session);
        // this.createMockUser(session);
    }

    public void createMockCategory(IDocumentSession session) {
        int size = categoryNames.size();
        List <CategoryModel> categories = new ArrayList<CategoryModel>();
        for (int i = 0; i < size; i++){
            String name = categoryNames.get(i);
            CategoryModel category = new CategoryModel();
            category.setName(name);
            session.store(category);
            session.saveChanges();
            categories.add(category);
        }
 
       
        this.createMockGame(session, categories);
    }

    public void createMockGame(IDocumentSession session, List<CategoryModel> categories) {
        Faker faker = new Faker();
        List <Document> games = new ArrayList<Document>();
        for (Document category : categories) {
                for (int i = 0; i < 10; i++) {
                    String name = gameNames.get(faker.random().nextInt(gameNames.size()));
                    Double price = prices.get(faker.random().nextInt(prices.size()));
                    Integer age_restriction = faker.number().numberBetween(9, 18);
                   
                    Document game = new Document().append("name", name).append("category", 
                    category).append("price", price)
                            .append("age_restriction", age_restriction).append("total", 0);
                    games.add(game);
                }
        }
 
        session.getCollection("games").createIndex(
            new Document("name", 1).append("_id", 1),
            new IndexOptions().unique(true));
        session.getCollection("games").insertMany(games);
    }

    public void createMockUser(IDocumentSession session) {
        List <Document> users = new ArrayList<Document>();
        
        for (int i = 0; i < 50; i++) {
            Faker faker = new Faker();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String email = faker.internet().emailAddress();
            String userName = faker.name().username();
            String password = faker.internet().password();
            Integer age = faker.number().numberBetween(14, 55);
            Date date = new Date();
            Document user = new Document().append("firstname",
             firstName).append("lastname", lastName).append("email", email).append("age", age)
                    .append("comments", new ArrayList<ObjectId>())
                    .append("ratings", new ArrayList<ObjectId>())
                    .append("purchases", new ArrayList<ObjectId>())
                    .append("username", userName).append("password", password).append("created_at", date);
            users.add(user);
        }
        session.getCollection("users").createIndex(
                new Document("username", 1).append("email", 1).append("_id", 1), 
                new IndexOptions().unique(true));

        session.getCollection("users").insertMany(users);
        List <Document> games = session.getCollection("games").find().into(new ArrayList<Document>());
        this.createMockPurchase(session, users, games);
        this.createMockComment(session, users, games);
        this.createMockRating(session, users, games);
    }

    public void createMockPurchase(IDocumentSession session, List<Document> users, List<Document> games) {
        List <Document> purchases = new ArrayList<Document>();

        for (int i = 0; i < 50; i++) {
            Faker faker = new Faker();
            String bankName = bankNames.get(faker.random().nextInt(bankNames.size()));
            Integer bankNumber = faker.number().numberBetween(100000000, 999999999);
            Double amout = prices.get(faker.random().nextInt(prices.size()));
            String currency = "EUR";
            Document purchase = new Document();
            purchase.append("game_id", users.get(faker.random().nextInt(users.size())).getObjectId("_id"));
            purchase.append("created_at", new Date());
            purchase.append("bank", new Document().append("name", bankName).append("number", bankNumber));
            purchase.append("amount", amout)
            .append("currency", currency);
            purchases.add(purchase);
        }   
        session.getCollection("purchases").insertMany(purchases);
        pushPurchaseIntoUSer(session, users, purchases);
    }

    public void pushPurchaseIntoUSer(IDocumentSession session, List<Document> users, List<Document> purchases) {
        for (Document user : users) {
            Faker faker = new Faker();
            ObjectId userId = user.getObjectId("_id"); 
            ObjectId purchaseId = purchases.get(faker.random().nextInt(purchases.size())).getObjectId("_id");
            Bson filter = Filters.eq("_id", userId);
            Bson push = Updates.push("purchases", purchaseId);
            session.getCollection("users").updateOne(filter, push);
        }
    }   

    public void createMockComment(IDocumentSession session, List<Document> users, List<Document> games) {
        List <Document> comments = new ArrayList<Document>();

        for (int i = 0; i < 50; i++) {
            Faker faker = new Faker();
            String comment = faker.lorem().sentence();
            Document commentDoc = new Document();
            commentDoc.append("created_at", new Date());
            commentDoc.append("comment", comment);
            commentDoc.append("game_id", users.get(faker.random().nextInt(users.size())).getObjectId("_id"));
            comments.add(commentDoc);
        }  

        session.getCollection("comments").insertMany(comments);
        pushCommentIntoUSerAndGame(session, users, comments, games);
    }
    public void pushCommentIntoUSerAndGame(IDocumentSession session, List<Document> users, List<Document> comments, List<Document> games) {
        for (Document user : users) {
            Faker faker = new Faker();
            ObjectId userId = user.getObjectId("_id"); 
            ObjectId commentId = comments.get(faker.random().nextInt(comments.size())).getObjectId("_id");
            Bson filter = Filters.eq("_id", userId);
            Bson push = Updates.push("comments", commentId);
            session.getCollection("users").updateOne(filter, push);
        }

        for (Document game : games) {
            Faker faker = new Faker();
            ObjectId gameId = game.getObjectId("_id"); 
            ObjectId commentId = comments.get(faker.random().nextInt(comments.size())).getObjectId("_id");
            Bson filter = Filters.eq("_id", gameId);
            Bson push = Updates.push("comments", commentId);
            
            session.getCollection("games").updateOne(filter, push);
        }
    }   

    public void createMockRating(IDocumentSession session, List<Document> users, List<Document> games) {
        List <Document> ratings = new ArrayList<Document>();

        for (int i = 0; i < 50; i++) {
            Faker faker = new Faker();
            Integer rating = faker.number().numberBetween(1, 5);
            Document ratingDoc = new Document();
            ratingDoc.append("created_at", new Date());
            ratingDoc.append("rating", rating);
            ratingDoc.append("game_id", users.get(faker.random().nextInt(users.size())).getObjectId("_id"));
            ratings.add(ratingDoc);
        } 

        session.getCollection("ratings").insertMany(ratings);
        pushRatingIntoUSer(session, users, ratings);
    }

    public void pushRatingIntoUSer(IDocumentSession session, List<Document> users, List<Document> ratings) {
        for (Document user : users) {
            Faker faker = new Faker();
            ObjectId userId = user.getObjectId("_id"); 
            ObjectId ratingId = ratings.get(faker.random().nextInt(ratings.size())).getObjectId("_id");
            Bson filter = Filters.eq("_id", userId);
            Bson push = Updates.push("ratings", ratingId);
            session.getCollection("users").updateOne(filter, push);
        }
    }   


}