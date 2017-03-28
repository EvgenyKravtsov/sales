package kgk.mobile.external.greendao;

import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.domain.SalesOutlet;


public final class GreenDaoSqlite implements DatabaseService {

    private static final String TAG = GreenDaoSqlite.class.getSimpleName();

    private final DaoSession daoSession;
    private final List<Listener> listeners = new ArrayList<>();

    ////

    public GreenDaoSqlite(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "sqlite_database");
        Database database = helper.getWritableDb();
        daoSession = new DaoMaster(database).newSession();
    }

    //// DATABASE SERVICE

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void requestSalesOutlets() {
        List<SalesOutlet> salesOutlets = new ArrayList<>();
        List<SalesOutletEntity> entities = daoSession.getSalesOutletEntityDao().loadAll();

        for (SalesOutletEntity entity : entities) {
            SalesOutlet salesOutlet = new SalesOutlet(
                    entity.getLatitude(),
                    entity.getLongitude(),
                    entity.getCode());
            salesOutlets.add(salesOutlet);
        }

        for (Listener listener : listeners)
            listener.onSalesOutletsReceivedFromLocalStorage(salesOutlets);
    }

    @Override
    public void updateSalesOutlets(List<SalesOutlet> salesOutlets) {
        daoSession.getSalesOutletEntityDao().deleteAll();
        for (SalesOutlet salesOutlet : salesOutlets) {
            SalesOutletEntityDao salesOutletEntityDao = daoSession.getSalesOutletEntityDao();
            SalesOutletEntity entity = new SalesOutletEntity(
                    salesOutlet.getLatitude(),
                    salesOutlet.getLongitude(),
                    salesOutlet.getCode());
            salesOutletEntityDao.insert(entity);
        }
    }
}
