package kgk.mobile.presentation.view.mainscreen;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kgk.mobile.DependencyInjection;
import kgk.mobile.R;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.SystemService;
import kgk.mobile.external.android.ImageCreator;
import kgk.mobile.presentation.model.reactive.MainStoreReactive;
import kgk.mobile.presentation.view.loginscreen.dialog.PermissionsNeededAlert;
import kgk.mobile.presentation.view.mainscreen.dialog.AttendanceSuccessfulAlert;
import kgk.mobile.presentation.view.mainscreen.dialog.AuthorizationDeniedAlert;
import kgk.mobile.presentation.view.mainscreen.dialog.NoOperationSelectedAlert;
import kgk.mobile.presentation.view.mainscreen.help.HelpFragment;
import kgk.mobile.presentation.view.mainscreen.lastactions.LastActionsFragment;
import kgk.mobile.presentation.view.mainscreen.technicalinformation.TechnicalInformationFragment;
import kgk.mobile.presentation.view.map.MapController;
import kgk.mobile.presentation.view.map.google.GoogleMapController;

public final class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, MainContract.View, MapController.Listener {

    @BindView(R.id.mainActivity_fetchingUserLocationAlertLinearLayout)
    LinearLayout fetchingUserLocationAlertLinearLayout;
    @BindView(R.id.mainActivity_loadingSalesOutletsAlertLinearLayout)
    LinearLayout loadingSalesOutletsAlertLinearLayout;
    @BindView(R.id.mainActivity_enteredSalesOutletsRecyclerView)
    RecyclerView enteredSalesOutletsRecyclerView;
    @BindView(R.id.mainActivity_navigationMenuLinearLayout)
    LinearLayout navigationMenuLinearLayout;
    @BindView(R.id.mainActivity_navigationMenuImageButton)
    ImageButton navigationMenuImageButton;
    @BindView(R.id.mainActivity_navigationMenuDropDownImageButton)
    ImageButton navigationMenuDropDownImageButton;
    @BindView(R.id.mainActivity_userOperationsLinearLayout)
    LinearLayout userOperationsLinearLayout;
    @BindView(R.id.mainActivity_userOperationsRecyclerView)
    RecyclerView userOperationsRecyclerView;
    @BindView(R.id.mainActivity_loadingUserOperationsAlertLinearLayout)
    LinearLayout loadingUserOperationsAlertLinearLayout;
    @BindView(R.id.mainActivity_selectedSalesOutletTitleTextView)
    TextView selectedSalesOutletTitleTextView;
    @BindView(R.id.mainActivity_loadingAuthorizationAlertLinearLayout)
    LinearLayout loadingAuthorizationAlertLinearLayout;
    @BindView(R.id.mainActivity_authorizationDeniedTextVew)
    TextView authorizationDeniedTextVew;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int WRAP_CONTENT_CODE = 10000;
    private static final String TECHNICAL_INFORMATION_FRAGMENT_BACKSTACK_ID = "TechnicalInformationFragment";
    private static final String LAST_ACTIONS_FRAGMENT_BACKSTACK_ID = "LastActionsFragment";
    private static final String HELP_FRAGMENT_BACKSTACK_ID = "HelpFragment";
    private static final int REQUEST_PERMISSIONS_ID = 10;
    private static final String[] PERMISSIONS = { Manifest.permission.READ_PHONE_STATE,
                                                  Manifest.permission.ACCESS_FINE_LOCATION };

    private MainContract.Presenter presenter;
    private MapController mapController;
    private EnteredSalesOutletsRecyclerAdapter enteredSalesOutletsRecyclerAdapter;
    private UserOperationsRecyclerAdapter userOperationsRecyclerAdapter;
    private boolean userOperationsMinimized;

    //// ACTIVITY

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupMap();
        setupEnteredSalesOutletsRecyclerView();
        setupUserOperationsRecyclerView();

        presenter = DependencyInjection.provideMainPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter = null;
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            finish();
        }
        else {
            getSupportFragmentManager().popBackStack();
            mapController.redrawMapObjects();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (!hasPermissions()) presenter.onPermissionsDenied();
        else presenter.onPermissionGranted();
    }

    //// ON MAP READY

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.setPadding(0, 0, 0, ImageCreator.dpToPx(92));

        mapController = new GoogleMapController(
                googleMap,
                DependencyInjection.provideSettingsStorageService());
        mapController.addListener(this);
        presenter.onViewReady();
        Log.d(TAG, "onMapReady: ");
    }

    //// MAIN VIEW

    @Override
    public void displayUserLocation(UserLocation userLocation) {
        mapController.centerCameraOnUser(userLocation.getLatitude(), userLocation.getLongitude(), false);
        mapController.displayUser(userLocation.getLatitude(), userLocation.getLongitude());
    }

    @Override
    public void displayUserLocationFetchingAlert() {
        fetchingUserLocationAlertLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideUserLocationFetchingAlert() {
        fetchingUserLocationAlertLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void displaySalesOutlets(List<SalesOutlet> salesOutlets) {
        mapController.displaySalesOutlets(salesOutlets);
    }

    @Override
    public void displayLoadingSalesOutletsAlert() {
        loadingSalesOutletsAlertLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidLoadingSalesOutletsAlert() {
        loadingSalesOutletsAlertLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void displayEnteredSalesOutlets(List<SalesOutlet> enteredSalesOutlet) {
        mapController.displayEnteredSalesOutlets(enteredSalesOutlet);

        // Display On Recycler
        enteredSalesOutletsRecyclerAdapter.updateEnteredSalesOutlets(enteredSalesOutlet);
        enteredSalesOutletsRecyclerView.setVisibility(enteredSalesOutlet.size() == 0 ? View.GONE : View.VISIBLE);
        if (enteredSalesOutlet.size() > 3) setViewHeight(enteredSalesOutletsRecyclerView, 176);
        else setViewHeight(enteredSalesOutletsRecyclerView, WRAP_CONTENT_CODE);
    }

    @Override
    public void displayMapZoom(float zoom) {
        mapController.displayZoom(zoom, false);
    }

    @Override
    public void displayNavigationMenu() {
        navigationMenuLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNavigationMenu() {
        navigationMenuLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void navigateToTechnicalInformation() {
        navigateToFragment(new TechnicalInformationFragment(), TECHNICAL_INFORMATION_FRAGMENT_BACKSTACK_ID);
    }

    @Override
    public void navigateToLastActions() {
        navigateToFragment(new LastActionsFragment(), LAST_ACTIONS_FRAGMENT_BACKSTACK_ID);
    }

    @Override
    public void navigateToHelp() {
        navigateToFragment(new HelpFragment(), HELP_FRAGMENT_BACKSTACK_ID);
    }

    @Override
    public void displaySelectedSalesOutlet(SalesOutlet selectedSalesOutlet,
                                           long salesOutletAttendanceBeginDateUnixSeconds) {

        mapController.displaySelectedSalesOutlet(selectedSalesOutlet);
        mapController.centerCamera(selectedSalesOutlet.getLatitude(), selectedSalesOutlet.getLongitude(), true);

        enteredSalesOutletsRecyclerAdapter.updateSelectedSalesOutlet(
                selectedSalesOutlet,
                salesOutletAttendanceBeginDateUnixSeconds);

        selectedSalesOutletTitleTextView.setText(selectedSalesOutlet.getTitle());
        userOperationsLinearLayout.setVisibility(View.VISIBLE);
    }
    @Override
    public void displayUserOperations(List<UserOperation> userOperations) {
        if (userOperations.size() == 0) {
            loadingUserOperationsAlertLinearLayout.setVisibility(View.VISIBLE);
            userOperationsRecyclerView.setVisibility(View.GONE);
        }
        else {
            loadingUserOperationsAlertLinearLayout.setVisibility(View.GONE);
            userOperationsRecyclerView.setVisibility(View.VISIBLE);
            userOperationsRecyclerAdapter.updateUserOperations(userOperations);

            if (userOperations.size() > 3) setViewHeight(userOperationsRecyclerView, 182);
            else setViewHeight(userOperationsRecyclerView, WRAP_CONTENT_CODE);
        }
    }

    @Override
    public void hideSelectedSalesOutlet() {
        mapController.hideSelectedSalesOutlet();
        enteredSalesOutletsRecyclerAdapter.updateSelectedSalesOutlet(null, 0);
        userOperationsLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void displayLoadingAuthorizationAlert() {
        loadingAuthorizationAlertLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingAuthorizationAlert() {
        loadingAuthorizationAlertLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void displayAuthorizationDeniedAlert() {
        new AuthorizationDeniedAlert(this).show();
    }

    @Override
    public void displayAuthorizationDenied() {
        authorizationDeniedTextVew.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAuthorizationDenied() {
        authorizationDeniedTextVew.setVisibility(View.GONE);
    }

    @Override
    public void requestPermissions() {
        if (!hasPermissions()) ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS_ID);
        else presenter.onPermissionGranted();
    }

    @Override
    public void displayPermissionsNeededAlert() {
        new PermissionsNeededAlert(this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }).show();
    }

    //// MAP CONTROLLER LISTENER

    @Override
    public void onMapZoomChanged(float zoom) {
        presenter.onMapZoomChanged(zoom);
    }

    //// CONTROL CALLBACKS

    @OnClick(R.id.mainActivity_navigationMenuImageButton)
    public void onClickNavigationMenuButton() {
        presenter.onClickNavigationMenuButton();
    }

    @OnClick(R.id.mainActivity_navigationMenuDropDownImageButton)
    public void onClickNavigationMenuDropDownButton() {
        presenter.onClickNavigationMenuDropDownButton();
    }

    @OnClick(R.id.mainActivity_technicalInformationButton)
    public void onClickTechnicalInformationButton() {
        presenter.onClickTechnicalInformationButton();
    }

    @OnClick(R.id.mainActivity_lastActionsButton)
    public void onClickLastActionsButton() {
        presenter.onClickLastActionsButton();
    }

    @OnClick(R.id.mainActivity_helpImageButton)
    public void onClickHelpButton() {
        presenter.onClickHelpButton();
    }

    @OnClick(R.id.mainActivity_userOperationsDropDownImageButton)
    public void onClickUserOperationsDropDownImageButton() {
        if (userOperationsMinimized) {
            userOperationsMinimized = false;
            setViewHeight(userOperationsLinearLayout, WRAP_CONTENT_CODE);
        }
        else {
            userOperationsMinimized = true;
            setViewHeight(userOperationsLinearLayout, 24);
        }
    }

    @OnClick(R.id.mainActivity_userOperationsConfirmButton)
    public void onClickUserOperationsConfirmButton() {
        List<UserOperation> selectedUserOperations = userOperationsRecyclerAdapter.getSelectedUserOperations();
        int attendanceAddedValue = userOperationsRecyclerAdapter.getAddedValue();

        if (selectedUserOperations.size() == 0) {
            new NoOperationSelectedAlert(this).show();
            return;
        }

        presenter.onClickUserOperationsConfirmButton(selectedUserOperations, attendanceAddedValue);
        presenter.onClickEnteredSalesOutlet(null);
        new AttendanceSuccessfulAlert(this).show();
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

    private void setupEnteredSalesOutletsRecyclerView() {
        enteredSalesOutletsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        enteredSalesOutletsRecyclerView.setLayoutManager(layoutManager);
        enteredSalesOutletsRecyclerAdapter = new EnteredSalesOutletsRecyclerAdapter(
                enteredSalesOutletsRecyclerView,
                new EnteredSalesOutletClickListener() {
                    @Override
                    public void onClickEnteredSalesOutlet(SalesOutlet salesOutlet) {
                        presenter.onClickEnteredSalesOutlet(salesOutlet);
                    }
                });
        enteredSalesOutletsRecyclerView.setAdapter(enteredSalesOutletsRecyclerAdapter);
    }

    private void setupUserOperationsRecyclerView() {
        userOperationsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        userOperationsRecyclerView.setLayoutManager(layoutManager);
        userOperationsRecyclerAdapter =
                new UserOperationsRecyclerAdapter(userOperationsRecyclerView, this);
        userOperationsRecyclerView.setAdapter(userOperationsRecyclerAdapter);
    }

    private void setViewHeight(View view, int heightInDp) {
        int height = ImageCreator.dpToPx(heightInDp);
        if (heightInDp == WRAP_CONTENT_CODE) height = ViewGroup.LayoutParams.WRAP_CONTENT;

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    private boolean hasPermissions() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private void navigateToFragment(Fragment fragment, String backstackId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.mainActivity_contentFragmentContainer, fragment)
                .addToBackStack(backstackId)
                .commit();
    }
}
