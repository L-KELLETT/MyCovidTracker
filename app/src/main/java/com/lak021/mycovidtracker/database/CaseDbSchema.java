package com.lak021.mycovidtracker.database;

public class CaseDbSchema {
    public static final class CaseTable {
        public static final String NAME = "cases";
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String CLOSE = "hadcontact";
            public static final String CONTACT = "contacts";
            public static final String DURATION = "duration";

        }
    }
}
