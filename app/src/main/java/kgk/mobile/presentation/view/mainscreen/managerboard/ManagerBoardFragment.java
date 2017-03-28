package kgk.mobile.presentation.view.mainscreen.managerboard;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kgk.mobile.DependencyInjection;
import kgk.mobile.R;
import kgk.mobile.domain.SalesOutlet;


public final class ManagerBoardFragment extends Fragment implements ManagerBoardContract.View {

    @BindView(R.id.managerBoardFragment_enteredSalesOutletsRecyclerView)
    RecyclerView enteredSalesOutletsRecyclerView;

    private static final String TAG = ManagerBoardFragment.class.getSimpleName();

    private ManagerBoardContract.Presenter presenter;

    //// FRAGMENT

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        presenter = DependencyInjection.provideManagerBoardPresenter();
        presenter.attachView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_board, container, false);
        ButterKnife.bind(this, view);
        setupEnteredSalesOutletsRecyclerView();
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
        Log.d(TAG, "displayEnteredSalesOutlets: ");
    }

    //// PRIVATE

    private void setupEnteredSalesOutletsRecyclerView() {
        enteredSalesOutletsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        enteredSalesOutletsRecyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new EnteredSalesOutletsRecyclerAdapter();
        enteredSalesOutletsRecyclerView.setAdapter(adapter);
    }
}
