package kgk.mobile.presentation.view.mainscreen.userboard;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import kgk.mobile.R;
import kgk.mobile.domain.SalesOutlet;

final class EnteredSalesOutletsRecyclerAdapter
        extends RecyclerView.Adapter<EnteredSalesOutletsRecyclerAdapter.ViewHolder> {

    private static final String TAG = EnteredSalesOutletsRecyclerAdapter.class.getSimpleName();

    private List<SalesOutlet> salesOutletsEntered = new ArrayList<>();
    private String selectedSalesOutletCode = "";

    private final RecyclerView recyclerView;
    private final UserBoardContract.Presenter presenter;

    ////

    EnteredSalesOutletsRecyclerAdapter(
            RecyclerView recyclerView,
            UserBoardContract.Presenter presenter) {
        this.recyclerView = recyclerView;
        this.presenter = presenter;
    }

    ////

    public void setSalesOutletsEntered(List<SalesOutlet> salesOutletsEntered) {
        this.salesOutletsEntered = salesOutletsEntered;
    }

    void deselectSalesOutlets() {
        Log.d(TAG, "deselectSalesOutlets: ");
        selectedSalesOutletCode = "";
        notifyDataSetChanged();
    }

    void selectSalesOutlet(String selectedSalesOutletCode) {
        Log.d(TAG, "selectSalesOutlet: " + selectedSalesOutletCode);
        this.selectedSalesOutletCode = selectedSalesOutletCode;
        notifyDataSetChanged();
    }

    //// RECYCLER VIEW ADAPTER

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View recyclerItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_entered_sales_outlet, parent, false);
        final ViewHolder viewHolder = new ViewHolder(recyclerItemView);

        recyclerItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalesOutlet selectedSalesOutlet = salesOutletsEntered.get(
                        recyclerView.getChildLayoutPosition(view));

                presenter.salesOutletSelectedByUser(selectedSalesOutlet);

                if (selectedSalesOutletCode.equals(selectedSalesOutlet.getCode())) {
                    selectedSalesOutletCode = "";
                }
                else {
                    selectedSalesOutletCode = selectedSalesOutlet.getCode();
                }

                notifyDataSetChanged();

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SalesOutlet salesOutlet = salesOutletsEntered.get(position);
        holder.salesOutletTitleTextView.setText(salesOutlet.getTitle());

        if (salesOutlet.getCode().equals(selectedSalesOutletCode)) {
            holder.parentLinearLayout.setBackgroundColor(holder.colorAccentOrangeTransparent);
        }
        else {
            holder.parentLinearLayout.setBackgroundColor(holder.colorWhiteTransparent);
        }
    }

    @Override
    public int getItemCount() {
        return salesOutletsEntered.size();
    }

    ////

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindColor(R.color.white_transparent)
        int colorWhiteTransparent;
        @BindColor(R.color.accent_orange_transparent)
        int colorAccentOrangeTransparent;

        @BindView(R.id.enteredSalesOutletListItem_rootLinearLayout)
        LinearLayout parentLinearLayout;
        @BindView(R.id.enteredSalesOutletListItem_salesOutletTitleTextView)
        TextView salesOutletTitleTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
