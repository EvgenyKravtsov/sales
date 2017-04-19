package kgk.mobile.presentation.view.mainscreen;


import android.support.v7.widget.RecyclerView;
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
import kgk.mobile.external.util.DateFormatter;

final class EnteredSalesOutletsRecyclerAdapter
        extends RecyclerView.Adapter<EnteredSalesOutletsRecyclerAdapter.ViewHolder> {

    private final List<SalesOutlet> enteredSalesOutlets = new ArrayList<>();
    private final RecyclerView recyclerView;
    private final EnteredSalesOutletClickListener onClickListener;

    private SalesOutlet selectedSalesOutlet;
    private long salesOutletAttendanceBeginDateUnixSeconds;

    ////

    EnteredSalesOutletsRecyclerAdapter(RecyclerView recyclerView,
                                       EnteredSalesOutletClickListener onClickListener) {
        this.recyclerView = recyclerView;
        this.onClickListener = onClickListener;
    }

    ////

    void updateEnteredSalesOutlets(List<SalesOutlet> salesOutlets) {
        enteredSalesOutlets.clear();
        enteredSalesOutlets.addAll(salesOutlets);
        notifyDataSetChanged();
    }

    void updateSelectedSalesOutlet(SalesOutlet salesOutlet, long salesOutletAttendanceBeginDateUnixSeconds) {
        this.selectedSalesOutlet = salesOutlet;
        this.salesOutletAttendanceBeginDateUnixSeconds = salesOutletAttendanceBeginDateUnixSeconds;
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
                int itemIndex = recyclerView.getChildLayoutPosition(view);
                SalesOutlet selectedSalesOutlet = enteredSalesOutlets.get(itemIndex);

                if (EnteredSalesOutletsRecyclerAdapter.this.selectedSalesOutlet != null && EnteredSalesOutletsRecyclerAdapter.this.selectedSalesOutlet.equals(selectedSalesOutlet)) onClickListener.onClickEnteredSalesOutlet(null);
                else onClickListener.onClickEnteredSalesOutlet(selectedSalesOutlet);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SalesOutlet salesOutlet = enteredSalesOutlets.get(position);

        if (selectedSalesOutlet == null) {
            holder.rootLinearLayout.setBackgroundColor(holder.colorWhiteTransparent);
            holder.salesOutletTitleTextView.setText(salesOutlet.getTitle());
            holder.salesOutletTitleTextView.setTextSize(20);
            return;
        }

        if (selectedSalesOutlet.equals(salesOutlet)) {
            holder.rootLinearLayout.setBackgroundColor(holder.colorAccentOrangeTransparent);
            String dateString = new DateFormatter().unixSecondsToFormattedString(salesOutletAttendanceBeginDateUnixSeconds, "yyyy-MM-dd HH:mm:ss");
            holder.salesOutletTitleTextView.setText(String.format("%s\nВход: %s", salesOutlet.getTitle(), dateString));
            holder.salesOutletTitleTextView.setTextSize(17);
        }
        else {
            holder.rootLinearLayout.setBackgroundColor(holder.colorWhiteTransparent);
            holder.salesOutletTitleTextView.setText(salesOutlet.getTitle());
            holder.salesOutletTitleTextView.setTextSize(20);
        }
    }

    @Override
    public int getItemCount() {
        return enteredSalesOutlets.size();
    }

    //// VIEW HOLDER

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindColor(R.color.white_transparent)
        int colorWhiteTransparent;
        @BindColor(R.color.accent_orange_transparent)
        int colorAccentOrangeTransparent;

        @BindView(R.id.enteredSalesOutletListItem_rootLinearLayout)
        LinearLayout rootLinearLayout;
        @BindView(R.id.enteredSalesOutletListItem_salesOutletTitleTextView)
        TextView salesOutletTitleTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
