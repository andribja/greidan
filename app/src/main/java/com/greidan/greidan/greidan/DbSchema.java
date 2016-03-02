package com.greidan.greidan.greidan;

/**
 * Created by Daníel on 01/03/2016.
 */
public class DbSchema {

    public static final String CREATE_TABLE_ADS =
            "CREATE TABLE " + AdTable.NAME + " (" +
                    AdTable.Cols.ID + " INTEGER PRIMARY KEY, " +
                    AdTable.Cols.TITLE + " TEXT, " +
                    AdTable.Cols.CONTENT + " TEXT, " +
                    AdTable.Cols.CATEGORY + " TEXT, " +
                    AdTable.Cols.AUTHOR_ID + " INTEGER, " +
                    AdTable.Cols.TIME_POSTED + " TEXT, " +
                    AdTable.Cols.ADDRESS + " STRING, " +
                    AdTable.Cols.LAT + " INTEGER, " +
                    AdTable.Cols.LNG + " INTEGER)";

    public static final String CREATE_TABLE_USERS = "CREATE TABLE " + UserTable.NAME + " (" +
                    UserTable.Cols.ID + " INTEGER PRIMARY KEY, " +
                    UserTable.Cols.USERNAME + " TEXT, " +
                    UserTable.Cols.EMAIL + " TEXT)";

    public static final String DROP_TABLE_ADS = "DROP TABLE IF EXISTS Ads";
    public static final String DROP_TABLE_USERS = "DROP TABLE IF EXISTS Users";

    public static final class AdTable {
        public static final String NAME = "Ads";
        public static final class Cols {
            public static final String ID = "ID";
            public static final String TITLE = "title";
            public static final String CONTENT = "content";
            public static final String CATEGORY= "category";
            public static final String AUTHOR_ID = "author_id";
            public static final String TIME_POSTED = "time_posted";
            public static final String ADDRESS = "address";
            public static final String LAT = "lat";
            public static final String LNG = "lng";
        }
    }

    public static final class UserTable {
        public static final String NAME = "Users";
        public static final class Cols {
            public static final String ID = "ID";
            public static final String USERNAME = "username";
            public static final String EMAIL = "email";
        }
    }
}
