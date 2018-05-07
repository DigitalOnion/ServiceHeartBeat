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

public class MainPresenter implements IMainPresenter, IChronosClient {

    private IMainView view;

    private IChronosService chronosService = null;
    private MyServiceConnection connection = new MyServiceConnection();

    private MainPresenter() { }

    public MainPresenter(IMainView view) {
        this.view = view;
    }

    @Override
    public void doStartService() {
        Intent intent = new Intent(view.getContext(), ChronosService.class);
        view.getContext().startService(intent);

        view.startMonitor();
    }

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
        chronosService.pingService();
    }

    @Override
    public void doUnbindService() {
        view.getContext().unbindService(connection);
    }

    @Override
    public void doTerminateService() {
        if(chronosService != null)
            chronosService.terminateService();

        view.stopMonitor();
    }

    @Override
    public void pingResponse(String response) {
        view.heartBeat();
    }

    private class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ChronosService.ChronosServiceBinder chronosBinder =
                    (ChronosService.ChronosServiceBinder) iBinder;
            chronosService = chronosBinder.getService();
            chronosService.addClient(MainPresenter.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }

        @Override
        public void onBindingDied(ComponentName name) {
        }

    }

}
