package com.outerspace.serviceheartbeat.view;

import com.outerspace.serviceheartbeat.presenter.IMainPresenter;
import com.outerspace.serviceheartbeat.presenter.MainPresenter;
import com.outerspace.serviceheartbeat.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements IMainView {
    private IMainPresenter presenter = null;
    private Button[] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttons = new Button[] {
                (Button) findViewById(R.id.start),
                (Button) findViewById(R.id.bind),
                (Button) findViewById(R.id.ping),
                (Button) findViewById(R.id.unbind),
                (Button) findViewById(R.id.terminate),
        };

        presenter = new MainPresenter(this);
    }

    @Override
    protected void onStart() {
        enableButton(0);
        super.onStart();
    }

    @Override
    public Context getContext() {
        return this.getBaseContext();
    }

    private void enableButton(int... iBtns) {
        for(Button btn : buttons)
            btn.setEnabled(false);
        for(int i: iBtns)
            buttons[i].setEnabled(true);
    }

    public void onClickBtnStart(View view) {
        enableButton(1);
        presenter.doStartService();
    }

    public void onClickBtnBind(View view) {
        enableButton(2, 3);
        presenter.doBindService();
    }

    public void onClickBtnPing(View view) {
        enableButton(2, 3);
        presenter.doPingService();
    }

    public void onClickBtnUnbind(View view) {
        enableButton(4);
        presenter.doUnbindService();
    }

    public void onClickBtnTerminate(View view) {
        enableButton(0);
        presenter.doTerminateService();
    }

}
