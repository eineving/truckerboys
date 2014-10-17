package truckerboys.otto.driver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple sqlite db that stores the users sessions on the device.
 * Created by Martin on 13/10/2014.
 */
public class HistoryOpenHelper extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 3;
    // Database Name
    private static final String DATABASE_NAME = "HistoryDB";

    // Books table name
    private static final String TABLE_SESSIONS = "sessions";

    private static final String KEY_TYPE = "type";
    private static final String KEY_START = "start";
    private static final String KEY_END = "end";

    private static final String[] COLUMNS = {KEY_START,KEY_END, KEY_TYPE};


    public HistoryOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.w("DB" , "DB HELPER CLASS CONSTRUCTOR");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_HISTORY_TABLE = "CREATE TABLE "+ TABLE_SESSIONS + " ( " +
                KEY_TYPE + " TEXT,"+
                KEY_START + " BIGINT PRIMARY KEY, "+
                KEY_END +" BIGINT)";


        // create books table
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Overwrites a Session in the database.
     * @param session
     */
    public void overwriteSession(Session session){
        Log.w("DB","Session overwritten: " + session.toString());
        //TODO Nilsson Check what happens when to try to change non-existing values.
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, "" + session.getSessionType());
        values.put(KEY_END, "" + session.getEndTime().getMillis()); // get EndTime


        int result = db.update(TABLE_SESSIONS,
                values,
                KEY_START + " =  ?",
                new String[]{String.valueOf(session.getStartTime().getMillis())});

        //TODO Nilsson check for errors, research sqlite error codes...
        db.close();
    }

    /**
     * Adds a session to the Database.
     * @param session The session to add.
     */
    public void addSession(Session session){
        Log.w("DB","Session saved: " + session.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, "" + session.getSessionType());
        values.put(KEY_START, "" + session.getStartTime().getMillis());
        values.put(KEY_END, "" + session.getEndTime().getMillis());

        db.insert(TABLE_SESSIONS,
                null, // All columns
                values);

        db.close();
    }

    /**
     * Deletes a session from the Database.
     * @param session
     */
    public void deleteSession(Session session){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_SESSIONS,
                KEY_START + " = ?",
                new String[]{String.valueOf(session.getStartTime().getMillis())});
        db.close();
        Log.w("DB", "Deleted " + session);
    }

    /**
     * Returns a list of all the sessions in the database.
     * @return
     */
    public List<Session> getAllSessions() {
        Log.w("HISTORY", "READING SESSIONS");
        List<Session> sessions = new ArrayList<Session>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_SESSIONS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Session s = null;
        if (cursor.moveToFirst()) {
            do {
                String str = cursor.getString(0);

                if(str.equals("DRIVING")){
                    s = new Session(SessionType.DRIVING, new Instant(Long.parseLong(cursor.getString(1))), new Instant(Long.parseLong(cursor.getString(2))));
                }else if(str.equals("WORKING")){
                    s = new Session(SessionType.WORKING, new Instant(Long.parseLong(cursor.getString(1))), new Instant(Long.parseLong(cursor.getString(2))));
                }else{
                    s = new Session(SessionType.RESTING, new Instant(Long.parseLong(cursor.getString(1))), new Instant(Long.parseLong(cursor.getString(2))));
                }

                // Add book to books
                sessions.add(s);
            } while (cursor.moveToNext());
        }
        // return books
        return sessions;
    }
}
