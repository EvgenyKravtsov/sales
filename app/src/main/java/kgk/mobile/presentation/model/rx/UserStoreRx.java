package kgk.mobile.presentation.model.rx;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kgk.mobile.App;
import kgk.mobile.domain.LocationService;
import kgk.mobile.domain.SettingsStorageService;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.presentation.model.UserStore;

public final class UserStoreRx implements UserStore, LocationService.Listener {

    private static final String TAG = UserStoreRx.class.getSimpleName();

    private final LocationService locationService;
    private final SettingsStorageService settingsStorageService;

    private List<SingleObserver<UserLocation>> userLocationObservers = new ArrayList<>();

    ////

    @Inject public UserStoreRx(LocationService locationService,
                       SettingsStorageService settingsStorageService) {
        this.locationService = locationService;
        this.locationService.addListener(this);
        this.settingsStorageService = settingsStorageService;
    }

    //// USER STORE

    @Override
    public void subscribeForUserLocationUpdate(final LocationListener listener) {
        locationService.startLocationUpdate();
        userLocationObservers.add(new SingleObserver<UserLocation>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onSuccess(UserLocation userLocation) {
                listener.onLocationReceived(userLocation);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: Location Observer Error");
            }
        });
    }

    @Override
    public void requestPreferredMapZoom(final PreferredMapZoomListener listener) {
        Single<Float> preferredMapZoomSingle = Single.just(settingsStorageService.getPreferredMapZoom());
        preferredMapZoomSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Float>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onSuccess(Float zoom) {
                        listener.onPreferredMapZoomReceived(zoom);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO Implement
                        Log.d(TAG, "onError: Preferred Zoom Observer Error");
                    }
                });
    }

    //// LOCATION SERVICE LISTENER

    @Override
    public void onLocationChanged(UserLocation userLocation) {
        Single<UserLocation> userLocationSingle = Single.just(userLocation);
        for (SingleObserver<UserLocation> observer : userLocationObservers) {
            userLocationSingle.subscribe(observer);
        }
    }
}















