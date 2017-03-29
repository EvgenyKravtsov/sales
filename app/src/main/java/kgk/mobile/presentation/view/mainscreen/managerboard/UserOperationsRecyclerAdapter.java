package kgk.mobile.presentation.view.mainscreen.managerboard;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kgk.mobile.R;
import kgk.mobile.domain.UserOperation;

final class UserOperationsRecyclerAdapter
        extends RecyclerView.Adapter<UserOperationsRecyclerAdapter.ViewHolder> {

    private List<UserOperation> userOperations = new ArrayList<>();

    ////

    public void setUserOperations(List<UserOperation> userOperations) {
        this.userOperations = userOperations;
    }

    //// RECYCLER VIEW ADAPTER

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View recyclerItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user_operation, parent, false);
        return new ViewHolder(recyclerItemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserOperation userOperation = userOperations.get(position);
        holder.userOperationTitleTextView.setText(userOperation.getTitle());
    }

    @Override
    public int getItemCount() {
        return userOperations.size();
    }

    ////

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userOperationListItem_userOperationTitleTextView)
        TextView userOperationTitleTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
