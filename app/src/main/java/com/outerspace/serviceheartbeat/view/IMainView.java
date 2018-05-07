package com.outerspace.serviceheartbeat.view;

import android.content.Context;

public interface IMainView {

    public Context getContext();

//    public void startMonitor();
//
//    public void stopMonitor();

    public void heartBeat();

    public void ticTok();
}
