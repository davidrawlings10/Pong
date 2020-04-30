package com.example.pong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Surface extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread mainThread;
    private Object player;
    private Ball ball;
    private int SCREEN_HEIGHT;
    private int SCREEN_WIDTH;

    public Surface(Context context) {
        super(context);

        getHolder().addCallback(this);

        mainThread = new MainThread(getHolder(), this);

        player = new Object(400, 100, 300, 500);
        ball = new Ball(25, 300, 300);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        SCREEN_HEIGHT = displayMetrics.heightPixels;
        SCREEN_WIDTH = displayMetrics.widthPixels;

        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mainThread = new MainThread(getHolder(), this);
        mainThread.setRunning(true);
        mainThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // boolean retry = true;
        while (true) {
            try {
                mainThread.setRunning(false);
                mainThread.join();
            } catch (Exception e) { e.printStackTrace(); }
            // retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            // case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                player.update((int)event.getX(), (int)event.getY() - 150);
        }
        return true;
        // return super.onTouchEvent(event);
    }

    public void update() {
        ball.updatePos();
        ball.handleCollisionWall(SCREEN_WIDTH, SCREEN_HEIGHT);
        ball.handleCollision(player);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(Color.WHITE);

        player.draw(canvas);
        ball.draw(canvas);
    }
}
