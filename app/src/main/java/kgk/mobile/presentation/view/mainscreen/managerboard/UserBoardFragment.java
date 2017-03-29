package kgk.mobile.presentation.view.mainscreen.managerboard;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kgk.mobile.DependencyInjection;
import kgk.mobile.R;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.external.android.ImageCreator;


public final class UserBoardFragment extends Fragment implements UserBoardContract.View {

    @BindView(R.id.userBoardFragment_enteredSalesOutletsRecyclerView)
    RecyclerView enteredSalesOutletsRecyclerView;
    @BindView(R.id.userBoardFragment_userOperationsRecyclerView)
    RecyclerView userOperationsRecyclerView;

    private static final String TAG = UserBoardFragment.class.getSimpleName();
    private static final int WRAP_CONTENT_CODE = 10000;

    private UserBoardContract.Presenter presenter;
    private EnteredSalesOutletsRecyclerAdapter enteredSalesOutletsRecyclerAdapter;
    private UserOperationsRecyclerAdapter userOperationsRecyclerAdapter;

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
        userOperationsRecyclerView.setVisibility(View.VISIBLE);
        userOperationsRecyclerAdapter.setUserOperations(userOperations);
        userOperationsRecyclerAdapter.notifyDataSetChanged();
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
        userOperationsRecyclerAdapter = new UserOperationsRecyclerAdapter();
        userOperationsRecyclerView.setAdapter(userOperationsRecyclerAdapter);
    }

    private void setHeight(View view, int heightInDp) {
        int height = ImageCreator.dpToPx(heightInDp);
        if (heightInDp == WRAP_CONTENT_CODE) height = ViewGroup.LayoutParams.WRAP_CONTENT;

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }
}
