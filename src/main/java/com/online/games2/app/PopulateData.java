package com.online.games2.app;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import javax.print.Doc;

import org.checkerframework.checker.units.qual.s;

import com.github.javafaker.Faker;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.operations.DeleteByQueryOperation;
import net.ravendb.client.documents.queries.IndexQuery;
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

    public void createMock(DocumentStore store) {

        try (IDocumentSession session = store.openSession()) { 
            this.createMockCategory(session);
        }
        try (IDocumentSession session = store.openSession()) { 
            this.createMockUser(session);
        }
     
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
        List <GameModel> games = new ArrayList<GameModel>();
        for (CategoryModel category : categories) {
                for (int i = 0; i < 10; i++) {
                    String name = gameNames.get(faker.random().nextInt(gameNames.size()));
                    Double price = prices.get(faker.random().nextInt(prices.size()));
                    Integer age_restriction = faker.number().numberBetween(9, 18);
                   
                    GameModel game = new GameModel();
                    game.setName(name);
                    game.setPrice(price);
                    game.setAgeRestriction(age_restriction);
                    game.setCategory(category);
                    session.store(game);
            
                    games.add(game);
                }
                session.saveChanges();
        }
 
    }

    public void createMockUser(IDocumentSession session) {
        List <UserModel> users = new ArrayList<UserModel>();
        
        for (int i = 0; i < 1000; i++) {
            Faker faker = new Faker();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String email = faker.internet().emailAddress();
            String userName = faker.name().username();
            String password = faker.internet().password();
            Integer age = faker.number().numberBetween(14, 55);
            UserModel user = new UserModel();
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setEmail(email);
            user.setAge(age);
            user.setUsername(userName);
            user.setPassword(password);
            user.setCreated_at(new Date());
            session.store(user);
            users.add(user);
        }
        session.saveChanges();
        List <GameModel> games = session.query(GameModel.class).toList();
        this.createMockPurchase(session, users, games);
        this.createMockComment(session, users, games);
        this.createMockRating(session, users, games);
    }

    public void createMockPurchase(IDocumentSession session, List<UserModel> users, List<GameModel> games) {
        List <PurchaseModel> purchases = new ArrayList<PurchaseModel>();

        for (int i = 0; i < 1000; i++) {
            Faker faker = new Faker();
            String bankName = bankNames.get(faker.random().nextInt(bankNames.size()));
            Long bankNumber = (long) faker.number().numberBetween(100000000, 999999999);
            Double amout = prices.get(faker.random().nextInt(prices.size()));
            String currency = "EUR";
            PurchaseModel purchase = new PurchaseModel();
            BankModel bank = new BankModel();
            bank.setName(bankName);
            bank.setNumber(bankNumber);
            purchase.setBank(bank);
            purchase.setAmount(amout);
            purchase.setCurrency(currency);
            purchase.setCreated_at(new Date());
            purchase.setUser_id(users.get(faker.random().nextInt(users.size())).getId());
            purchase.setGame_id(games.get(faker.random().nextInt(games.size())).getId());

            session.store(purchase);
            purchases.add(purchase);
        }   
        session.saveChanges();
    }


    public void createMockComment(IDocumentSession session, List<UserModel> users, List<GameModel> games) {
        List <CommentModel> comments = new ArrayList<CommentModel>();

        for (int i = 0; i < 1000; i++) {
            Faker faker = new Faker();
            String comment = faker.lorem().sentence();
            CommentModel commentDoc = new CommentModel();
            commentDoc.setCreated_at(new Date());
            commentDoc.setText(comment);
            commentDoc.setUser_id(users.get(faker.random().nextInt(users.size())).getId());
            commentDoc.setGame_id(games.get(faker.random().nextInt(games.size())).getId());
            comments.add(commentDoc);
            session.store(commentDoc);
        }  
        session.saveChanges();

    }


    public void createMockRating(IDocumentSession session, List<UserModel> users, List<GameModel> games) {
        List <RatingModel> ratings = new ArrayList<RatingModel>();

        for (int i = 0; i < 1000; i++) {
            Faker faker = new Faker();
            Integer rating = faker.number().numberBetween(1, 5);
            RatingModel ratingDoc = new RatingModel();
            ratingDoc.setRating(rating);
            ratingDoc.setUser_id(users.get(faker.random().nextInt(users.size())).getId());
            ratingDoc.setGame_id(games.get(faker.random().nextInt(games.size())).getId());
            session.store(ratingDoc);
            ratings.add(ratingDoc);
        } 

        session.saveChanges();
    }

}