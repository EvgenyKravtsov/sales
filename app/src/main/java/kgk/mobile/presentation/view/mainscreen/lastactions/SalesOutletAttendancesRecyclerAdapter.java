package kgk.mobile.presentation.view.mainscreen.lastactions;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import kgk.mobile.R;
import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.external.util.DateFormatter;

final class SalesOutletAttendancesRecyclerAdapter
        extends RecyclerView.Adapter<SalesOutletAttendancesRecyclerAdapter.ViewHolder> {

    private static final String TAG = SalesOutletAttendancesRecyclerAdapter.class.getSimpleName();

    private List<SalesOutletAttendance> salesOutletAttendances = new ArrayList<>();

    ////

    void setSalesOutletAttendances(List<SalesOutletAttendance> salesOutletAttendances) {
        this.salesOutletAttendances = salesOutletAttendances;
        Collections.sort(this.salesOutletAttendances, new Comparator<SalesOutletAttendance>() {
            @Override
            public int compare(SalesOutletAttendance attendance, SalesOutletAttendance t1) {
                return (int) (t1.getBeginDateUnixSeconds() - attendance.getBeginDateUnixSeconds());
            }
        });
    }

    //// RECYCLER VIEW ADAPTER

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View recyclerItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_sales_outlet_attendance, parent, false);
        return new ViewHolder(recyclerItemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SalesOutletAttendance attendance = salesOutletAttendances.get(position);
        holder.salesOutletTitleTextView.setText(
                String.format(holder.salesOutletTitleText,
                        attendance.getAttendedSalesOutlet().getTitle()));
        holder.salesOutletCodeTextView.setText(
                String.format(holder.salesOutletCodeText,
                        attendance.getAttendedSalesOutlet().getCode()));
        holder.attendanceDateTextView.setText(
                String.format(holder.attendanceDateText,
                              new DateFormatter().unixSecondsToFormattedString(
                                      attendance.getBeginDateUnixSeconds(), "yyyy-MM-dd HH:mm:ss"),
                              new DateFormatter().unixSecondsToFormattedString(
                                      attendance.getEndDateUnixSeconds(), "yyyy-MM-dd HH:mm:ss")));
    }

    @Override
    public int getItemCount() {
        return salesOutletAttendances.size();
    }

    ////

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindString(R.string.lastActionsFragment_salesOutletTitleText)
        String salesOutletTitleText;
        @BindString(R.string.lastActionsFragment_salesOutletCodeText)
        String salesOutletCodeText;
        @BindString(R.string.lastActionsFragment_attendanceDateText)
        String attendanceDateText;

        @BindView(R.id.salesOutletAttendanceListItem_salesOutletTitleTextView)
        TextView salesOutletTitleTextView;
        @BindView(R.id.salesOutletAttendanceListItem_salesOutletCodeTextView)
        TextView salesOutletCodeTextView;
        @BindView(R.id.salesOutletAttendanceListItem_attendanceDateTextView)
        TextView attendanceDateTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
