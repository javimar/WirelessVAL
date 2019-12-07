package eu.javimar.wirelessval.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class WifiViewModelFactory implements ViewModelProvider.Factory
{
    private final Application mApplication;
    private final double mCurrentLat;
    private final double mCurrentLng;

    public WifiViewModelFactory(Application application, double currentLat, double currentLng)
    {
        mApplication = application;
        mCurrentLat = currentLat;
        mCurrentLng = currentLng;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)
    {
        return (T) new WifiViewModel(mApplication, mCurrentLat, mCurrentLng);
    }
}