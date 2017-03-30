package kgk.mobile.external.greendao;

import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.UserOperation;
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
                    entity.getCode(),
                    entity.getTitle());
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
                    salesOutlet.getCode(),
                    salesOutlet.getTitle());
            salesOutletEntityDao.insert(entity);
        }
    }

    @Override
    public void requestUserOperations() {
        List<UserOperation> userOperations = new ArrayList<>();
        List<UserOperationEntity> entities = daoSession.getUserOperationEntityDao().loadAll();

        for (UserOperationEntity entity : entities) {
            UserOperation userOperation = new UserOperation(
                    entity.getId(),
                    entity.getTitle());
            userOperations.add(userOperation);
        }

        for (Listener listener : listeners)
            listener.onUserOperationsReceivedFromLocalStorage(userOperations);
    }

    @Override
    public void updateUserOperations(List<UserOperation> userOperations) {
        daoSession.getUserOperationEntityDao().deleteAll();
        for (UserOperation userOperation : userOperations) {
            UserOperationEntityDao userOperationEntityDao = daoSession.getUserOperationEntityDao();
            UserOperationEntity entity = new UserOperationEntity(
                    userOperation.getId(),
                    userOperation.getTitle());
            userOperationEntityDao.insert(entity);
        }
    }
}
