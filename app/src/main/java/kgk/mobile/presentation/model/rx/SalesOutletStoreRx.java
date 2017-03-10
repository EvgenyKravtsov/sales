package kgk.mobile.presentation.model.rx;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kgk.mobile.domain.DatabaseService;
import kgk.mobile.domain.KgkService;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.presentation.model.SalesOutletStore;


public final class SalesOutletStoreRx implements SalesOutletStore {

    private static final String TAG = SalesOutletStoreRx.class.getSimpleName();

    private final KgkService kgkService;
    private final DatabaseService databaseService;

    ////

    @Inject public SalesOutletStoreRx(KgkService kgkService, DatabaseService databaseService) {
        this.kgkService = kgkService;
        this.databaseService = databaseService;
    }

    //// POINT STORE

    @Override
    public void requestSalesOutlets(final Listener listener) {
        Single<List<SalesOutlet>> salesOutletsSingle;

        if (kgkService.isConnectionAvailable()) {
            salesOutletsSingle = Single.just(kgkService.getSalesOutlets());
        }
        else {
            salesOutletsSingle = Single.just(databaseService.getSalesOutlets());
        }

        salesOutletsSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<SalesOutlet>>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onSuccess(List<SalesOutlet> salesOutlets) {
                        listener.onSalesOutletsReceived(salesOutlets);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO Implement
                        Log.d(TAG, "onError: Sales Outlets Observer Error");
                    }
                });
    }
}
