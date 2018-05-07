package com.outerspace.serviceheartbeat.presenter;

import com.outerspace.serviceheartbeat.chronos.ChronosService;
import com.outerspace.serviceheartbeat.chronos.IChronosClient;
import com.outerspace.serviceheartbeat.chronos.IChronosService;
import com.outerspace.serviceheartbeat.view.IMainView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

public class MainPresenter implements IMainPresenter {
    private static final String TIC_TAC_CLIENT = "TIC_TAC";
    private static final Long TIC_TAC_INTERVAL = 20l;

    private static final String HEART_BEAT_CLIENT = "HEART_BEAT";
    private static final Long HEART_BEAT_INTERVAL = 1000l;

    private IMainView view;

    private IChronosClient ticClient;
    private IChronosClient heartBeatClient;

    private IChronosService chronosService = null;
    private MyServiceConnection connection = new MyServiceConnection();

    private MainPresenter() { }

    public MainPresenter(IMainView view) {
        this.view = view;
    }

//    @Override
//    public void doStartService() {
//        Intent intent = new Intent(view.getContext(), ChronosService.class);
//        view.getContext().startService(intent);
//
//
//    }

    @Override
    public void doBindService() {
        Intent intent = new Intent(
                view.getContext(),
                ChronosService.class);
        view.getContext().bindService(
                intent,
                connection,
                view.getContext().BIND_AUTO_CREATE );
    }

    @Override
    public void doPingService() {
        //chronosService.pingService();
        heartBeatClient.enable( !heartBeatClient.isEnabled() );
    }

    @Override
    public void doUnbindService() {
        view.getContext().unbindService(connection);
    }

//    @Override
//    public void doTerminateService() {
//        if(chronosService != null)
//            chronosService.terminateService();
//
//    }

    private class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            ticClient = new MyChronosClient(TIC_TAC_CLIENT, TIC_TAC_INTERVAL) {
                @Override
                public void chronosResponse(String response) {
                    view.ticTok();
                }
            };

            heartBeatClient = new MyChronosClient(HEART_BEAT_CLIENT, HEART_BEAT_INTERVAL) {
                @Override
                public void chronosResponse(String response) {
                    view.heartBeat();
                }
            };

            ChronosService.ChronosServiceBinder chronosBinder =
                    (ChronosService.ChronosServiceBinder) iBinder;
            chronosService = chronosBinder.getService();
            chronosService.addClient(ticClient);
            chronosService.addClient(heartBeatClient);
            ticClient.enable(true);
            chronosService.execute();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) { }

        @Override
        public void onBindingDied(ComponentName name) { }
    }

    private abstract class MyChronosClient implements IChronosClient {
        private Boolean enabled = false;
        private String name = null;
        private Long interval = 0l;

        public void enable(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public MyChronosClient(String name, Long interval) {
            this.name = name;
            this.interval = interval;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setInterval(Long millSecs) {
            this.interval = millSecs;
        }

        @Override
        public Long getInterval() {
            return interval;
        }

    }


}
