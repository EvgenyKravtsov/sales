package kgk.mobile.external.kgkservicesocketnio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.KgkService;
import kgk.mobile.domain.SalesOutlet;


public final class KgkServiceSocketNio implements KgkService {

    private static final String TAG = KgkServiceSocketNio.class.getSimpleName();

    //// KGK SERVICE

    @Override
    public List<SalesOutlet> getSalesOutlets() {
        // TODO Implement
        return new ArrayList<>();
    }

    @Override
    public boolean isConnectionAvailable() {
        // TODO Implement
        return true;
    }
}
