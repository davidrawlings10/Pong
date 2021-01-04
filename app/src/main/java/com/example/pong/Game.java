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

    private final int SCREEN_WIDTH, SCREEN_HEIGHT, SCREEN_BLOCK, FIELD_CENTER_X, FIELD_CENTER_Y, FIELD_BOTTOM_Y, TIME_BETWEEN_STAGES;

    private Ball ball;
    private Object player;
    private Object opponent;
    private List<Object> goalContainers; // objects for the ball to bounce off

    private OpponentBrain opponentBrain;

    private int playerScore;
    private int opponentScore;

    private PointStage pointStage;

    private int timeToNextStage;

    Drawer drawer;

    // INITIALIZE ----------------------------------------------------------------------------------
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
        SCREEN_BLOCK = (int)Math.round(SCREEN_HEIGHT * 0.05);
        FIELD_CENTER_X = SCREEN_WIDTH / 2;
        FIELD_CENTER_Y =  (SCREEN_HEIGHT - SCREEN_BLOCK * 3) / 2;
        FIELD_BOTTOM_Y = SCREEN_HEIGHT - SCREEN_BLOCK * 3;

        TIME_BETWEEN_STAGES = 50;

        initializeObjects();

        opponentBrain = new OpponentBrain();

        drawer = new Drawer(SCREEN_WIDTH, SCREEN_HEIGHT, SCREEN_BLOCK, FIELD_CENTER_X, FIELD_CENTER_Y, FIELD_BOTTOM_Y, TIME_BETWEEN_STAGES);

        pointStage = PointStage.BEFORE;
        // timeToNextStage = TIME_BETWEEN_STAGES;
        gameSetup();
    }

    /*private void initializeCoreProperties() {
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
    }*/

    private void initializeObjects() {
        ball = new Ball(SCREEN_BLOCK / 4);
        player = new Object(SCREEN_BLOCK * 4, SCREEN_BLOCK, SCREEN_WIDTH / 2, FIELD_BOTTOM_Y - SCREEN_BLOCK - SCREEN_BLOCK / 2);
        opponent = new Object(SCREEN_BLOCK * 4, SCREEN_BLOCK, SCREEN_WIDTH / 2, SCREEN_BLOCK * 4);
        goalContainers = new ArrayList<>();
        goalContainers.add(new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_BLOCK, SCREEN_BLOCK / 2)); // top left
        goalContainers.add(new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK, SCREEN_BLOCK / 2)); // top right
        goalContainers.add(new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_BLOCK, FIELD_BOTTOM_Y - SCREEN_BLOCK / 2 + 1)); // bottom left
        goalContainers.add(new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK, FIELD_BOTTOM_Y - SCREEN_BLOCK / 2 + 1)); // bottom right
        goalContainers.add(new Object(SCREEN_WIDTH, SCREEN_BLOCK, FIELD_CENTER_X, FIELD_BOTTOM_Y + SCREEN_BLOCK)); // bottom back
    }

    // DIMENSION HELPERS ---------------------------------------------------------------------------
    /*private int getFieldCenterX() {
        return SCREEN_WIDTH / 2;
    }

    private int getFieldBottomY() { return (SCREEN_HEIGHT - SCREEN_BLOCK * 3); }

    private int getFieldCenterY() {
        return getFieldBottomY() / 2;
    }*/

    // SETUP ---------------------------------------------------------------------------------------
    private void gameSetup() {
        opponentBrain.updateBrain(opponentBrain.getBrain() + playerScore - opponentScore);
        playerScore = 0;
        opponentScore = 0;
        pointSetup();
    }

    private void pointSetup() {
        ball.reset(FIELD_CENTER_X, FIELD_CENTER_Y, opponentBrain.getBrain());
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

            int minY = pointStage.equals(PointStage.BEFORE) ? FIELD_CENTER_Y + SCREEN_BLOCK : SCREEN_BLOCK;
            player.update(new Point(touchX, touchY), SCREEN_BLOCK, 0, minY, SCREEN_WIDTH, FIELD_BOTTOM_Y - SCREEN_BLOCK - SCREEN_BLOCK / 2);
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
        final int END_SCORE = 3;

        if (pointStage.equals(PointStage.PLAYING)) {
            ball.updatePos();
        } else if (pointStage.equals(PointStage.BEFORE)) {
            timeToNextStage -= 1;
            if (timeToNextStage == 0) {
                drawer.setDisplayGameStartText(false);
                pointStage = PointStage.PLAYING;
            }
        } else if (pointStage.equals(PointStage.AFTER)) {
            timeToNextStage -= 1;
            if (timeToNextStage == 0) {
                pointSetup();
                if (playerScore == END_SCORE || opponentScore == END_SCORE) {
                    gameSetup();
                }
                pointStage = PointStage.BEFORE;
                timeToNextStage = TIME_BETWEEN_STAGES;

                player.setPos(getPlayerPosForBeforePoint());
            }

            if (ball.getSpeedY() > 0) {
                ball.setSpeedY(ball.getSpeedY() - 1);
            }
            if (ball.getSpeedX() > 0) {
                ball.setSpeedX(ball.getSpeedX() - 1);
            }

            ball.updatePos();
        }
    }

    private Point getPlayerPosForBeforePoint() {
        return new Point(player.getPos().x, Math.max(player.getPos().y, FIELD_CENTER_Y + SCREEN_BLOCK));
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

        for (Object goalPost : goalContainers) {
            CollisionDirection collisionDirectionGoalPost = ball.testCollision(goalPost);
            if (collisionDirectionGoalPost != null) {
                opponentBrain.ballCollision();
            }
        }

        if (ball.getTop() <= 0
                && ball.getLeft() >= goalContainers.get(0).getRight() && ball.getRight() <= goalContainers.get(1).getLeft() // ensure that the ball is actually in the goal
                && pointStage == PointStage.PLAYING) {
            playerScore += 1;
            handleGoal();
        }
        if (ball.getBottom() >= FIELD_BOTTOM_Y
                && ball.getLeft() >= goalContainers.get(0).getRight() && ball.getRight() <= goalContainers.get(1).getLeft() // ensure that the ball is actually in the goal
                && pointStage == PointStage.PLAYING) {
            opponentScore += 1;
            handleGoal();
        }
    }

    private void handleGoal() {
        /*if (playerScore == 3 || opponentScore == 3) {
            gameEnd();
        } */

        pointStage = PointStage.AFTER;
        timeToNextStage = TIME_BETWEEN_STAGES;

        // make ball bounce off net
        ball.setDirY(ball.getDirY() * -1);
        ball.setSpeedY(ball.getSpeedY() / 2);
        ball.setSpeedX(ball.getSpeedX() / 2);
    }

    private void gameEnd() {
        // TIME_BETWEEN_STAGES += 50; // add a little extra time due to game end
        // drawer.setDisplayGameStartText(false);
    }

    // DRAW ----------------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        /*Paint paint = new Paint();

        paint.setColor(android.graphics.Color.BLACK);
        canvas.drawPaint(paint);

        paint.setStrokeWidth(4);
        paint.setColor(android.graphics.Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        Point a = new Point(95, 0);
        Point b = new Point(0, 69);
        Point c = new Point(36, 181);
        Point d = new Point(154, 181);
        Point e = new Point(190, 69);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(d.x, d.y);
        path.lineTo(e.x, e.y);
        path.lineTo(a.x, a.y);
        path.close();

        canvas.drawPath(path, paint);*/

        drawer.drawField(canvas);
        drawer.drawObjects(canvas, ball, player, opponent);
        drawer.drawGoal(canvas, goalContainers);
        drawer.drawText(canvas, opponentBrain, playerScore, opponentScore, pointStage, timeToNextStage);

        // drawer.drawScreenBlockGrid(canvas); // drawing helper
    }
}
