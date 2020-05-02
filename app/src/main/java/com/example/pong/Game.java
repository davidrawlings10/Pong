package com.example.pong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private OpponentBrain opponentBrain;
    private int brain;
    private int playerScore;
    private int opponentScore;

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

        ball = new Ball(22);

        int paddleWidth = (int)Math.round(SCREEN_WIDTH * 0.3);
        int paddleHeight = (int)Math.round(SCREEN_HEIGHT * 0.04);

        int playerX = SCREEN_WIDTH / 2;
        int playerY = (int)Math.round(SCREEN_HEIGHT - SCREEN_HEIGHT * 0.2);

        player = new Object(paddleWidth, paddleHeight, playerX, playerY);

        int opponentX = SCREEN_WIDTH / 2;
        int opponentY = (int)Math.round(SCREEN_HEIGHT * 0.2);

        opponent = new Object(paddleWidth, paddleHeight, opponentX, opponentY);

        opponentBrain = new OpponentBrain(50);

        int goalPostWidthHeight = (int)Math.round(SCREEN_HEIGHT * 0.1);
        goalPostA = new Object(goalPostWidthHeight, goalPostWidthHeight, goalPostWidthHeight / 2, goalPostWidthHeight / 2);
        goalPostB = new Object(goalPostWidthHeight, goalPostWidthHeight, SCREEN_WIDTH - goalPostWidthHeight / 2, goalPostWidthHeight / 2);
        goalPostC = new Object(goalPostWidthHeight, goalPostWidthHeight, goalPostWidthHeight / 2, SCREEN_HEIGHT - goalPostWidthHeight / 2);
        goalPostD = new Object(goalPostWidthHeight, goalPostWidthHeight, SCREEN_WIDTH - goalPostWidthHeight / 2, SCREEN_HEIGHT - goalPostWidthHeight / 2);

        brain = 50;
        gameSetup();

        setFocusable(true);
    }

    private void gameSetup() {
        brain += playerScore - opponentScore;
        opponentBrain.setBrain(brain);
        playerScore = 0;
        opponentScore = 0;
        pointSetup();
    }

    private void pointSetup() {
        ball.reset(SCREEN_WIDTH, SCREEN_HEIGHT);
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

        opponent.update(opponentBrain.getOpponentPos(opponent, ball, SCREEN_HEIGHT), SCREEN_WIDTH, SCREEN_HEIGHT);

        Collision collisionWall = ball.testCollisionWall(SCREEN_WIDTH, SCREEN_HEIGHT);

        if (collisionWall != null) {
            ball.handleCollisionWall(SCREEN_WIDTH, SCREEN_HEIGHT, collisionWall);
        }

        Collision collisionPlayer = ball.testCollision(player);
        if (collisionPlayer != null) {
            ball.handleCollision(player, collisionPlayer);
        }

        Collision collisionOpponent = ball.testCollision(opponent);
        if (collisionOpponent != null) {
            ball.handleCollision(opponent, collisionOpponent);
        }

        Collision collisionGoalPostA = ball.testCollision(goalPostA);
        if (collisionGoalPostA != null) {
            ball.handleCollision(goalPostA, collisionGoalPostA);
        }

        Collision collisionGoalPostB = ball.testCollision(goalPostB);
        if (collisionGoalPostB != null) {
            ball.handleCollision(goalPostB, collisionGoalPostB);
        }

        Collision collisionGoalPostC = ball.testCollision(goalPostC);
        if (collisionGoalPostC != null) {
            ball.handleCollision(goalPostC, collisionGoalPostC);
        }

        Collision collisionGoalPostD = ball.testCollision(goalPostD);
        if (collisionGoalPostD != null) {
            ball.handleCollision(goalPostD, collisionGoalPostD);
        }

        if (collisionWall != null || collisionPlayer != null || collisionOpponent != null ||
            collisionGoalPostA != null || collisionGoalPostB != null || collisionGoalPostC != null || collisionGoalPostD != null) {
            opponentBrain.ballCollision();
        }

        if (collisionWall != null && collisionWall.equals(Collision.TOP)) {
            playerScore += 1;
            pointSetup();
            if (playerScore == 3) {
                gameSetup();
            }
        }
        if (collisionWall != null && collisionWall.equals(Collision.BOTTOM)) {
            opponentScore += 1;
            pointSetup();
            if (opponentScore == 3) {
                gameSetup();
            }
        }
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

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        canvas.drawText (Integer.toString(opponentBrain.getBrain()), SCREEN_WIDTH - 70, 70, paint);
        canvas.drawText (Integer.toString(playerScore), 40, 70, paint);
        canvas.drawText (Integer.toString(opponentScore), 40, SCREEN_HEIGHT - 70, paint);
    }
}
