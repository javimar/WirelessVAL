package eu.javimar.wirelessval.sync;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import es.dmoral.toasty.Toasty;
import eu.javimar.wirelessval.R;
import eu.javimar.wirelessval.model.WifiContract;
import eu.javimar.wirelessval.parser.WifiParserSax;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static eu.javimar.wirelessval.utils.HelperUtils.stripNonValidXMLCharacters;


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
        final boolean failToLoad;

        // establish connection with the server
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(WIFI_URL)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            // remove illegal characters from the XML file
            String goodXml = stripNonValidXMLCharacters(response.body().string());
            // convert "Clean String" into an InputStream so it can be fed into the parser
            InputStream is = new ByteArrayInputStream(goodXml.getBytes());

            WifiParserSax saxparser = new WifiParserSax(is, context);
            wifisCv = saxparser.parse();

            if(wifisCv != null)
            {
                // insert Content Values with full list of wifis into DB
                context.getContentResolver().bulkInsert(WifiContract.WifiEntry.CONTENT_URI,
                        wifisCv.toArray(new ContentValues[wifisCv.size()]));

                failToLoad = false;
            }
            else
            {
                failToLoad = true;
            }
            // inform the user
            Handler h = new Handler(context.getMainLooper());
            // need this, to show a toast in this thread
            List<ContentValues> finalWifisCv = wifisCv;
            h.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (!failToLoad)
                    {
                        final int count = finalWifisCv.size();
                        if(count > 0)
                        {
                            Toasty.success(context,
                                    String.format(context.getString(R.string.number_wifis_loaded),
                                            count),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                        else
                        {
                            Toasty.info(context, R.string.no_more_wifis,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                    else
                    {
                        Toasty.error(context, R.string.err_loading_wifis,
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
