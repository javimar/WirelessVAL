package eu.javimar.wirelessval.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "wifis", primaryKeys = {"wifiName", "latitude", "longitude"})
public class Wifi
{
    @NonNull
    @ColumnInfo(name = "wifiName")
    private String wifiName;

    @NonNull
    @ColumnInfo(name = "latitude")
    private double latitude;

    @NonNull
    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "comments")
    private String comments;

    @ColumnInfo(name = "opinion")
    private float opinion = 0;

    public Wifi(@NonNull String wifiName, @NonNull double latitude, @NonNull double longitude,
                String comments, float opinion)
    {
        this.wifiName = wifiName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comments = comments;
        this.opinion = opinion;
    }

    @Ignore
    public Wifi() {}

    @NonNull
    public String getWifiName() {
        return wifiName;
    }
    public void setWifiName(@NonNull String wifiName) {
        this.wifiName = wifiName;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String getComments() {
        return comments;
    }
    public float getOpinion() {
        return opinion;
    }
    public void setOpinion(float opinion) { this.opinion = opinion; }
}
