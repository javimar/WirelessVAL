package eu.javimar.wirelessval.parser;

import android.content.Context;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import eu.javimar.wirelessval.model.Wifi;
import eu.javimar.wirelessval.utils.HelperUtils;


class WifiHandler extends DefaultHandler
{
    private Wifi currentWifi;
    private List<Wifi> wifis;
    private boolean isValues, isNombre = false;
    private StringBuilder sbCoordinates;
    private final Context mContext;

    WifiHandler(Context context)
    {
        this.mContext = context;
    }

    public List<Wifi> getWifis() {
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
            currentWifi = new Wifi();
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
                    currentWifi.setWifiName(new String(ch, start, length).trim());
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

                // check if wifi is already in DB checking its coordinates which must be unique
                if(HelperUtils.isWifiInDatabase(mContext, currentWifi.getWifiName(),
                        Double.parseDouble(array[1].trim()),
                        Double.parseDouble(array[0].trim())))
                {
                    // wifi found, don't include it in the array
                    currentWifi = null;
                }
                else
                {
                    currentWifi.setLatitude(Double.parseDouble(array[1].trim()));
                    currentWifi.setLongitude(Double.parseDouble(array[0].trim()));
                }
            }
            else if (localName.equalsIgnoreCase("Placemark"))
            {
                // Placemark finish a wifi and add it to List
                currentWifi.setOpinion(0);
                wifis.add(currentWifi);
            }
            sbCoordinates.setLength(0);
        }
    }
}