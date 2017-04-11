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

import butterknife.ButterKnife;
import butterknife.OnClick;
import kgk.mobile.DependencyInjection;
import kgk.mobile.R;
import kgk.mobile.presentation.view.mainscreen.menu.MenuFragment;
import kgk.mobile.presentation.view.mainscreen.userboard.UserBoardFragment;
import kgk.mobile.presentation.view.map.MapController;
import kgk.mobile.presentation.view.map.google.GoogleMapController;

public final class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, MainContract.View {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_FINE_LOCATION_ID = 10;
    private static final int REQUEST_PHONE_STATE_ID = 20;
    private static final String USER_BOARD_FRAGMENT_BACKSTACK_ID = "UserBoardFragment";
    private static final String MENU_FRAGMENT_BACKSTACK_ID = "MenuFragment";

    private MainContract.Presenter presenter;

    //// ACTIVITY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupMap();

        presenter = DependencyInjection.provideMainPresenter();
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

        switch (requestCode) {
            case REQUEST_FINE_LOCATION_ID:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.onLocationPermissionGranted();
                }
                else {
                    // TODO Handle Permission Deny
                }

                break;

            case REQUEST_PHONE_STATE_ID:
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    presenter.onPhoneStatePermissionNotGranted();
                }

                break;
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        }
        else {
            getSupportFragmentManager().popBackStack();

            FragmentManager fragmentManager = getSupportFragmentManager();
            UserBoardFragment userBoardFragment = (UserBoardFragment) fragmentManager
                    .findFragmentByTag(USER_BOARD_FRAGMENT_BACKSTACK_ID);

            fragmentManager.beginTransaction()
                    .show(userBoardFragment)
                    .commit();
        }
    }

    //// ON MAP READY

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapController mapController = new GoogleMapController(googleMap);
        presenter.onMapDisplayed(mapController);
        DependencyInjection.setMapController(mapController);

        // Ensure That Map Object Is Ready Before Creating Manager Board Fragment
        setupManagerBoard();
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

    @Override
    public void requestPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PHONE_STATE_ID);
        }
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void navigateToMenu() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MenuFragment menuFragment = new MenuFragment();

        UserBoardFragment userBoardFragment = (UserBoardFragment) fragmentManager
                .findFragmentByTag(USER_BOARD_FRAGMENT_BACKSTACK_ID);

        fragmentManager.beginTransaction()
                .hide(userBoardFragment)
                .add(R.id.mainActivity_contentFragmentContainer, menuFragment)
                .addToBackStack(MENU_FRAGMENT_BACKSTACK_ID)
                .commit();
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

    private void setupManagerBoard() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        UserBoardFragment fragment = new UserBoardFragment();
        fragmentManager.beginTransaction()
                .add(R.id.mainActivity_contentFragmentContainer, fragment, USER_BOARD_FRAGMENT_BACKSTACK_ID)
                .commit();
    }

    //// CONTROL CALLBACKS

    @OnClick(R.id.mainActivity_menuImageButton)
    public void onClick() {
        presenter.onMenuButtonClicked();
    }
}
