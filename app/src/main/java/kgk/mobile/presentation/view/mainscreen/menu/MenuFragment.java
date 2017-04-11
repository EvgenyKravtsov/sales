package kgk.mobile.presentation.view.mainscreen.menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import kgk.mobile.DependencyInjection;
import kgk.mobile.R;


public final class MenuFragment extends Fragment implements MenuContract.View {

    private static final String TAG = MenuFragment.class.getSimpleName();

    @BindString(R.string.menuFragment_lastLocationDateText)
    String lastLocationDateText;
    @BindString(R.string.menuFragment_invalidLastLocationDateText)
    String invalidLastLocationDateText;

    @BindView(R.id.menuFragment_lastLocationDateTextView)
    TextView lastLocationTextView;

    private MenuContract.Presenter presenter;

    //// FRAGMENT

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = DependencyInjection.provideMenuPresenter();
        presenter.attachView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, view);
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

    //// MENU VIEW

    @Override
    public void displayLastLocationDate(String text) {
        Log.d(TAG, "displayLastLocationDate: ");
        lastLocationTextView.setText(String.format(lastLocationDateText, text));
    }

    @Override
    public void displayInvalidLastLocationDate() {
        Log.d(TAG, "displayInvalidLastLocationDate: ");
        lastLocationTextView.setText(String.format(lastLocationDateText, invalidLastLocationDateText));
    }
}
