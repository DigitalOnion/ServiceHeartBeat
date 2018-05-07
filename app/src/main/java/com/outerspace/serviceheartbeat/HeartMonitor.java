package com.outerspace.serviceheartbeat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class HeartMonitor extends View {
    private static final int DEF_VER_DIV = 5;
    private static final int DEF_HOR_DIV = 10;
    private static final int DEF_GRAN = 20;
    private static final float DEF_CYCLES = 5;

    private int gridColor = 0;
    private int backgroundColor = 0;
    private int traceColor = 0;
    private int verticalDivisions = 0;
    private int horizontalDivisions = 0;
    private int granularity = 0;
    private float cycles = 0.0f;

    private int width = 0;
    private int height = 0;
    private float maxX = 0.0f;
    private float minX = 0.0f;
    private float maxY = 0.0f;
    private float minY = 0.0f;

    private Float[] fullTraceValues = null;
    private Float[] cacheTraceValues = null;
    private int idxCacheHead;
    private int idxCacheTail;
    private static final int MAX_CACHE = 200;
    private Float[] heartBeatValues = null;
    private float maxTraceVal = 0.0f;
    private float yFactor = 1.0f;

    //private ClockTicTac clockTicTac;

    public HeartMonitor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.HeartMonitor,
                0, 0
        );

        try{
            int defColor = ContextCompat.getColor(context, R.color.monitor_background);
            backgroundColor = a.getColor(R.styleable.HeartMonitor_background_color, defColor);
            defColor = ContextCompat.getColor(context, R.color.monitor_grid);
            gridColor = a.getColor(R.styleable.HeartMonitor_grid_color, defColor);
            defColor = ContextCompat.getColor(context, R.color.monitor_trace_line);
            traceColor = a.getColor(R.styleable.HeartMonitor_trace_color, defColor);

            verticalDivisions = a.getInt(R.styleable.HeartMonitor_vertical_divisions, DEF_VER_DIV);
            horizontalDivisions = a.getInt(R.styleable.HeartMonitor_horizontal_divisions, DEF_HOR_DIV);

            granularity = a.getInt(R.styleable.HeartMonitor_granulatiry, DEF_GRAN);
            cycles = a.getFloat(R.styleable.HeartMonitor_cycles, DEF_CYCLES);

            // granularity has the amount of X values to draw a heartbeat.
            //    just for convenience, one horizontal division fits a heart beat.
            //    then the whole screen will have granularity * horizontalDivision
            //    different X values.
            //
            heartBeatValues = new Float[granularity];
            fullTraceValues = new Float[granularity * horizontalDivisions];

            // calculate one Heart Beat series.
            float delta = (float) Math.PI * 2.0f / (granularity-1);
            float xStep = 1 / granularity;
            float sigma = 0.001f;
            float y = 0.0f;

            for(int i = 0; i < granularity; i++) {
                float xProg = 1.0f / granularity * i;
                float factor = 100.0f;
                if(xProg >= sigma)
                    factor = 1/xProg;
                heartBeatValues[i] = (float) Math.sin(y * cycles) * factor;
                if(Math.abs(heartBeatValues[i]) > maxTraceVal)
                    maxTraceVal = Math.abs(heartBeatValues[i]);
                y += delta;
                xProg += xStep;
            }

            // fill full trace with empties. Empty points are not traced.
            for(int i = 0; i < fullTraceValues.length; i++ ) {
                fullTraceValues[i] = null;
            }

        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        maxX = w;
        maxY = h;

        if (verticalDivisions > 2)
            yFactor = ((height / 2) * (verticalDivisions - 2) / verticalDivisions) / maxTraceVal;
    }

    private static final int STROKE_AXES_WIDTH = 4;
    private static final int STROKE_TRACE_WIDTH = 10;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float p;
        float delta;

        canvas.drawColor(backgroundColor);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(gridColor);
        paint.setStrokeWidth(STROKE_AXES_WIDTH);

        p = 0.0f;
        delta = width / horizontalDivisions;
        for (int i = 0; i < horizontalDivisions; i++) {
            canvas.drawLine(p, 0, p, height, paint);
            p = p + delta;
        }
        p = 0.0f;
        delta = height / verticalDivisions;
        for (int i = 0; i < verticalDivisions; i++) {
            canvas.drawLine(0, p, width, p, paint);
            p = p + delta;
        }

        ticTac();

        paint.setColor(traceColor);
        paint.setStrokeWidth(STROKE_TRACE_WIDTH);

        float vertOffset = height / 2;
        float xIni = 0.0f;
        delta = (float) width / (float) fullTraceValues.length;
        for(int i = 0; i < fullTraceValues.length - 1; i++) {
            if(fullTraceValues[ i ] != null && fullTraceValues[ i + 1 ] != null) {
                float startY = vertOffset - fullTraceValues[ i ] * yFactor;
                float stopY = vertOffset - fullTraceValues[ i + 1 ] * yFactor;
                canvas.drawLine(
                        xIni, startY, xIni + delta, stopY,
                        paint);
            }
            xIni += delta;
        }
    }

    public void addHeartBeat() {
        addCacheTraceValues(heartBeatValues);
    }

//    public void startTicking() {
//        if( clockTicTac == null ) {
//            clockTicTac = new ClockTicTac();
//        }
//        clockTicTac.execute(null, null, null);
//    }
//
//    public void stopTicking() {
//        if( clockTicTac != null ) {
//            clockTicTac.cancel(true);
//        }
//    }

    // Trace values are cached in a queue
    private void addCacheTraceValues(Float[] newValues) {
        if( cacheTraceValues == null ) {
            cacheTraceValues = new Float[MAX_CACHE];
            idxCacheHead = 0;
            idxCacheTail = 0;
        }
        for(int i = 0; i < newValues.length; i++) {
            cacheTraceValues[idxCacheTail] = newValues[i];
            idxCacheTail = (++idxCacheTail) % cacheTraceValues.length;
        }
    }

    private Float getNextCacheTraceValue() {
        if(idxCacheHead == idxCacheTail) {
            return null;
        } else {
            Float next = cacheTraceValues[idxCacheHead];
            idxCacheHead = (++idxCacheHead) % cacheTraceValues.length;
            return next;
        }
    }

    private int nTests = 0;

    private void ticTac() {
        for(int i = 1; i < fullTraceValues.length; i++ ) {
            fullTraceValues[ i-1 ] = fullTraceValues[ i ];
        }
        Float next = getNextCacheTraceValue();
        if( next == null )
            next = 0.0f;
        fullTraceValues[ fullTraceValues.length-1 ] = next;
    }

//    private class ClockTicTac extends AsyncTask<Void, Void, Void> {
//        private Void myVoid = null;
//        private boolean keepGoing = true;
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            keepGoing = true;
//            while(true) {
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                publishProgress();
//
//                if(!keepGoing) break;
//            }
//
//            return myVoid;
//        }
//
//        @Override
//        protected void onCancelled() {
//            keepGoing = false;
//            super.onCancelled();
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//            super.onProgressUpdate(values);
//            HeartMonitor.this.invalidate();
//        }
//
//    }
}
