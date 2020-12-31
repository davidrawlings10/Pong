package com.example.pong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class Game extends SurfaceView implements SurfaceHolder.Callback {

    private MainThread mainThread;

    private int SCREEN_HEIGHT;
    private int SCREEN_WIDTH;
    private int SCREEN_BLOCK;

    private Ball ball;
    private Object player;
    private Object opponent;
    private List<Object> goalPosts;

    private OpponentBrain opponentBrain;

    private int playerScore;
    private int opponentScore;

    private PointStage pointStage;

    private int timeToNextStage;
    private int TIME_BETWEEN_STAGES;

    Drawer drawer;

    // INITIALIZE ----------------------------------------------------------------------------------
    public Game(Context context) {
        super(context);

        initializeCoreProperties();

        initializeObjects();

        opponentBrain = new OpponentBrain();

        TIME_BETWEEN_STAGES = 150;

        drawer = new Drawer(SCREEN_WIDTH, SCREEN_HEIGHT, SCREEN_BLOCK, TIME_BETWEEN_STAGES, getFieldCenterX(), getFieldCenterY(), getFieldBottomY());

        pointStage = PointStage.BEFORE;
        timeToNextStage = TIME_BETWEEN_STAGES;
        gameSetup();
    }

    private void initializeCoreProperties() {
        getHolder().addCallback(this);
        setFocusable(true);

        mainThread = new MainThread(getHolder(), this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        SCREEN_HEIGHT = displayMetrics.heightPixels;
        SCREEN_WIDTH = displayMetrics.widthPixels;
        SCREEN_BLOCK = (int)Math.round(SCREEN_HEIGHT * 0.05);
    }

    private void initializeObjects() {
        ball = new Ball(SCREEN_BLOCK / 4);
        player = new Object(SCREEN_BLOCK * 4, SCREEN_BLOCK, SCREEN_WIDTH / 2, getFieldBottomY() - SCREEN_BLOCK - SCREEN_BLOCK / 2);
        opponent = new Object(SCREEN_BLOCK * 4, SCREEN_BLOCK, SCREEN_WIDTH / 2, SCREEN_BLOCK * 4);
        goalPosts = new ArrayList<>();
        goalPosts.add(new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_BLOCK, SCREEN_BLOCK / 2)); // top left
        goalPosts.add(new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK, SCREEN_BLOCK / 2)); // top right
        goalPosts.add(new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_BLOCK, getFieldBottomY() - SCREEN_BLOCK / 2)); // bottom left
        goalPosts.add(new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK, getFieldBottomY() - SCREEN_BLOCK / 2)); // bottom right
    }

    // DIMENSION HELPERS ---------------------------------------------------------------------------
    private int getFieldCenterX() {
        return SCREEN_WIDTH / 2;
    }

    private int getFieldBottomY() {
        return (SCREEN_HEIGHT - SCREEN_BLOCK * 3) /*- SCREEN_BLOCK / 2*/;
    }

    private int getFieldCenterY() {
        return getFieldBottomY() / 2;
    }

    // SETUP ---------------------------------------------------------------------------------------
    private void gameSetup() {
        opponentBrain.updateBrain(opponentBrain.getBrain() + playerScore - opponentScore);
        playerScore = 0;
        opponentScore = 0;
        pointSetup();
    }

    private void pointSetup() {
        ball.reset(getFieldCenterX(), getFieldCenterY());
        timeToNextStage = TIME_BETWEEN_STAGES;
    }

    // EVENTS --------------------------------------------------------------------------------------
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
            int touchY = (int)event.getY() - SCREEN_BLOCK * 2;

            int minY = pointStage.equals(PointStage.BEFORE) ? getFieldCenterY() + SCREEN_BLOCK : SCREEN_BLOCK;
            player.update(new Point(touchX, touchY), SCREEN_BLOCK, 0, minY, SCREEN_WIDTH, getFieldBottomY() - SCREEN_BLOCK - SCREEN_BLOCK / 2);
        }

        return true;
    }

    // UPDATE --------------------------------------------------------------------------------------
    public void update() {
        handleGameFlow();
        updateOpponent();
        handleBallCollision();
    }

    private void handleGameFlow() {
        if (pointStage.equals(PointStage.BEFORE)) {
            timeToNextStage -= 1;
            if (timeToNextStage == 0) {
                drawer.setDisplayGameStartText(false);
                pointStage = PointStage.PLAYING;
            }
        } else if (pointStage.equals(PointStage.AFTER)) {
            timeToNextStage -= 1;
            if (timeToNextStage == 0) {
                pointSetup();
                if (playerScore == 3 || opponentScore == 3) {
                    gameSetup();
                }
                pointStage = PointStage.BEFORE;
                timeToNextStage = TIME_BETWEEN_STAGES;
            }
            if (ball.getSpeedY() > 0) {
                ball.setSpeedY(ball.getSpeedY() - 1);
            }
            if (ball.getSpeedX() > 0) {
                ball.setSpeedX(ball.getSpeedX() - 1);
            }
            ball.updatePos();
        } else if (pointStage.equals(PointStage.PLAYING)) {
            ball.updatePos();
        }
    }

    private void updateOpponent() {
        int maxY = pointStage.equals(PointStage.BEFORE) ? SCREEN_HEIGHT / 2 - SCREEN_BLOCK * 3 : SCREEN_HEIGHT;
        opponent.update(opponentBrain.getOpponentPos(opponent, ball, pointStage), SCREEN_BLOCK, 0, SCREEN_BLOCK, SCREEN_WIDTH, maxY);
    }

    private void handleBallCollision() {
        CollisionDirection collisionDirectionWall = ball.testCollisionWall(SCREEN_WIDTH, SCREEN_HEIGHT);
        CollisionDirection collisionDirectionPlayer = ball.testCollision(player);
        CollisionDirection collisionDirectionOpponent = ball.testCollision(opponent);
        if (collisionDirectionWall != null || collisionDirectionPlayer != null || collisionDirectionOpponent != null) {
            opponentBrain.ballCollision();
        }

        for (Object goalPost : goalPosts) {
            CollisionDirection collisionDirectionGoalPost = ball.testCollision(goalPost);
            if (collisionDirectionGoalPost != null) {
                opponentBrain.ballCollision();
            }
        }

        if (ball.getTop() <= 0 && pointStage == PointStage.PLAYING) {
            playerScore += 1;
            handleGoal();
        }
        if (ball.getBottom() >= getFieldBottomY() && pointStage == PointStage.PLAYING) {
            opponentScore += 1;
            handleGoal();
        }
    }

    private void handleGoal() {
        pointStage = PointStage.AFTER;
        if (playerScore == 3 || opponentScore == 3) {
            gameEnd();
        }
        timeToNextStage = TIME_BETWEEN_STAGES + 50;
        ball.setDirY(ball.getDirY() * -1);
        ball.setSpeedY(ball.getSpeedY() * 2/3);
        ball.setSpeedX(ball.getSpeedX() * 2/3);
    }

    private void gameEnd() {
        TIME_BETWEEN_STAGES += 50; // add a little extra time due to game end
        drawer.setDisplayGameStartText(false);
    }

    // DRAW ----------------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawer.drawBackground(canvas);
        drawer.drawObjects(canvas, ball, player, opponent);
        drawer.drawForeground(canvas, goalPosts);
        drawer.drawText(canvas, opponentBrain, playerScore, opponentScore, pointStage, timeToNextStage);

        // drawer.drawScreenBlockGrid(canvas);
    }
}
