package com.lak021.mycovidtracker.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.lak021.mycovidtracker.Case;
import com.lak021.mycovidtracker.database.CaseDbSchema.CaseTable;

import java.util.Date;
import java.util.UUID;

public class CaseCursorWrapper extends CursorWrapper {
    public CaseCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Case getCase() {
        String uuidString = getString(getColumnIndex(CaseTable.Cols.UUID));
        String title = getString(getColumnIndex(CaseTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CaseTable.Cols.DATE));
        int closeContact = getInt(getColumnIndex(CaseTable.Cols.CLOSE));
        String contacts = getString(getColumnIndex(CaseTable.Cols.CONTACT));
        String duration = getString(getColumnIndex(CaseTable.Cols.DURATION));
        double latitude = getDouble(getColumnIndex(CaseTable.Cols.LATITUDE));
        double longitude = getDouble(getColumnIndex(CaseTable.Cols.LONGITUDE));

        Case aCase = new Case(UUID.fromString(uuidString));
        aCase.setTitle(title);
        aCase.setDate(new Date(date));
        aCase.setWasCloseContact(closeContact != 0);
        aCase.setContacts(contacts);
        aCase.setDuration(duration);
        aCase.setLatitude(latitude);
        aCase.setLongitude(longitude);

        return aCase;
    }
}