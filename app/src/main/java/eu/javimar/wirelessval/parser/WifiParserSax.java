package eu.javimar.wirelessval.parser;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class WifiParserSax
{
    private InputStream mInputStream;
    private Context mContext;
    private static final String LOG_TAG = WifiParserSax.class.getName();

    public WifiParserSax(InputStream inputStream, Context context)
    {
        this.mContext = context;
        this.mInputStream= inputStream;
    }

    public List<ContentValues> parse()
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try
        {
            SAXParser parser = factory.newSAXParser();
            WifiHandler handler = new WifiHandler(mContext);
            parser.parse(mInputStream, handler);
            return handler.getWifis();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "Problem parsing wifis ", e);
            return null;
        }
    }

}