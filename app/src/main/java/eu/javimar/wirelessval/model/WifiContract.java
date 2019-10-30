package eu.javimar.wirelessval.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

@SuppressWarnings("All")
public final class WifiContract
{
    private WifiContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "eu.javimar.wirelessval";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    // To make this a usable URI, we use the parse method which takes in a URI string
    // and returns a Uri
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://eu.javimar.wirelessvlc/wifis/ is a valid path for
     * looking at wifi data. content://eu.javimar.wirelessvlc/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_WIFIS = "wifis";



    /**
     * Inner class that defines the table of wireless networks. One per table
     */
    public static class WifiEntry implements BaseColumns
    {
        /** The MIME type of the #CONTENT_URI for a list of wifis */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WIFIS;

        /** The MIME type of the #CONTENT_URI for a single wifi */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WIFIS;

        /** The content URI to access the wifi data in the provider */
        // Uri.withAppendedPath() method appends the BASE_CONTENT_URI (which contains
        // the scheme and the content authority) to the path segment.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WIFIS);

        /** Name of database table for wifis */
        public static final String TABLE_NAME = "wifis";

        /**
         * Unique ID number for the pet (only for use in the database table).
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_WIFI_NAME = "name";
        public static final String COLUMN_WIFI_LATITUDE = "latitude";
        public static final String COLUMN_WIFI_LONGITUDE = "longitude";
        public static final String COLUMN_WIFI_INFO = "comments";
        public static final String COLUMN_WIFI_OPINION = "opinion";
    }
}
