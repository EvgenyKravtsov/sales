package kgk.mobile.presentation.view.mainscreen.lastactions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import kgk.mobile.domain.SalesOutletAttendance;

public final class LastActionsFragment extends Fragment
        implements LastActionsContract.View {

    private static final String TAG = LastActionsFragment.class.getSimpleName();

    @BindView(R.id.lastActionsFragment_salesOutletAttendancesRecyclerView)
    RecyclerView salesOutletAttendancesRecyclerView;

    private SalesOutletAttendancesRecyclerAdapter attendancesRecyclerAdapter;

    private LastActionsContract.Presenter presenter;

    //// FRAGMENT

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = DependencyInjection.provideLastActionsPresenter();
        presenter.attachView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_last_actions, container, false);
        ButterKnife.bind(this, view);
        setupSalesOutletAttendancesRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onCreateView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    //// LAST ACTIONS VIEW

    @Override
    public void displaySalesOutletAttendances(List<SalesOutletAttendance> attendances) {
        if (attendancesRecyclerAdapter != null) {
            attendancesRecyclerAdapter.setSalesOutletAttendances(attendances);
            attendancesRecyclerAdapter.notifyDataSetChanged();
        }
    }

    //// PRIVATE

    private void setupSalesOutletAttendancesRecyclerView() {
        salesOutletAttendancesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        salesOutletAttendancesRecyclerView.setLayoutManager(layoutManager);
        attendancesRecyclerAdapter = new SalesOutletAttendancesRecyclerAdapter();
        salesOutletAttendancesRecyclerView.setAdapter(attendancesRecyclerAdapter);
    }
}
