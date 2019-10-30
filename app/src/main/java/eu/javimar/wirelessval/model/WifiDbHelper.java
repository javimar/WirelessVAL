package eu.javimar.wirelessval.model;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import eu.javimar.wirelessval.model.WifiContract.WifiEntry;


public class WifiDbHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "wifi.db";
    /** If you change the database schema, you must increment the database version */
    private static final int DATABASE_VERSION = 1;

    public WifiDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Called the first time DB is created */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String SQL_CREATE_WIFI_TABLE = "CREATE TABLE " + WifiEntry.TABLE_NAME + " ( "
                + WifiEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WifiEntry.COLUMN_WIFI_NAME + " TEXT NOT NULL, "
                + WifiEntry.COLUMN_WIFI_LONGITUDE + " REAL, "
                + WifiEntry.COLUMN_WIFI_LATITUDE + " REAL, "
                + WifiEntry.COLUMN_WIFI_INFO + " TEXT, "
                + WifiEntry.COLUMN_WIFI_OPINION + " REAL DEFAULT 0 );";

        db.execSQL(SQL_CREATE_WIFI_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        db.execSQL(" DROP TABLE IF EXISTS " + WifiEntry.TABLE_NAME);
        onCreate(db);
    }
}
