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
        // not implemented.
    }



    public void addSession(Session session){
        //for logging
        Log.d("Sessions saved", session.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, "" + session.getSessionType());
        values.put(KEY_START, "" + session.getStartTime()); //get StartTime
        values.put(KEY_END, "" + session.getEndTime()); // get EndTime

        // 3. insert
        db.insert(TABLE_SESSIONS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

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
