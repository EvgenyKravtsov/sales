package kgk.mobile.presentation.view.mainscreen.managerboard;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kgk.mobile.DependencyInjection;
import kgk.mobile.R;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.external.android.ImageCreator;
import kgk.mobile.presentation.view.mainscreen.managerboard.dialog.AttendanceSuccessfulAlert;
import kgk.mobile.presentation.view.mainscreen.managerboard.dialog.NoOperationSelectedAlert;


public final class UserBoardFragment extends Fragment implements UserBoardContract.View {

    @BindView(R.id.userBoardFragment_enteredSalesOutletsRecyclerView)
    RecyclerView enteredSalesOutletsRecyclerView;
    @BindView(R.id.userBoardFragment_userOperationsLinearLayout)
    LinearLayout userOperationsLinearLayout;
    @BindView(R.id.userBoardFragment_salesOutletTitleTextView)
    TextView salesOutletTitleTextView;
    @BindView(R.id.userBoardFragment_userOperationsRecyclerView)
    RecyclerView userOperationsRecyclerView;
    @BindView(R.id.userBoardFragment_userOperationsConfirmButton)
    Button userOperationsConfirmButton;

    private static final String TAG = UserBoardFragment.class.getSimpleName();
    private static final int WRAP_CONTENT_CODE = 10000;

    private UserBoardContract.Presenter presenter;
    private EnteredSalesOutletsRecyclerAdapter enteredSalesOutletsRecyclerAdapter;
    private UserOperationsRecyclerAdapter userOperationsRecyclerAdapter;
    private boolean isUserOperationDroppedDown;

    //// FRAGMENT

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = DependencyInjection.provideManagerBoardPresenter();
        presenter.attachView(this);
        presenter.setMapController(DependencyInjection.provideMapController());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_board, container, false);
        ButterKnife.bind(this, view);
        setupEnteredSalesOutletsRecyclerView();
        setupUserOperationsRecyclerView();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    //// MANAGER BOARD VIEW

    @Override
    public void displayEnteredSalesOutlets(List<SalesOutlet> salesOutletsEntered) {
        if (salesOutletsEntered.size() > 0) {
            enteredSalesOutletsRecyclerView.setVisibility(View.VISIBLE);

            if (salesOutletsEntered.size() > 3) setHeight(enteredSalesOutletsRecyclerView, 210);
            else setHeight(enteredSalesOutletsRecyclerView, WRAP_CONTENT_CODE);
        }
        else {
            enteredSalesOutletsRecyclerView.setVisibility(View.GONE);
        }

        enteredSalesOutletsRecyclerAdapter.setSalesOutletsEntered(salesOutletsEntered);
        enteredSalesOutletsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void displayUserOperations(List<UserOperation> userOperations) {
        slideUpUserOperations();

        if (userOperations.size() > 0) {
            userOperationsLinearLayout.setVisibility(View.VISIBLE);

            if (userOperations.size() > 4) setHeight(userOperationsRecyclerView, 270);
            else setHeight(userOperationsRecyclerView, WRAP_CONTENT_CODE);
        }
        else {
            userOperationsRecyclerView.setVisibility(View.GONE);
        }

        userOperationsRecyclerAdapter.setUserOperations(userOperations);
        userOperationsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void hideUserOperations() {
        userOperationsLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void displaySelectedSalesOutlet(SalesOutlet selectedSalesOutlet) {
        salesOutletTitleTextView.setText(selectedSalesOutlet.getTitle());
    }

    //// CONTROL CALLBACKS

    @OnClick(R.id.userBoardFragment_userOperationsDropDownImageButton)
    public void onClickUserOperationsDropDownImageButton() {
        if (!isUserOperationDroppedDown) dropDownUserOperations();
        else slideUpUserOperations();
    }

    @OnClick(R.id.userBoardFragment_userOperationsConfirmButton)
    public void onClickUserOperationsConfirmButton() {
        List<UserOperation> selectedUserOperations =
                userOperationsRecyclerAdapter.getSelectedUserOperations();

        if (selectedUserOperations.size() == 0) {
            displayNoOperationSelectedAlert();
            return;
        }

        presenter.salesOutletAttended(selectedUserOperations,
                userOperationsRecyclerAdapter.getAddedValue());

        displayAttendanceSuccessful();
        dropDownUserOperations();
    }

    //// PRIVATE

    private void setupEnteredSalesOutletsRecyclerView() {
        enteredSalesOutletsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        enteredSalesOutletsRecyclerView.setLayoutManager(layoutManager);
        enteredSalesOutletsRecyclerAdapter =
                new EnteredSalesOutletsRecyclerAdapter(enteredSalesOutletsRecyclerView, presenter);
        enteredSalesOutletsRecyclerView.setAdapter(enteredSalesOutletsRecyclerAdapter);
    }

    private void setupUserOperationsRecyclerView() {
        userOperationsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        userOperationsRecyclerView.setLayoutManager(layoutManager);
        userOperationsRecyclerAdapter =
                new UserOperationsRecyclerAdapter(userOperationsRecyclerView, getActivity());
        userOperationsRecyclerView.setAdapter(userOperationsRecyclerAdapter);
    }

    private void setHeight(View view, int heightInDp) {
        int height = ImageCreator.dpToPx(heightInDp);
        if (heightInDp == WRAP_CONTENT_CODE) height = ViewGroup.LayoutParams.WRAP_CONTENT;

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    private void displayNoOperationSelectedAlert() {
        NoOperationSelectedAlert alert = new NoOperationSelectedAlert(getActivity());
        alert.show();
    }

    private void displayAttendanceSuccessful() {
        AttendanceSuccessfulAlert alert = new AttendanceSuccessfulAlert(getActivity());
        alert.show();
    }

    private void dropDownUserOperations() {
        isUserOperationDroppedDown = true;
        setHeight(userOperationsLinearLayout, 24);
    }

    private void slideUpUserOperations() {
        isUserOperationDroppedDown = false;
        setHeight(userOperationsLinearLayout, WRAP_CONTENT_CODE);
    }
}
