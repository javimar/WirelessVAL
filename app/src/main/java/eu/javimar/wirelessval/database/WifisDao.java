package eu.javimar.wirelessval.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;

import java.util.List;

import eu.javimar.wirelessval.model.Wifi;

@Dao
public interface WifisDao
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertWifis(List<Wifi> wifis);

    @Query("SELECT * FROM wifis ORDER BY wifiName ASC")
    LiveData<List<Wifi>> getAllWifisByName();

    @Query("SELECT * FROM wifis ORDER BY opinion DESC")
    LiveData<List<Wifi>> getAllWifisByOpinion();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT wifiName, latitude, longitude, comments, opinion, " +
            "MAX (latitude, :currentLat) - MIN (latitude, :currentLat) + " +
            "MAX (longitude, :currentLng ) - MIN (longitude, :currentLng) AS distance " +
            " FROM wifis ORDER BY distance ASC")
    LiveData<List<Wifi>> getAllWifisByDistance(double currentLat, double currentLng);

    @Query("SELECT * FROM wifis WHERE wifiName = :name AND longitude = :lng AND latitude = :lat")
    List<Wifi> findWifi(String name, double lat, double lng);

    @Query("SELECT COUNT(wifiName) FROM wifis")
    int getCountNumberOfRows();

    @Query("UPDATE wifis SET opinion = :rating, comments = :comments " +
            "WHERE wifiName= :name AND longitude = :lng AND latitude = :lat")
    void updateOpinionComments(String name, String comments, float rating, double lat, double lng);

    @Query("DELETE FROM wifis WHERE wifiName = :name AND latitude = :lat AND longitude = :lng")
    void deleteWifi(String name, double lat, double lng);

    @Query("SELECT * FROM wifis WHERE wifiName LIKE :query ORDER BY wifiName ASC")
    List<Wifi> getSearchResults(String query);

    @Query("SELECT COUNT(*) FROM wifis WHERE wifiName = :name AND latitude = :lat AND longitude = :lng")
    int checkIfWifiInDatabase(String name, double lat, double lng);
}
