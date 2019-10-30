package eu.javimar.wirelessval.parser;

import android.content.ContentValues;
import android.content.Context;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import eu.javimar.wirelessval.utils.HelperUtils;
import eu.javimar.wirelessval.model.WifiContract.WifiEntry;

@SuppressWarnings("all")
public class WifiHandler extends DefaultHandler
{
    private ContentValues currentWifi;
    private List<ContentValues> wifis;
    private boolean isValues, isNombre = false;
    private StringBuilder sbCoordinates;
    private Context mContext;


    public WifiHandler(Context context)
    {
        this.mContext = context;
    }


    public List<ContentValues> getWifis() {
        return wifis;
    }

    @Override
    public void startDocument() throws SAXException
    {
        super.startDocument();
        wifis = new ArrayList<>();
        sbCoordinates = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException
    {
        super.startElement(uri, localName, name, attributes);

        if (localName.equalsIgnoreCase("Placemark"))
        {
            currentWifi = new ContentValues();
        }
        else if (localName.equalsIgnoreCase("Data"))
        {
            String dataName = attributes.getValue("name");
            if (dataName.equalsIgnoreCase("descripcion"))
            {
                isNombre = true;
            }
        }
        else if (localName.equalsIgnoreCase("value"))
        {
            isValues = true;
        }
    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        super.characters(ch, start, length);
        if (currentWifi != null)
        {
            sbCoordinates.append(ch, start, length);
            if (isValues)
            {
                if (isNombre)
                {
                    currentWifi.put(WifiEntry.COLUMN_WIFI_NAME,
                            new String(ch, start, length).trim());
                    isNombre = false;
                }
                isValues = false;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException
    {
        super.endElement(uri, localName, name);

        if (currentWifi != null)
        {
            if (localName.equalsIgnoreCase("coordinates"))
            {
                String[] array = sbCoordinates.toString().trim().split(",");

                // check if wifi is already in DB
                if (HelperUtils.wifiInDatabase(array[1].trim(), array[0].trim(), mContext))
                {
                    // wifi found, don't include it in the ContentValues array
                    currentWifi.clear();
                }
                else
                {
                    currentWifi.put(WifiEntry.COLUMN_WIFI_LATITUDE, array[1].trim());
                    currentWifi.put(WifiEntry.COLUMN_WIFI_LONGITUDE, array[0].trim());
                }
            }
            else if (localName.equalsIgnoreCase("Placemark"))
            {
                // if we have an empty CV, skip it since wifi was already in DB
                if (currentWifi.size() != 0)
                    // Placemark finish a wifi and add it to List
                    wifis.add(currentWifi);
            }
            sbCoordinates.setLength(0);
        }
    }


}
