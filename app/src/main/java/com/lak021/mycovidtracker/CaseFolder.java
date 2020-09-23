package com.lak021.mycovidtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lak021.mycovidtracker.database.CaseDatabaseHelper;
import com.lak021.mycovidtracker.database.CaseCursorWrapper;
import com.lak021.mycovidtracker.database.CaseDbSchema.CaseTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CaseFolder {
    private static CaseFolder sCaseFolder;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CaseFolder get(Context context) {
        if (sCaseFolder == null) {
            sCaseFolder = new CaseFolder(context);
        }
        return sCaseFolder;
    }

    private CaseFolder(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CaseDatabaseHelper(mContext).getWritableDatabase();

    }

    public void addCase(Case c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CaseTable.NAME, null, values);
    }

    public void deleteCase(Case c) {
        ContentValues values = getContentValues(c);
        mDatabase.delete(CaseTable.NAME, CaseTable.Cols.UUID + "=?", new String[]{c.getID().toString()});
    }

    public void deleteCases() {
        String clearDB = "DELETE FROM " + CaseTable.NAME;
        mDatabase.execSQL(clearDB);
    }

    public List<Case> getCases() {
        List<Case> aCases = new ArrayList<>();
        CaseCursorWrapper cursor = queryCases(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                aCases.add(cursor.getCase());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return aCases;
    }

    public Case getCase(UUID id) {
        CaseCursorWrapper cursor = queryCases(
                CaseTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCase();
        } finally {
            cursor.close();
        }
    }



    public void updateCase(Case aCase) {
        String uuidString = aCase.getID().toString();
        ContentValues values = getContentValues(aCase);
        mDatabase.update(CaseTable.NAME, values,
                CaseTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    private CaseCursorWrapper queryCases(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CaseTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new CaseCursorWrapper(cursor);
    }

    public File getPhotoFile(Case aCase) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, aCase.getPhotoFilename());
    }


    private static ContentValues getContentValues(Case aCase) {
        ContentValues values = new ContentValues();
        values.put(CaseTable.Cols.UUID, aCase.getID().toString());
        values.put(CaseTable.Cols.TITLE, aCase.getTitle());
        values.put(CaseTable.Cols.DATE, aCase.getDate().getTime());
        values.put(CaseTable.Cols.CLOSE, aCase.isWasCloseContact() ? 1 : 0);
        values.put(CaseTable.Cols.CONTACT, aCase.getContacts());
        return values;
    }
}
