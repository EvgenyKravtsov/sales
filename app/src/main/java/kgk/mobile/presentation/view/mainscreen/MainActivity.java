package kgk.mobile.presentation.view.mainscreen;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import kgk.mobile.App;
import kgk.mobile.R;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.presentation.view.map.GoogleMapController;
import kgk.mobile.presentation.view.map.MapController;


public final class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, MainContract.View {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_FINE_LOCATION_ID = 10;

    private MainContract.Presenter presenter;

    //// ACTIVITY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupMap();

        presenter = App.getComponent().mainPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            presenter.onLocationPermissionGranted();
        }
        else {
            // TODO Handle Permission Deny
        }
    }

    //// ON MAP READY

    @Override
    public void onMapReady(GoogleMap googleMap) {
        presenter.onMapDisplayed(new GoogleMapController(googleMap));
    }

    //// MAIN VIEW

    @Override
    public void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION_ID);
        }
        else {
            presenter.onLocationPermissionGranted();
        }
    }

    //// PRIVATE

    private void setupMap() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment fragment = new SupportMapFragment();
        fragmentManager.beginTransaction()
                .add(R.id.mainActivity_mapFragmentContainer, fragment)
                .commit();
        fragment.getMapAsync(this);
    }
}
