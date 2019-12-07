package eu.javimar.wirelessval.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import eu.javimar.wirelessval.model.Wifi;

@Database(entities = { Wifi.class }, version = 2, exportSchema = false )
public abstract class WifiDatabase extends RoomDatabase
{
    public abstract WifisDao wifisDao();

    // Make the DB a singleton to prevent having multiple instances of the database opened at the same time.
    private static volatile WifiDatabase INSTANCE;

    public static WifiDatabase getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (WifiDatabase.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WifiDatabase.class, "wifis_vlc.db")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Migrate from:
     * version 1 - using SQL
     * to
     * version 2 - using Room
     */
    @VisibleForTesting
    private static final Migration MIGRATION_1_2 = new Migration(1, 2)
    {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database)
        {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

}
