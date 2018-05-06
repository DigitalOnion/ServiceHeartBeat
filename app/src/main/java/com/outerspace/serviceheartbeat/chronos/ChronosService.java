package com.outerspace.serviceheartbeat.chronos;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * ChronosService is, guess what, a service to make my practices on Services. I will take
 * it through the life cycle with help of the buttons of the Main Activity.
 *
 * Chronos' functionality is unknown at the time of this writing. it does something that
 * takes time.
 *
 * I am using an Explicit Intent to start the service. Implicit Intent Services are a
 * security breach and use the UI thread. I do not see any advantages on using them.
 */

public class ChronosService extends Service implements IChronosService {

    private final IBinder binder = new ChronosServiceBinder();
    private boolean bound = false;
    private ArrayList<IChronosClient> clientList = new ArrayList<>();

    // IBinder interface through Binder
    public class ChronosServiceBinder extends Binder {
        public ChronosService getService() {
            return ChronosService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        bound = true;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bound = false;
        return super.onUnbind(intent);
    }

    @Override
    public void addClient(IChronosClient client) {
        clientList.add( client );
    }

    @Override
    public void removeClient(IChronosClient client) {
        clientList.remove( client );
    }

    // public interface
    @Override
    public boolean isBound() {
        return bound;
    }

    @Override
    public void pingService() {
        String pingResponse = "Hello World!";
        for(IChronosClient client : clientList) {
            client.pingResponse( pingResponse );
        }
    }

    @Override
    public void terminateService() {
        this.stopSelf();
    }

}
