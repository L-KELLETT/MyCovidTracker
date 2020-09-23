package com.lak021.mycovidtracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lak021.mycovidtracker.database.CaseDbSchema.CaseTable;

public class CaseDatabaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME ="caseDatabase.db";

    public CaseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CaseTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CaseTable.Cols.UUID + ", " +
                CaseTable.Cols.TITLE + ", " +
                CaseTable.Cols.DATE + ", " +
                CaseTable.Cols.CLOSE + ", " +
                CaseTable.Cols.CONTACT + ", " +
                CaseTable.Cols.DURATION +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
