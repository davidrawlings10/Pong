package com.example.pong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private boolean displayGameStartText;
    private String opponentSkillText, opponentScoreText, playerScoreText;

    // INITIALIZE ----------------------------------------------------------------------------------
    public Game(Context context) {
        super(context);

        initializeCoreProperties();

        initializeObjects();

        opponentBrain = new OpponentBrain();

        initializeText();

        TIME_BETWEEN_STAGES = 150;

        displayGameStartText = true;

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
        ball = new Ball(22);
        player = new Object(SCREEN_BLOCK * 4, SCREEN_BLOCK, SCREEN_WIDTH / 2, getFieldBottomY() - SCREEN_BLOCK - SCREEN_BLOCK / 2);
        opponent = new Object(SCREEN_BLOCK * 4, SCREEN_BLOCK, SCREEN_WIDTH / 2, SCREEN_BLOCK * 4);
        goalPosts = new ArrayList<>();
        goalPosts.add(new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_BLOCK, SCREEN_BLOCK / 2)); // top left
        goalPosts.add(new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK, SCREEN_BLOCK / 2)); // top right
        goalPosts.add(new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_BLOCK, getFieldBottomY() - SCREEN_BLOCK / 2)); // bottom left
        goalPosts.add(new Object(SCREEN_BLOCK * 2, SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK, getFieldBottomY() - SCREEN_BLOCK / 2)); // bottom right
    }

    private void initializeText() {
        opponentSkillText = "Opponent Skill ";
        opponentScoreText = "Opponent Score ";
        playerScoreText = "Player Score ";
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
        if (pointStage.equals(PointStage.BEFORE) || pointStage.equals(PointStage.AFTER)) {
            timeToNextStage -= 1;
            if (timeToNextStage == 0) {
                if (pointStage.equals(PointStage.BEFORE)) {
                    displayGameStartText = false;
                    pointStage = PointStage.PLAYING;
                } else if (pointStage.equals(PointStage.AFTER)) {
                    pointSetup();
                    if (playerScore == 3 || opponentScore == 3) {
                        gameSetup();
                    }
                    pointStage = PointStage.BEFORE;
                    timeToNextStage = TIME_BETWEEN_STAGES;
                }
            }
        } else {
            ball.updatePos();
        }
    }

    private void updateOpponent() {
        int maxY = pointStage.equals(PointStage.BEFORE) ? SCREEN_HEIGHT / 2 - SCREEN_BLOCK * 3 : SCREEN_HEIGHT;
        opponent.update(opponentBrain.getOpponentPos(opponent, ball), SCREEN_BLOCK, 0, SCREEN_BLOCK, SCREEN_WIDTH, maxY);
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

        if (collisionDirectionWall != null && collisionDirectionWall.equals(CollisionDirection.TOP)) {
            playerScore += 1;
            handleGoal();
        }
        if (collisionDirectionWall != null && collisionDirectionWall.equals(CollisionDirection.BOTTOM)) {
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
    }

    private void gameEnd() {
        TIME_BETWEEN_STAGES += 50; // add a little extra time due to game end
        displayGameStartText = true;
    }

    // DRAW ----------------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawBackground(canvas);
        drawObjects(canvas);
        drawText(canvas);

        // drawScreenBlockGrid(canvas);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.rgb(0,175,0));
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        Paint greenPaint = new Paint();
        greenPaint.setColor(Color.rgb(0,175,0));

        int LINE_WIDTH = 4;

        // center circle
        canvas.drawCircle(getFieldCenterX(), getFieldCenterY(), 130, paint);
        canvas.drawCircle(getFieldCenterX(), getFieldCenterY(), 130 - LINE_WIDTH, greenPaint);

        // midline
        canvas.drawRect(0, getFieldCenterY() - LINE_WIDTH / 2, SCREEN_WIDTH, getFieldCenterY() + LINE_WIDTH / 2, paint);

        // top goal box circle
        canvas.drawOval(getFieldCenterX() - SCREEN_BLOCK - LINE_WIDTH, SCREEN_BLOCK * 4 - SCREEN_BLOCK / 2 - LINE_WIDTH, getFieldCenterX() + SCREEN_BLOCK + 4, SCREEN_BLOCK * 4 + SCREEN_BLOCK / 2 + LINE_WIDTH, paint);
        canvas.drawOval(getFieldCenterX() - SCREEN_BLOCK, SCREEN_BLOCK * 4 - SCREEN_BLOCK / 2, getFieldCenterX() + SCREEN_BLOCK, SCREEN_BLOCK * 4 + SCREEN_BLOCK / 2, greenPaint);

        // bottom goal box circles
        canvas.drawOval(getFieldCenterX() - SCREEN_BLOCK - LINE_WIDTH, getFieldBottomY() - SCREEN_BLOCK * 4 - SCREEN_BLOCK / 2 - LINE_WIDTH, getFieldCenterX() + SCREEN_BLOCK + LINE_WIDTH, getFieldBottomY() - SCREEN_BLOCK * 3 + SCREEN_BLOCK / 2 + LINE_WIDTH, paint);
        canvas.drawOval(getFieldCenterX() - SCREEN_BLOCK, getFieldBottomY() - SCREEN_BLOCK * 4 - SCREEN_BLOCK / 2, getFieldCenterX() + SCREEN_BLOCK, getFieldBottomY() - SCREEN_BLOCK * 3 + SCREEN_BLOCK / 2, greenPaint);

        // top goal box
        canvas.drawRect(SCREEN_BLOCK - LINE_WIDTH, SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK + LINE_WIDTH, SCREEN_BLOCK * 4 + LINE_WIDTH, paint);
        canvas.drawRect(SCREEN_BLOCK, SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK, SCREEN_BLOCK * 4, greenPaint);

        // bottom goal box
        canvas.drawRect(SCREEN_BLOCK - LINE_WIDTH, getFieldBottomY() - SCREEN_BLOCK * 4 - LINE_WIDTH, SCREEN_WIDTH - SCREEN_BLOCK + LINE_WIDTH, getFieldBottomY() - SCREEN_BLOCK, paint);
        canvas.drawRect(SCREEN_BLOCK, getFieldBottomY() - SCREEN_BLOCK * 4, SCREEN_WIDTH - SCREEN_BLOCK, getFieldBottomY() - SCREEN_BLOCK, greenPaint);

        // corner circles
        canvas.drawCircle(0, SCREEN_BLOCK, 30, paint);
        canvas.drawCircle(0, SCREEN_BLOCK, 30 - LINE_WIDTH, greenPaint);

        canvas.drawCircle(SCREEN_WIDTH, SCREEN_BLOCK, 30, paint);
        canvas.drawCircle(SCREEN_WIDTH, SCREEN_BLOCK, 30 - LINE_WIDTH, greenPaint);

        canvas.drawCircle(0, getFieldBottomY() - SCREEN_BLOCK, 30, paint);
        canvas.drawCircle(0, getFieldBottomY() - SCREEN_BLOCK, 30 - LINE_WIDTH, greenPaint);

        canvas.drawCircle(SCREEN_WIDTH, getFieldBottomY() - SCREEN_BLOCK, 30, paint);
        canvas.drawCircle(SCREEN_WIDTH, getFieldBottomY() - SCREEN_BLOCK, 30 - LINE_WIDTH, greenPaint);

        // goal lines
        canvas.drawRect(0, SCREEN_BLOCK, SCREEN_WIDTH, SCREEN_BLOCK + LINE_WIDTH, paint); // top goal line
        canvas.drawRect(0, getFieldBottomY() - SCREEN_BLOCK - LINE_WIDTH, SCREEN_WIDTH, getFieldBottomY() - SCREEN_BLOCK, paint); // bottom goal line

        // net lines
        canvas.drawRect(SCREEN_BLOCK * 2, 0, SCREEN_BLOCK * 2 + LINE_WIDTH, SCREEN_BLOCK, paint);
        canvas.drawRect(SCREEN_WIDTH - SCREEN_BLOCK * 2, 0, SCREEN_WIDTH - SCREEN_BLOCK * 2 - 4, SCREEN_BLOCK, paint);
        canvas.drawRect(SCREEN_BLOCK * 2, getFieldBottomY() - SCREEN_BLOCK, SCREEN_BLOCK * 2 + 4, getFieldBottomY(), paint);
        canvas.drawRect(SCREEN_WIDTH - SCREEN_BLOCK * 2, getFieldBottomY() - SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK * 2 - 4, getFieldBottomY(), paint);
        canvas.drawRect(SCREEN_BLOCK * 2, 0, SCREEN_WIDTH - SCREEN_BLOCK * 2, LINE_WIDTH, paint);
        canvas.drawRect(SCREEN_BLOCK * 2, getFieldBottomY(), SCREEN_WIDTH - SCREEN_BLOCK * 2, getFieldBottomY() - LINE_WIDTH, paint);

        // nets
        for (int i = 0; i < 100; ++i) {
            canvas.drawLine(SCREEN_BLOCK + 15 * i, 0, SCREEN_BLOCK + SCREEN_BLOCK + 15 * i, SCREEN_BLOCK, paint);
            canvas.drawLine(SCREEN_BLOCK + SCREEN_BLOCK + 15 * i, 0, SCREEN_BLOCK  + 15 * i, SCREEN_BLOCK, paint);
        }

        for (int i = 0; i < 100; ++i) {
            canvas.drawLine(SCREEN_BLOCK + 15 * i, getFieldBottomY(), SCREEN_BLOCK + SCREEN_BLOCK + 15 * i, getFieldBottomY() - SCREEN_BLOCK, paint);
            canvas.drawLine(SCREEN_BLOCK + SCREEN_BLOCK + 15 * i, getFieldBottomY(), SCREEN_BLOCK  + 15 * i, getFieldBottomY() - SCREEN_BLOCK, paint);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawObjects(Canvas canvas) {
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);

        Paint greenPaint = new Paint();
        greenPaint.setColor(Color.rgb(0,175,0));

        ball.draw(canvas);
        player.draw(canvas, blackPaint);
        opponent.draw(canvas, blackPaint);
        for (Object goalPost : goalPosts) {
            goalPost.draw(canvas, greenPaint);
        }
    }

    private void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);

        Paint blackPaint = new Paint();
        blackPaint.setTextSize(50);
        blackPaint.setColor(Color.WHITE);
        blackPaint.setTextAlign(Paint.Align.CENTER);

        if (displayGameStartText) {
            canvas.drawText(opponentSkillText.substring(0, displayTextEndIndex(opponentSkillText.length())) + opponentBrain.getBrain(), getFieldCenterX(), getFieldCenterY() - SCREEN_BLOCK * 3, blackPaint);
            canvas.drawText(opponentScoreText.substring(0, displayTextEndIndex(opponentScoreText.length())) + opponentScore, getFieldCenterX(), getFieldCenterY() - SCREEN_BLOCK * 2 + 15, blackPaint);
            canvas.drawText(playerScoreText.substring(0, displayTextEndIndex(playerScoreText.length())) + playerScore, getFieldCenterX(), getFieldCenterY() + SCREEN_BLOCK * 2 + 15, blackPaint);
        } else {
            canvas.drawText(Integer.toString(opponentBrain.getBrain()), SCREEN_WIDTH - 110, SCREEN_BLOCK - 40, paint);
            canvas.drawText(Integer.toString(opponentScore), SCREEN_BLOCK - 30, SCREEN_BLOCK - 40, paint);
            canvas.drawText(Integer.toString(playerScore), SCREEN_BLOCK - 30, getFieldBottomY() - SCREEN_BLOCK + SCREEN_BLOCK / 2 + 10, paint);
        }
    }

    private void drawScreenBlockGrid(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);

        for (int i = SCREEN_BLOCK; i < SCREEN_WIDTH; i += SCREEN_BLOCK) {
            canvas.drawLine(i, 0, i, SCREEN_HEIGHT, paint);
        }

        for (int i = SCREEN_BLOCK; i < SCREEN_HEIGHT; i += SCREEN_BLOCK) {
            canvas.drawLine(0, i, SCREEN_WIDTH, i, paint);
        }
    }

    private int displayTextEndIndex(int textLength) {
        if (pointStage.equals(PointStage.BEFORE)) {
            return Math.min(Math.max(timeToNextStage - 75, 0), textLength);
        } else if (pointStage.equals(PointStage.AFTER)) {
            return Math.min(Math.max(TIME_BETWEEN_STAGES - timeToNextStage, 0), textLength);
        } else {
            return textLength;
        }
    }
}
