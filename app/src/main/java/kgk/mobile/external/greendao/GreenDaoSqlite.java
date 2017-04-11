package kgk.mobile.external.greendao;

import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.database.Database;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.App;
import kgk.mobile.DependencyInjection;
import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.service.SystemService;
import kgk.mobile.external.network.json.JsonProtocol;


public final class GreenDaoSqlite implements DatabaseService {

    private static final String TAG = GreenDaoSqlite.class.getSimpleName();

    private final DaoSession daoSession;
    private final List<Listener> listeners = new ArrayList<>();
    private final SystemService systemService;

    ////

    public GreenDaoSqlite(Context context, SystemService systemService) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "sqlite_database");
        Database database = helper.getWritableDb();
        daoSession = new DaoMaster(database).newSession();

        this.systemService = systemService;
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

        for (Listener listener : listeners) {
            listener.onSalesOutletsReceivedFromLocalStorage(salesOutlets);
        }
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

    @Override
    public void insertSalesOutletAttendance(SalesOutletAttendance attendance) {
        SalesOutletAttendanceEntityDao salesOutletAttendanceEntityDao =
                daoSession.getSalesOutletAttendanceEntityDao();
        SalesOutletAttendanceEntity entity = new SalesOutletAttendanceEntity(0L,
                new JsonProtocol(DependencyInjection.provideSystemService())
                        .createSalesOutletAttendanceMessage(attendance).toString(),
                false);
        salesOutletAttendanceEntityDao.insert(entity);
    }

    @Override
    public void requestNonSynchronizedSalesOutletAttendances() {
        List<String> attendanceMessages = new ArrayList<>();

        List<SalesOutletAttendanceEntity> entities = daoSession
                .getSalesOutletAttendanceEntityDao().loadAll();

        for (SalesOutletAttendanceEntity entity : entities) {
            if (!entity.getIsSynchronized()) {
                attendanceMessages.add(entity.getAttendanceJson());
            }
        }

        for (Listener listener : listeners) {
            listener.onNonSynchronizedSalesOutletAttendanceMessagesReceivedFromLocalStorage(attendanceMessages);
        }
    }

    @Override
    public void confirmSalesOutletAttendance(String eventId) {
        List<SalesOutletAttendanceEntity> entities = daoSession
                .getSalesOutletAttendanceEntityDao().loadAll();

        for (SalesOutletAttendanceEntity entity : entities) {
            try {
                JSONObject messageJson = new JSONObject(entity.getAttendanceJson());
                String exitTime = messageJson.getJSONObject("EVENT").getString("TIME");
                String deviceId = systemService.getDeviceId().substring(5);

                if ((exitTime + deviceId).equals(eventId)) {
                    entity.setIsSynchronized(true);
                }

                daoSession.update(entity);
            }
            catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "confirmSalesOutletAttendance: JSON Exception");
            }
            catch (SecurityException e) {
                e.printStackTrace();
                Log.d(TAG, "confirmSalesOutletAttendance: Security Exception");
            }
        }
    }
}
