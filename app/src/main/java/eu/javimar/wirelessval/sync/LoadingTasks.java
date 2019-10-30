package eu.javimar.wirelessval.sync;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import es.dmoral.toasty.Toasty;
import eu.javimar.wirelessval.R;
import eu.javimar.wirelessval.model.WifiContract;
import eu.javimar.wirelessval.parser.WifiParserSax;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


@SuppressWarnings("all")
public class LoadingTasks
{
    private static final String LOG_TAG = LoadingTasks.class.getSimpleName();

    /** ACTIONS TO PERFORM */
    public static final String ACTION_LOAD_WIFIS= "load_wifis";

    /** URL for the Ayto de Valencia web service for the list of the city Wireless spots */
    private static final String WIFI_URL = "http://mapas.valencia.es/lanzadera/opendata/wifi/KML";


    public static void executeTask(Context context, String action)
    {
        switch (action)
        {
            case ACTION_LOAD_WIFIS:
                loadWifis(context);
                break;
        }
    }


    private static void loadWifis(final Context context)
    {
        List<ContentValues> wifisCv = null;

        // establish connection with the server
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(WIFI_URL)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            // pass InputStream to the parser
            WifiParserSax saxparser = new WifiParserSax(response.body().byteStream(), context);
            wifisCv = saxparser.parse();

            // insert Content Values with full list of wifis into DB
            final int count = wifisCv.size();
            context.getContentResolver().bulkInsert(WifiContract.WifiEntry.CONTENT_URI,
                    wifisCv.toArray(new ContentValues[count]));

            // inform the user
            Handler h = new Handler(context.getMainLooper());
            // need this, to show a toast in this thread
            h.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (count != 0)
                    {
                        Toasty.info(context,
                                String.format(context.getString(R.string.number_wifis_loaded), count),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                    else
                    {
                        Toasty.info(context,
                                R.string.no_more_wifis,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "Problem accessing the server while loading wifis", e);
        }
    }
}
