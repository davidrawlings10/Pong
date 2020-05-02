package com.example.pong;

import android.graphics.Point;

public class OpponentBrain {
    private int brain;
    private int responseSpeed;
    private int responseDelay;
    private int timeToResponse;

    public OpponentBrain(int startingBrain) {
        setBrain(startingBrain);
    }

    public Point getOpponentPos(Object opponent, Ball ball, int SCREEN_HEIGHT) {
        if (timeToResponse > 0) {
            timeToResponse -= 1;
            return opponent.getPos();
        }

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

    public void setBrain(int brain) {
        this.brain = brain;
        responseSpeed = 10;
        responseDelay = 30;
    }

    public int getBrain() {
        return brain;
    }

    public void ballCollision() {
        timeToResponse = responseDelay;
    }
}
