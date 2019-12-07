package eu.javimar.wirelessval.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import eu.javimar.wirelessval.database.WifiDatabase;
import eu.javimar.wirelessval.database.WifisDao;
import eu.javimar.wirelessval.model.Wifi;

class WifiRepository
{
    private final WifisDao wifisDao;

    WifiRepository(Application application, double currentLat, double currentLng)
    {
        WifiDatabase db;
        db = WifiDatabase.getDatabase(application);
        wifisDao = db.wifisDao();

        // Dao return methods
        allWifisByName = wifisDao.getAllWifisByName();
        allWifisByDistance = wifisDao.getAllWifisByDistance(currentLat, currentLng);
        allWifisByOpinion = wifisDao.getAllWifisByOpinion();
    }

    private final LiveData<List<Wifi>> allWifisByName;
    LiveData<List<Wifi>> getAllWifisByName() { return allWifisByName; }
    private final LiveData<List<Wifi>> allWifisByDistance;
    LiveData<List<Wifi>> getAllWifisByDistance() { return allWifisByDistance; }
    private final LiveData<List<Wifi>> allWifisByOpinion;
    LiveData<List<Wifi>> getAllWifisByOpinion() { return allWifisByOpinion; }


    /**
     * Check IF WIFI IS IN DATABASE
     */
    int wifiInDatabase(String name, double lat, double lng)
    {
        AtomicInteger wifi = new AtomicInteger(0);
        Thread thread = new Thread(() ->
                wifi.set(wifisDao.checkIfWifiInDatabase(name, lat, lng)));
        thread.setPriority(10);
        thread.start();
        try { thread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        return wifi.get();
    }

    /**
     * CHECK IF DATABASE EMPTY
     */
    int getCountNumberOfWifis()
    {
        final AtomicInteger wifis = new AtomicInteger();
        Thread thread = new Thread(() ->
        {
            int count = wifisDao.getCountNumberOfRows();
            wifis.set(count);
        });
        thread.setPriority(10);
        thread.start();
        try { thread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        return wifis.get();
    }

    /**
     * OPINION UPDATE
     */
    void updateOpinionComments(String name, String comments, float rating, double lat, double lng)
    {
        UpdateAsyncTask task = new UpdateAsyncTask(wifisDao);
        task.execute(name, comments,
                String.valueOf(rating), String.valueOf(lat), String.valueOf(lng));
    }
    private static class UpdateAsyncTask extends AsyncTask<String, Void, Void>
    {
        private final WifisDao asyncTaskDao;
        UpdateAsyncTask(WifisDao dao) { asyncTaskDao = dao; }
        @Override
        protected Void doInBackground(String... params)
        {
            asyncTaskDao.updateOpinionComments(params[0], params[1],
                    Float.parseFloat(params[2]),
                    Double.parseDouble(params[3]),
                    Double.parseDouble(params[4]));
            return null;
        }
    }

    /**
     * INSERTION
     */
    void insertWifis(List<Wifi> wifis)
    {
        InsertAsyncTask task = new InsertAsyncTask(wifisDao);
        task.execute(wifis);
    }
    private static class InsertAsyncTask extends AsyncTask<List<Wifi>, Void, Void>
    {
        private final WifisDao asyncTaskDao;
        InsertAsyncTask(WifisDao dao) { asyncTaskDao = dao; }
        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Wifi>... wifis)
        {
            asyncTaskDao.insertWifis(wifis[0]);
            return null;
        }
    }

    /**
     * DELETION
     */
    void deleteWifi(String name, double lat, double lng)
    {
        DeleteAsyncTask task = new DeleteAsyncTask(wifisDao);
        task.execute(name, String.valueOf(lat), String.valueOf(lng));
    }
    private static class DeleteAsyncTask extends AsyncTask<String, Void, Void>
    {
        private final WifisDao asyncTaskDao;
        DeleteAsyncTask(WifisDao dao) { asyncTaskDao = dao; }
        @Override
        protected final Void doInBackground(String... params)
        {
            asyncTaskDao.deleteWifi(params[0],
                    Double.parseDouble(params[1]),
                    Double.parseDouble(params[2]));
            return null;
        }
    }

    /**
     * SEARCH FUNCTIONALITY
     */
    private final MutableLiveData<List<Wifi>> searchQueryResults = new MutableLiveData<>();
    private void asyncSearchFinished(List<Wifi> queryResults) { searchQueryResults.setValue(queryResults); }
    MutableLiveData<List<Wifi>> getSearchQueryResults() { return searchQueryResults; }
    void getSearchResults(String query)
    {
        SearchAsyncTask task = new SearchAsyncTask(wifisDao);
        task.delegate = this;
        task.execute(query);
    }
    private static class SearchAsyncTask extends AsyncTask<String, Void, List<Wifi>>
    {
        private final WifisDao asyncTaskDao;
        private WifiRepository delegate = null;
        SearchAsyncTask (WifisDao dao)
        {
            asyncTaskDao = dao;
        }
        @Override
        protected List<Wifi> doInBackground(String... queries)
        {
            return asyncTaskDao.getSearchResults(queries[0]);
        }
        @Override
        protected void onPostExecute(List<Wifi> wifi)
        {
            delegate.asyncSearchFinished(wifi);
        }
    }

    /**
     * FIND WIFI
     */
    private final MutableLiveData<List<Wifi>> searchWifiResults = new MutableLiveData<>();
    private void asyncFinished(List<Wifi> wifiResults) { searchWifiResults.setValue(wifiResults); }
    MutableLiveData<List<Wifi>> getSearchWifiResults()
    {
        return searchWifiResults;
    }
    void findWifi(String name, String lat, String lng)
    {
        QueryAsyncTask task = new QueryAsyncTask(wifisDao);
        task.delegate = this;
        task.execute(name, lat, lng);
    }
    private static class QueryAsyncTask extends AsyncTask<String, Void, List<Wifi>>
    {
        private final WifisDao asyncTaskDao;
        private WifiRepository delegate = null;
        QueryAsyncTask (WifisDao dao) { asyncTaskDao = dao; }
        @Override
        protected List<Wifi> doInBackground(String... params)
        {
            return asyncTaskDao.findWifi(params[0],
                    Double.parseDouble(params[1]),
                    Double.parseDouble(params[2]));
        }
        @Override
        protected void onPostExecute(List<Wifi> wifi)
        {
            delegate.asyncFinished(wifi);
        }
    }
}
