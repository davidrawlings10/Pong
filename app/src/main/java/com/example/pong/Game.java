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
    private int playerX;
    private int playerY;
    private Ball ball;
    private Object player;
    private Object opponent;
    private Object goalPostA;
    private Object goalPostB;
    private Object goalPostC;
    private Object goalPostD;
    private OpponentBrain opponentBrain;
    private int playerScore;
    private int opponentScore;
    private int SCREEN_BLOCK;
    private Stage stage;
    private int timeToNextStage;
    private int TIME_BETWEEN_STAGES;

    public Game(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);

        mainThread = new MainThread(getHolder(), this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        SCREEN_HEIGHT = displayMetrics.heightPixels;
        SCREEN_WIDTH = displayMetrics.widthPixels;

        ball = new Ball(22);

        SCREEN_BLOCK = (int)Math.round(SCREEN_HEIGHT * 0.05);

        player = new Object(SCREEN_BLOCK * 4, SCREEN_BLOCK, SCREEN_WIDTH / 2, SCREEN_HEIGHT - SCREEN_BLOCK * 4);
        opponent = new Object(SCREEN_BLOCK * 4, SCREEN_BLOCK, SCREEN_WIDTH / 2, SCREEN_BLOCK * 4);

        opponentBrain = new OpponentBrain();

        goalPostA = new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_BLOCK, SCREEN_BLOCK / 2);
        goalPostB = new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK, SCREEN_BLOCK / 2);
        goalPostC = new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_BLOCK, SCREEN_HEIGHT - SCREEN_BLOCK * 3);
        goalPostD = new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK, SCREEN_HEIGHT - SCREEN_BLOCK * 3);

        TIME_BETWEEN_STAGES = 100;

        stage = Stage.BEFORE;
        timeToNextStage = TIME_BETWEEN_STAGES;
        gameSetup();
    }

    private void gameSetup() {
        opponentBrain.updateBrain(opponentBrain.getBrain() + playerScore - opponentScore);
        playerScore = 0;
        opponentScore = 0;
        pointSetup();
    }

    private void pointSetup() {
        ball.reset(SCREEN_WIDTH, SCREEN_HEIGHT);
        timeToNextStage = TIME_BETWEEN_STAGES;
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
        while (true) {
            try {
                mainThread.setRunning(false);
                mainThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int touchX = (int)event.getX();
            int touchY = (int)event.getY() - 150;

            playerX = touchX;
            playerY = touchY;

            int minY = stage.equals(Stage.BEFORE) ? SCREEN_HEIGHT / 2 + SCREEN_BLOCK : SCREEN_BLOCK;
            player.update(new Point(playerX, playerY), SCREEN_BLOCK, 0, minY, SCREEN_WIDTH, SCREEN_HEIGHT - SCREEN_BLOCK * 4);
        }

        return true;
    }

    public void update() {
        if (stage.equals(Stage.BEFORE) || stage.equals(Stage.AFTER)) {
            timeToNextStage -= 1;
            if (timeToNextStage == 0) {
                if (stage.equals(Stage.BEFORE)) {
                    stage = Stage.PLAYING;
                } else if (stage.equals(Stage.AFTER)) {
                    pointSetup();
                    if (playerScore == 3 || opponentScore == 3) {
                        gameSetup();
                    }
                    stage = Stage.BEFORE;
                    timeToNextStage = TIME_BETWEEN_STAGES;
                }
            }
        } else {
            ball.updatePos();
        }

        int maxY = stage.equals(Stage.BEFORE) ? SCREEN_HEIGHT / 2 - SCREEN_BLOCK * 3 : SCREEN_HEIGHT;
        opponent.update(opponentBrain.getOpponentPos(opponent, ball/*, SCREEN_HEIGHT`1*/), SCREEN_BLOCK, 0, SCREEN_BLOCK, SCREEN_WIDTH, maxY);

        Collision collisionWall = ball.testCollisionWall(SCREEN_WIDTH, SCREEN_HEIGHT);

        if (collisionWall != null) {
            ball.handleCollisionWall(/*SCREEN_WIDTH, SCREEN_HEIGHT, `1*/ collisionWall);
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
            stage = Stage.AFTER;
            timeToNextStage = TIME_BETWEEN_STAGES;
        }
        if (collisionWall != null && collisionWall.equals(Collision.BOTTOM)) {
            opponentScore += 1;
            stage = Stage.AFTER;
            timeToNextStage = TIME_BETWEEN_STAGES;
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
        canvas.drawText(Integer.toString(opponentBrain.getBrain()), SCREEN_WIDTH - 110, SCREEN_BLOCK - 40, paint);
        canvas.drawText(Integer.toString(opponentScore), SCREEN_BLOCK - 30, SCREEN_BLOCK - 40, paint);
        canvas.drawText(Integer.toString(playerScore), SCREEN_BLOCK - 30, SCREEN_HEIGHT - SCREEN_BLOCK * 3 + 10, paint);
    }
}
