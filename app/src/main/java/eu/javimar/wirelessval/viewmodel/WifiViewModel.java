package eu.javimar.wirelessval.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import eu.javimar.wirelessval.model.Wifi;

public class WifiViewModel extends AndroidViewModel
{
    private final WifiRepository repository;
    private final LiveData<List<Wifi>> allWifisByName;
    private final LiveData<List<Wifi>> allWifisByDistance;
    private final LiveData<List<Wifi>> allWifisByOpinion;
    private final MutableLiveData<List<Wifi>> searchQueryResults;
    private final MutableLiveData<List<Wifi>> searchWifiResults;

    private final int countNumberOfWifis;

    WifiViewModel(@NonNull Application application, double currentLat, double currentLng)
    {
        super(application);
        repository = new WifiRepository(application, currentLat, currentLng);

        allWifisByName = repository.getAllWifisByName();
        allWifisByDistance = repository.getAllWifisByDistance();
        allWifisByOpinion = repository.getAllWifisByOpinion();
        countNumberOfWifis = repository.getCountNumberOfWifis();
        searchQueryResults = repository.getSearchQueryResults();
        searchWifiResults = repository.getSearchWifiResults();
    }

    public LiveData<List<Wifi>> getAllWifisByName() { return allWifisByName; }
    public LiveData<List<Wifi>> getAllWifisByDistance() { return allWifisByDistance; }
    public LiveData<List<Wifi>> getAllWifisByOpinion() { return allWifisByOpinion; }
    public MutableLiveData<List<Wifi>> getSearchQueryResults() { return searchQueryResults; }
    public MutableLiveData<List<Wifi>> getSearchWifiResults() { return searchWifiResults; }
    public void getSearchResults(String query) { repository.getSearchResults(query); }
    public int getCountNumberOfWifis() { return countNumberOfWifis; }
    public void insertWifis(List<Wifi> wifis) { repository.insertWifis(wifis); }
    public void deleteWifi(String name, double lat, double lng) { repository.deleteWifi(name, lat, lng); }
    public void findWifi(String name, String lat, String lng) { repository.findWifi(name, lat, lng); }
    public void updateOpinion(String name, String comments, float rating, double lat, double lng)
    {
        repository.updateOpinionComments(name, comments, rating, lat, lng);
    }
    public int checkIfWifiInDatabase(String name, double lat, double lng)
    {
        return repository.wifiInDatabase(name, lat, lng);
    }
}
