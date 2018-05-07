package com.outerspace.serviceheartbeat.chronos;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * ChronosService is, guess what: a service to make my practices on Services.
 *
 * You take it along the Services life-cycle by tapping on the buttons on the
 * Main Activity.
 *
 * Chronos' functionality is unknown at the time of this writing. it does something that
 * takes time.
 *
 * I am using an Explicit Intent to start the service. Implicit Intent Services are a
 * security breach and use the UI thread. I do not see any advantages on using them.
 */

public class ChronosService extends Service implements IChronosService {

    private AsyncTicTok ticTok = null;

    private final IBinder binder = new ChronosServiceBinder();
    private boolean bound = false;
    private ArrayList<IChronosClient> clientList = new ArrayList<>();

    // IBinder interface by extending Binder
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
        ticTok.cancel(true);
        bound = false;
        return super.onUnbind(intent);
    }

    /**
     * IChronosService
     *
     * public interface
     *
     */

    @Override
    public void addClient(IChronosClient client) {
        clientList.add( client );
    }

    @Override
    public void removeClient(IChronosClient client) {
        clientList.remove( client );
    }


    @Override
    public void execute() {
        if( ticTok != null ) {
            ticTok.cancel(true);
            ticTok = null;
        }
        ticTok = new AsyncTicTok();

        IChronosClient[] clients = new IChronosClient[clientList.size()];
        clients = clientList.toArray(clients);
        ticTok = new AsyncTicTok();
        ticTok.execute(clients);
    }

    @Override
    public void pingService() {

    }

    @Override
    public void terminateService() {
        this.stopSelf();
    }

    // just for cultural enhancement: the three <Generics...> of AsyncTask,
    // correspond to: doInBackground, onProgressUpdate and onPostExecute

    private static class AsyncTicTok extends AsyncTask<IChronosClient, IChronosClient, Void> {
        private static final long MINIMUM_WAIT = 20;
        @Override
        protected Void doInBackground(IChronosClient... clients) {

            Long[] timeTotals = new Long[ clients.length ];
            for(int i = 0; i < timeTotals.length; i++) {
                timeTotals[i] = clients[i].getInterval();  // put 0 if want to wait to get tics
            }

            while (true) {
                try {
                    Thread.sleep( MINIMUM_WAIT );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i < clients.length; i++) {
                    if (clients[i].isEnabled()) {
                        timeTotals[i] += MINIMUM_WAIT;
                        if(timeTotals[i] >= clients[i].getInterval()) {
                            publishProgress( clients[i] );
                            timeTotals[i] = 0l;
                        }
                    }
                }
            }
            //return null;  // it never returns...
        }

        @Override
        protected void onProgressUpdate(IChronosClient... elapsedClients) {
            super.onProgressUpdate(elapsedClients);

            IChronosClient client = elapsedClients[0];   // there is only one per call

            client.chronosResponse("Sup");

        }
    }


}
