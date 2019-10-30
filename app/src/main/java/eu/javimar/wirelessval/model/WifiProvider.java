package eu.javimar.wirelessval.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;

import eu.javimar.wirelessval.model.WifiContract.WifiEntry;

public class WifiProvider extends ContentProvider
{
    public static final String LOG_TAG = WifiProvider.class.getName();

    /** URI matcher code for the content URI for the wifi table
     *  content://eu.javimar.wirelessvlc/wifis --> WIFIS case, code 100
     */
    private static final int WIFIS = 100;

    /** URI matcher code for the content URI for a single wifi in the wifi table
     *  content://eu.javimar.wirelessvlc/wifis/#  --> WIFI_ID case, code 101
     *  # can be replaced with any INT
     */
    private static final int WIFI_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    // it initializes our global sUriMatcher
    static
    {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://eu.javimar.wirelessvlc/wifis" will map to the
        // integer code #WIFIS. This URI is used to provide access to MULTIPLE rows
        // of the wifis table.
        sUriMatcher.addURI(WifiContract.CONTENT_AUTHORITY, WifiContract.PATH_WIFIS, WIFIS);

        // The content URI of the form "content://eu.javimar.wirelessvlc/wifis/#" will map to the
        // integer code #WIFI_ID. This URI is used to provide access to ONE single row
        // of the wifi table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://eu.javimar.wirelessvlc/wifis/3" matches, but
        // "content://eu.javimar.wirelessvlc/wifis" (without a number at the end) doesn't match.
        sUriMatcher.addURI(WifiContract.CONTENT_AUTHORITY, WifiContract.PATH_WIFIS + "/#", WIFI_ID);
    }

    /** Database helper object */
    private WifiDbHelper mDbHelper;


    @Override
    public boolean onCreate()
    {
        mDbHelper = new WifiDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match)
        {
            case WIFIS:
                // For the WIFIS code, query the wifi table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(WifiEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case WIFI_ID:
                // For the WIFI_ID code, extract out the ID from the URI.
                // For an example URI such as "content://eu.javimar.wirelessvlc/wifis/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = WifiEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table WHERE _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(WifiEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query, unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }



    @Override
    public int update(Uri uri, ContentValues contentValues,
                      String selection, String[] selectionArgs)
    {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            // update multiple rows
            case WIFIS:
                return updateWifi(uri, contentValues, selection, selectionArgs);
            // update only one row
            case WIFI_ID:
                // For the WIFI_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = WifiEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateWifi(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    /**
     * Update wifis in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more wifis).
     * Return the number of rows that were successfully updated.
     */
    private int updateWifi(Uri uri, ContentValues values,
                          String selection, String[] selectionArgs)
    {
        // If the COLUMN_WIFI_NAME key is present, check that the name value is not null.
        // Because not all attributes will be present on an update, ContentValues containsKey()
        // method to check whether a key/value pair exists, before trying to check if it has a
        // reasonable value.
        if (values.containsKey(WifiEntry.COLUMN_WIFI_NAME))
        {
            String name = values.getAsString(WifiEntry.COLUMN_WIFI_NAME);
            if (name == null)
            {
                throw new IllegalArgumentException("WiFi network requires a name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0)
        {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(WifiEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case WIFIS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(WifiEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case WIFI_ID:
                // Delete a single row given by the ID in the URI
                selection = WifiEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(WifiEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    /** The purpose of this method is to return a String that describes the type of the data
     *  stored at the input Uri. This String is known as the MIME type, which can also be
     *  referred to as content type
     */
    @Override
    public String getType(Uri uri)
    {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case WIFIS:
                return WifiEntry.CONTENT_LIST_TYPE;
            case WIFI_ID:
                return WifiEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }



    /**
     * Handles requests to insert a set of new rows. In Fallas, we are only going to be
     * inserting multiple rows of data at a time. There is no use case
     * for inserting a single row of data into our ContentProvider, and so we are only going to
     * implement bulkInsert. In a normal ContentProvider's implementation, you will probably want
     * to provide proper functionality for the insert method as well.
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values)
    {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri))
        {
            // only FALLAS is supported obviously, insert into DB
            case WIFIS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values)
                    {
                        // Check that the name is not null
                        String name = value.getAsString(WifiEntry.COLUMN_WIFI_NAME);
                        if (name == null)
                        {
                            throw new IllegalArgumentException("Wifi requires a name");
                        }
                        long _id = db.insert(WifiEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                        {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally
                {
                    db.endTransaction();
                }
                if (rowsInserted > 0)
                {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                // Return the number of rows inserted from our implementation of bulkInsert
                return rowsInserted;
            default:
                // If the URI does match match WIFIS, return the super implementation of bulkInsert
                return super.bulkInsert(uri, values);
        }
    }



    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new RuntimeException(
                "Not implemented in Wireless Valencia. Use bulkInsert instead");
    }

}
