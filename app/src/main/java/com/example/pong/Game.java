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

public class Game extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread mainThread;
    private int SCREEN_HEIGHT;
    private int SCREEN_WIDTH;
    private Ball ball;
    private Object player;
    private Object opponent;
    private Object goalPostA;
    private Object goalPostB;
    private Object goalPostC;
    private Object goalPostD;

    public Game(Context context) {
        super(context);

        getHolder().addCallback(this);

        mainThread = new MainThread(getHolder(), this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        SCREEN_HEIGHT = displayMetrics.heightPixels;
        SCREEN_WIDTH = displayMetrics.widthPixels;

        ball = new Ball(22, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);

        int paddleWidth = (int)Math.round(SCREEN_WIDTH * 0.3);
        int paddleHeight = (int)Math.round(SCREEN_HEIGHT * 0.04);

        int playerX = SCREEN_WIDTH / 2;
        int playerY = (int)Math.round(SCREEN_HEIGHT - SCREEN_HEIGHT * 0.2);

        player = new Object(paddleWidth, paddleHeight, playerX, playerY);

        int opponentX = SCREEN_WIDTH / 2;
        int opponentY = (int)Math.round(SCREEN_HEIGHT * 0.2);

        opponent = new Object(paddleWidth, paddleHeight, opponentX, opponentY);

        int goalPostWidthHeight = (int)Math.round(SCREEN_HEIGHT * 0.1);
        goalPostA = new Object(goalPostWidthHeight, goalPostWidthHeight, goalPostWidthHeight / 2, goalPostWidthHeight / 2);
        goalPostB = new Object(goalPostWidthHeight, goalPostWidthHeight, SCREEN_WIDTH - goalPostWidthHeight / 2, goalPostWidthHeight / 2);
        goalPostC = new Object(goalPostWidthHeight, goalPostWidthHeight, goalPostWidthHeight / 2, SCREEN_HEIGHT - goalPostWidthHeight / 2);
        goalPostD = new Object(goalPostWidthHeight, goalPostWidthHeight, SCREEN_WIDTH - goalPostWidthHeight / 2, SCREEN_HEIGHT - goalPostWidthHeight / 2);

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
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            player.update(new Point((int)event.getX(), (int)event.getY() - 150), SCREEN_WIDTH, SCREEN_HEIGHT);
        }

        return true;

        /* switch(event.getAction()) { `1
            // case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                player.update((int)event.getX(), (int)event.getY() - 150, SCREEN_WIDTH, SCREEN_HEIGHT);
        }
        return true;
        // return super.onTouchEvent(event); */
    }

    public void update() {
        ball.updatePos();

        opponent.update(OpponentBrain.getOpponentPos(opponent, ball, SCREEN_HEIGHT), SCREEN_WIDTH, SCREEN_HEIGHT);

        ball.handleCollisionWall(SCREEN_WIDTH, SCREEN_HEIGHT);

        ball.handleCollision(player);
        ball.handleCollision(opponent);

        ball.handleCollision(goalPostA);
        ball.handleCollision(goalPostB);
        ball.handleCollision(goalPostC);
        ball.handleCollision(goalPostD);
    }

    public Point getOpponentPos(Object opponent, Ball ball) {
        Point opponentPos = opponent.getPos();
        Point ballPos = ball.getPos();

        if (ballPos.y > opponentPos.y && opponentPos.y < (int)Math.round(SCREEN_HEIGHT * 0.3))
            opponentPos.y += 5;
        if (ballPos.y < opponentPos.y)
            opponentPos.y -= 10;
        if (ballPos.x > opponentPos.x)
            opponentPos.x += 20;
        if (ballPos.x < opponentPos.x)
            opponentPos.x -= 20;

        return opponentPos;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(Color.WHITE);

        ball.draw(canvas);
        player.draw(canvas);
        opponent.draw(canvas);
        goalPostA.draw(canvas);
        goalPostB.draw(canvas);
        goalPostC.draw(canvas);
        goalPostD.draw(canvas);
    }
}
