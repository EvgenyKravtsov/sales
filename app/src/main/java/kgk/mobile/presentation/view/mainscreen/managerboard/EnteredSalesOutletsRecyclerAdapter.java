package kgk.mobile.presentation.view.mainscreen.managerboard;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.SalesOutlet;

final class EnteredSalesOutletsRecyclerAdapter extends RecyclerView.Adapter<EnteredSalesOutletsRecyclerAdapter.ViewHolder> {

    private List<SalesOutlet> salesOutletsEntered = new ArrayList<>();

    ////

    public void setSalesOutletsEntered(List<SalesOutlet> salesOutletsEntered) {
        this.salesOutletsEntered = salesOutletsEntered;
    }

    //// RECYCLER VIEW ADAPTER

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    ////

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View view) {
            super(view);
        }
    }
}
