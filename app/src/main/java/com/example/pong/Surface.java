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
    private int ball_speed_x;
    private int ball_speed_y;

    public Surface(Context context) {
        super(context);

        getHolder().addCallback(this);

        player = new Object(400, 100, 300, 500);
        ball = new Ball(25, 300, 300);

        mainThread = new MainThread(getHolder(), this);

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
        Point ballPoint = ball.getPos();

        ballPoint.x = ballPoint.x + ball.getSpeedX();
        ballPoint.y = ballPoint.y + ball.getSpeedY();

        ball.setPos(ballPoint);

        int yplayerSpeed = player.getLastPos().y - player.getPos().y;
        int xplayerSpeed = player.getLastPos().x - player.getPos().x;

        if (ball.getRight() > SCREEN_WIDTH && ball.getSpeedX() > 0 || ball.getLeft() < 0 && ball.getSpeedX() < 0)
            ball.setSpeedX(ball.getSpeedX() * -1);
        if (ball.getBottom() > SCREEN_HEIGHT && ball.getSpeedY() > 0 || ball.getTop() < 0 && ball.getSpeedY() < 0)
            ball.setSpeedY(ball.getSpeedY() * -1);

        ball.handleCollision(player);

        /*int bottom_distance = player.getTop() - ball.getBottom();
        int top_distance = ball.getTop() - player.getBottom();
        int right_distance = player.getLeft() - ball.getRight();
        int left_distance = ball.getLeft() - player.getRight();

        if (bottom_distance < 0 && top_distance < 0 && right_distance < 0 && left_distance < 0) {
            Collision collision = Collision.TOP;
            int min = Math.abs(top_distance);

            if (Math.abs(bottom_distance) < min) {
                collision = Collision.BOTTOM;
                min = Math.abs(bottom_distance);
            }

            if (Math.abs(left_distance) < min) {
                collision = Collision.LEFT;
                min = Math.abs(left_distance);
            }

            if (Math.abs(right_distance) < min) {
                collision = Collision.RIGHT;
                // min = Math.abs(right_distance);
            }

            if (collision.equals(Collision.TOP)) {
                int newSpeedY = Math.min(-player.getySpeed() + 10, 40);
                System.out.println(newSpeedY);
                ball.setSpeedY(newSpeedY);
                ball.setSpeedX(ball.getSpeedX() - player.getxSpeed() / 2);
                ballPoint.y = player.getBottom() + ball.getRADIUS();
            } else if (collision.equals(Collision.BOTTOM)) {
                int newSpeedY = Math.max(-player.getySpeed() - 10, -40);
                System.out.println(newSpeedY);
                ball.setSpeedY(newSpeedY);
                ball.setSpeedX(ball.getSpeedX() - player.getxSpeed() / 2);
                ballPoint.y = player.getTop() - ball.getRADIUS();
            } else if (collision.equals(Collision.LEFT)) {
                int newSpeedX = Math.min(-player.getxSpeed() / 2 + 10, 20);
                System.out.println(newSpeedX);
                ball.setSpeedX(newSpeedX);
                ball.setSpeedY(ball.getSpeedY() - player.getySpeed() / 2);
                ballPoint.x = player.getRight() + ball.getRADIUS();
            } else if (collision.equals(Collision.RIGHT)) {
                int newSpeedX = Math.max(-player.getxSpeed() / 2 - 10, -20);
                ball.setSpeedX(newSpeedX);
                ball.setSpeedY(ball.getSpeedY() - player.getySpeed() / 2);
                ballPoint.x = player.getLeft() - ball.getRADIUS();
            }

            System.out.println("x:"+ball.getSpeedX() + ", y:" + ball.getSpeedY());
        }*/
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(Color.WHITE);

        /*Rect rect = new Rect(playerPoint.x - 50, playerPoint.y - 50, playerPoint.x + 50, playerPoint.y + 50);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        canvas.drawRect(rect, paint);*/

        player.draw(canvas);
        ball.draw(canvas);
    }
}
