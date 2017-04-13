package kgk.mobile.presentation.view.mainscreen.userboard;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kgk.mobile.R;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.presentation.view.mainscreen.dialog.AddedValueDialog;

final class UserOperationsRecyclerAdapter
        extends RecyclerView.Adapter<UserOperationsRecyclerAdapter.ViewHolder> {

    private static final String TAG = UserOperationsRecyclerAdapter.class.getSimpleName();

    private List<UserOperation> userOperations = new ArrayList<>();
    private List<UserOperation> selectedUserOperations = new ArrayList<>();
    private int addedValue;

    private final RecyclerView recyclerView;
    private final Activity activity;

    ////

    UserOperationsRecyclerAdapter(RecyclerView recyclerView, Activity activity) {
        this.recyclerView = recyclerView;
        this.activity = activity;

        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                selectedUserOperations.clear();
                addedValue = 0;
            }
        });
    }

    ////

    void setUserOperations(List<UserOperation> userOperations) {
        this.userOperations = userOperations;
    }

    List<UserOperation> getSelectedUserOperations() {
        return selectedUserOperations;
    }

    int getAddedValue() {
        return addedValue;
    }

    //// RECYCLER VIEW ADAPTER

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View recyclerItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user_operation, parent, false);
        final ViewHolder viewHolder = new ViewHolder(recyclerItemView);

        recyclerItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserOperation selectedUserOperation = userOperations.get(
                        recyclerView.getChildLayoutPosition(recyclerItemView));

                if (selectedUserOperation.getId() == 3)
                    onClickUserOperationAddedValue(viewHolder, selectedUserOperation);
                else
                    onClickUserOperation(viewHolder, selectedUserOperation);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserOperation userOperation = userOperations.get(position);
        holder.userOperationTitleTextView.setText(userOperation.getTitle());
        holder.checkBox.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return userOperations.size();
    }

    //// PRIVATE

    private void onClickUserOperation(ViewHolder viewHolder,
                                      UserOperation selectedUserOperation) {

        boolean isSelected = viewHolder.checkBox.isChecked();

        if (isSelected) {
            viewHolder.checkBox.setChecked(false);
            selectedUserOperations.remove(selectedUserOperation);
        } else {
            viewHolder.checkBox.setChecked(true);
            selectedUserOperations.add(selectedUserOperation);
        }
    }

    private void onClickUserOperationAddedValue(final ViewHolder viewHolder,
                                                final UserOperation selectedUserOperation) {

        boolean isSelected = viewHolder.checkBox.isChecked();

        if (isSelected) {
            viewHolder.checkBox.setChecked(false);
            selectedUserOperations.remove(selectedUserOperation);
            addedValue = 0;
            viewHolder.userOperationTitleTextView.setText(selectedUserOperation.getTitle());
        }
        else {
            AddedValueDialog addedValueDialog = new AddedValueDialog(activity,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewHolder.checkBox.setChecked(true);
                            String title = viewHolder.userOperationTitleTextView.getText().toString();
                            String addedValueAsString = ((EditText) view).getText().toString();
                            title += " (" + addedValueAsString + " руб.)";
                            viewHolder.userOperationTitleTextView.setText(title);
                            selectedUserOperations.add(selectedUserOperation);

                            try { addedValue = Integer.valueOf(addedValueAsString); }
                            catch (IllegalArgumentException e) { addedValue = 0; }
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });

            addedValueDialog.show();
        }
    }

    ////

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userOperationListItem_checkBox)
        CheckBox checkBox;
        @BindView(R.id.userOperationListItem_userOperationTitleTextView)
        TextView userOperationTitleTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
