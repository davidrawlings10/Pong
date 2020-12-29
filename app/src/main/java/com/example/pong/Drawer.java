package com.example.pong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Drawer {
    private final int SCREEN_WIDTH, SCREEN_HEIGHT, SCREEN_BLOCK, FIELD_CENTER_X, FIELD_CENTER_Y, FIELD_BOTTOM_Y, TIME_BETWEEN_STAGES;
    private boolean displayGameStartText;
    private Paint whitePaint, blackPaint, greenPaint, darkGreenPaint, redPaint;
    private List<List<Float>> netLines;

    public Drawer(int SCREEN_WIDTH, int SCREEN_HEIGHT, int SCREEN_BLOCK, int TIME_BETWEEN_STAGES, int FIELD_CENTER_X, int FIELD_CENTER_Y, int FIELD_BOTTOM_Y) {
        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;
        this.SCREEN_BLOCK = SCREEN_BLOCK;
        this.FIELD_CENTER_X = FIELD_CENTER_X;
        this.FIELD_CENTER_Y = FIELD_CENTER_Y;
        this.FIELD_BOTTOM_Y = FIELD_BOTTOM_Y;
        this.TIME_BETWEEN_STAGES = TIME_BETWEEN_STAGES;

        initializePaintColors();
        initializeNetLines();
    }

    private void initializePaintColors() {
        whitePaint = new Paint();
        blackPaint = new Paint();
        greenPaint = new Paint();
        darkGreenPaint = new Paint();
        redPaint = new Paint();

        whitePaint.setColor(Color.WHITE);
        blackPaint.setColor(Color.BLACK);
        greenPaint.setColor(Color.rgb(0,175,0));
        darkGreenPaint.setColor(Color.rgb(0,155,0));
        redPaint.setColor(Color.RED);
    }

    public void initializeNetLines() {
        netLines = new ArrayList<>();
        for (int i = SCREEN_BLOCK; i < SCREEN_WIDTH - SCREEN_BLOCK * 2; i += 25) {
            netLines.add(new ArrayList<>(Arrays.asList((float)i, (float)0, (float)SCREEN_BLOCK + i, (float)SCREEN_BLOCK)));
            netLines.add(new ArrayList<>(Arrays.asList((float)SCREEN_BLOCK + i, (float)0, (float)i, (float)SCREEN_BLOCK)));
            netLines.add(new ArrayList<>(Arrays.asList((float)i, (float)FIELD_BOTTOM_Y, (float)SCREEN_BLOCK + i, (float)FIELD_BOTTOM_Y - SCREEN_BLOCK)));
            netLines.add(new ArrayList<>(Arrays.asList((float)SCREEN_BLOCK + i, (float)FIELD_BOTTOM_Y, (float)i, (float)FIELD_BOTTOM_Y - SCREEN_BLOCK)));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.rgb(0,175,0));


        /*for (int i = 0; i < FIELD_BOTTOM_Y; i++) {
            canvas.drawRect(0, SCREEN_BLOCK * 3 * i - SCREEN_BLOCK * 2, SCREEN_WIDTH, SCREEN_BLOCK * 3 * i + SCREEN_BLOCK * 3 - SCREEN_BLOCK * 2, (i % 2 == 0 ? greenPaint : darkGreenPaint));
        }*/

        final int LINE_WIDTH = 4;

        // center circle
        canvas.drawCircle(FIELD_CENTER_X, FIELD_CENTER_Y, 130, whitePaint);
        canvas.drawCircle(FIELD_CENTER_X, FIELD_CENTER_Y, 130 - LINE_WIDTH, darkGreenPaint);

        // midline
        canvas.drawRect(0, FIELD_CENTER_Y - LINE_WIDTH / 2, SCREEN_WIDTH, FIELD_CENTER_Y + LINE_WIDTH / 2, whitePaint);

        // top goal box circle
        canvas.drawOval(FIELD_CENTER_X - SCREEN_BLOCK - LINE_WIDTH, SCREEN_BLOCK * 4 - SCREEN_BLOCK / 2 - LINE_WIDTH, FIELD_CENTER_X + SCREEN_BLOCK + LINE_WIDTH, SCREEN_BLOCK * 4 + SCREEN_BLOCK / 2 + LINE_WIDTH, whitePaint);
        canvas.drawOval(FIELD_CENTER_X - SCREEN_BLOCK, SCREEN_BLOCK * 4 - SCREEN_BLOCK / 2, FIELD_CENTER_X + SCREEN_BLOCK, SCREEN_BLOCK * 4 + SCREEN_BLOCK / 2, greenPaint);

        // bottom goal box circles
        canvas.drawOval(FIELD_CENTER_X - SCREEN_BLOCK - LINE_WIDTH, FIELD_BOTTOM_Y - SCREEN_BLOCK * 4 - SCREEN_BLOCK / 2 - LINE_WIDTH, FIELD_CENTER_X + SCREEN_BLOCK + LINE_WIDTH, FIELD_BOTTOM_Y - SCREEN_BLOCK * 3 + SCREEN_BLOCK / 2 + LINE_WIDTH, whitePaint);
        canvas.drawOval(FIELD_CENTER_X - SCREEN_BLOCK, FIELD_BOTTOM_Y - SCREEN_BLOCK * 4 - SCREEN_BLOCK / 2, FIELD_CENTER_X + SCREEN_BLOCK, FIELD_BOTTOM_Y - SCREEN_BLOCK * 3 + SCREEN_BLOCK / 2, greenPaint);

        // top goal box
        canvas.drawRect(SCREEN_BLOCK - LINE_WIDTH, SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK + LINE_WIDTH, SCREEN_BLOCK * 4 + LINE_WIDTH, whitePaint);
        canvas.drawRect(SCREEN_BLOCK, SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK, SCREEN_BLOCK * 4, darkGreenPaint);

        // bottom goal box
        canvas.drawRect(SCREEN_BLOCK - LINE_WIDTH, FIELD_BOTTOM_Y - SCREEN_BLOCK * 4 - LINE_WIDTH, SCREEN_WIDTH - SCREEN_BLOCK + LINE_WIDTH, FIELD_BOTTOM_Y - SCREEN_BLOCK, whitePaint);
        canvas.drawRect(SCREEN_BLOCK, FIELD_BOTTOM_Y - SCREEN_BLOCK * 4, SCREEN_WIDTH - SCREEN_BLOCK, FIELD_BOTTOM_Y - SCREEN_BLOCK, darkGreenPaint);

        // corner circles
        canvas.drawCircle(0, SCREEN_BLOCK, 30, whitePaint);
        canvas.drawCircle(0, SCREEN_BLOCK, 30 - LINE_WIDTH, greenPaint);

        canvas.drawCircle(SCREEN_WIDTH, SCREEN_BLOCK, 30, whitePaint);
        canvas.drawCircle(SCREEN_WIDTH, SCREEN_BLOCK, 30 - LINE_WIDTH, greenPaint);

        canvas.drawCircle(0, FIELD_BOTTOM_Y - SCREEN_BLOCK, 30, whitePaint);
        canvas.drawCircle(0, FIELD_BOTTOM_Y - SCREEN_BLOCK, 30 - LINE_WIDTH, greenPaint);

        canvas.drawCircle(SCREEN_WIDTH, FIELD_BOTTOM_Y - SCREEN_BLOCK, 30, whitePaint);
        canvas.drawCircle(SCREEN_WIDTH, FIELD_BOTTOM_Y - SCREEN_BLOCK, 30 - LINE_WIDTH, greenPaint);

        // goal lines
        canvas.drawRect(0, SCREEN_BLOCK, SCREEN_WIDTH, SCREEN_BLOCK + LINE_WIDTH, whitePaint); // top goal line
        canvas.drawRect(0, FIELD_BOTTOM_Y - SCREEN_BLOCK - LINE_WIDTH, SCREEN_WIDTH, FIELD_BOTTOM_Y - SCREEN_BLOCK, whitePaint); // bottom goal line

        // side lines
        canvas.drawRect(0, 0, LINE_WIDTH, FIELD_BOTTOM_Y - SCREEN_BLOCK, whitePaint);
        canvas.drawRect(SCREEN_WIDTH - LINE_WIDTH, 0, SCREEN_WIDTH, FIELD_BOTTOM_Y - SCREEN_BLOCK, whitePaint);

        // net border
        canvas.drawRect(SCREEN_BLOCK * 2, 0, SCREEN_BLOCK * 2 + LINE_WIDTH, SCREEN_BLOCK, whitePaint);
        canvas.drawRect(SCREEN_WIDTH - SCREEN_BLOCK * 2, 0, SCREEN_WIDTH - SCREEN_BLOCK * 2 - 4, SCREEN_BLOCK, whitePaint);
        canvas.drawRect(SCREEN_BLOCK * 2, FIELD_BOTTOM_Y - SCREEN_BLOCK, SCREEN_BLOCK * 2 + 4, FIELD_BOTTOM_Y, whitePaint);
        canvas.drawRect(SCREEN_WIDTH - SCREEN_BLOCK * 2, FIELD_BOTTOM_Y - SCREEN_BLOCK, SCREEN_WIDTH - SCREEN_BLOCK * 2 - 4, FIELD_BOTTOM_Y, whitePaint);
        canvas.drawRect(SCREEN_BLOCK * 2, 0, SCREEN_WIDTH - SCREEN_BLOCK * 2, LINE_WIDTH, whitePaint);
        canvas.drawRect(SCREEN_BLOCK * 2, FIELD_BOTTOM_Y, SCREEN_WIDTH - SCREEN_BLOCK * 2, FIELD_BOTTOM_Y - LINE_WIDTH, whitePaint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void drawObjects(Canvas canvas, Ball ball, Object player, Object opponent) {
        /*Paint blackPaint = new Paint(); `1
        blackPaint.setColor(Color.BLACK);*/

        ball.draw(canvas);
        player.draw(canvas, blackPaint);
        opponent.draw(canvas, blackPaint);
    }

    public void drawText(Canvas canvas, OpponentBrain opponentBrain, int playerScore, int opponentScore, PointStage pointStage, int timeToNextStage) {
        Paint scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(40);

        Paint gameStartPaint = new Paint();
        gameStartPaint.setTextSize(50);
        gameStartPaint.setColor(Color.WHITE);
        gameStartPaint.setTextAlign(Paint.Align.CENTER);

        if (displayGameStartText) {
            String opponentSkillText = "Opponent Skill ";
            String opponentScoreText = "Opponent Score ";
            String playerScoreText = "Player Score ";
            canvas.drawText(opponentSkillText.substring(0, displayTextEndIndex(opponentSkillText.length(), pointStage, timeToNextStage)) + opponentBrain.getBrain(), FIELD_CENTER_X, FIELD_CENTER_Y - SCREEN_BLOCK * 3, gameStartPaint);
            canvas.drawText(opponentScoreText.substring(0, displayTextEndIndex(opponentScoreText.length(), pointStage, timeToNextStage)) + opponentScore, FIELD_CENTER_X, FIELD_CENTER_Y - SCREEN_BLOCK * 2 + 15, gameStartPaint);
            canvas.drawText(playerScoreText.substring(0, displayTextEndIndex(playerScoreText.length(), pointStage, timeToNextStage)) + playerScore, FIELD_CENTER_X, FIELD_CENTER_Y + SCREEN_BLOCK * 2 + 15, gameStartPaint);
        } else {
            canvas.drawText(Integer.toString(opponentBrain.getBrain()), SCREEN_WIDTH - 110, SCREEN_BLOCK - 40, scorePaint);
            canvas.drawText(Integer.toString(opponentScore), SCREEN_BLOCK - 30, SCREEN_BLOCK - 40, scorePaint);
            canvas.drawText(Integer.toString(playerScore), SCREEN_BLOCK - 30, FIELD_BOTTOM_Y - SCREEN_BLOCK + SCREEN_BLOCK / 2 + 10, scorePaint);
        }
    }

    public void drawForeground(Canvas canvas, List<Object> goalPosts) {
        /* Paint paint = new Paint(); `1
        paint.setColor(Color.WHITE);

        Paint greenPaint = new Paint();
        greenPaint.setColor(Color.rgb(0,175,0));*/

        // nets
        /*for (int i = SCREEN_BLOCK; i < SCREEN_WIDTH - SCREEN_BLOCK * 2; i += 25) { `1
            canvas.drawLine(i, 0, SCREEN_BLOCK + i, SCREEN_BLOCK, whitePaint);
            canvas.drawLine(SCREEN_BLOCK + i, 0, i, SCREEN_BLOCK, whitePaint);
            canvas.drawLine(i, FIELD_BOTTOM_Y, SCREEN_BLOCK + i, FIELD_BOTTOM_Y - SCREEN_BLOCK, whitePaint);
            canvas.drawLine(SCREEN_BLOCK + i, FIELD_BOTTOM_Y, i, FIELD_BOTTOM_Y - SCREEN_BLOCK, whitePaint);
        }*/

        for (List netLine : netLines) {
            canvas.drawLine((float) netLine.get(0), (float) netLine.get(1), (float) netLine.get(2), (float) netLine.get(3), whitePaint);
        }

        for (Object goalPost : goalPosts) {
            goalPost.draw(canvas, greenPaint);
        }
    }

    public  void drawScreenBlockGrid(Canvas canvas) {
        /* Paint paint = new Paint(); `1
        paint.setColor(Color.RED);*/

        for (int i = SCREEN_BLOCK; i < SCREEN_WIDTH; i += SCREEN_BLOCK) {
            canvas.drawLine(i, 0, i, SCREEN_HEIGHT, redPaint);
        }

        for (int i = SCREEN_BLOCK; i < SCREEN_HEIGHT; i += SCREEN_BLOCK) {
            canvas.drawLine(0, i, SCREEN_WIDTH, i, redPaint);
        }
    }

    private int displayTextEndIndex(int textLength, PointStage pointStage, int timeToNextStage) {
        if (pointStage.equals(PointStage.BEFORE)) {
            return Math.min(Math.max(timeToNextStage - 75, 0), textLength);
        } else if (pointStage.equals(PointStage.AFTER)) {
            return Math.min(Math.max(TIME_BETWEEN_STAGES - timeToNextStage, 0), textLength);
        } else {
            return textLength;
        }
    }

    public void setDisplayGameStartText(boolean displayGameStartText) {
        this.displayGameStartText = displayGameStartText;
    }
}
