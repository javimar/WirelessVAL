package eu.javimar.wirelessval.utils;

public class GeoPoint
{
    private double longitud, latitude;

    public GeoPoint(double longitud, double latitude)
    {
        this.longitud= longitud;
        this.latitude = latitude;
    }
    public double distance(GeoPoint point)
    {
        final double EARTH_RADIUS = 6371000; // en metros
        double dLat = Math.toRadians(latitude - point.latitude);
        double dLon = Math.toRadians(longitud - point.longitud);
        double lat1 = Math.toRadians(point.latitude);
        double lat2 = Math.toRadians(latitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) *
                        Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return c * EARTH_RADIUS;
    }

    public double getLongitude() {return longitud;}
    public void setLongitude(double longitud) {
        this.longitud = longitud;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString()
    {
        if (hasCoordinates())
        {
            // solo muestro las primeras posiciones, pero almaceno en BBDD el número entero
            return ("Latitude: " + String.valueOf(latitude).substring(0, 6) +
                    " Longitude: " + String.valueOf(longitud).substring(0, 6));
        }
        else {
            return ("Lat: " + latitude + ",  Lng: " + longitud);
        }
    }

    private boolean hasCoordinates()
    {
        return !(latitude == 0 && longitud == 0);
    }

    /** Necessary methods to support GeoPoint as a HashMap key to reflect "equality" of two objects.*/
    @Override
    public int hashCode()
    {
        return (int)((Math.abs(longitud + latitude)));
    }

    @Override
    public boolean equals(Object geo)
    {
        if(!(geo instanceof GeoPoint))
            return false;

        GeoPoint g = (GeoPoint) geo;
        return (g.latitude == this.latitude) && (g.longitud == this.longitud);
    }
}
