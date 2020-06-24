package com.example.pong;

import android.graphics.Point;

public class OpponentBrain {
    private int brain;
    private int responseSpeed;
    private int responseDelay;
    private int timeToResponse;

    public OpponentBrain() {
        brain = 0;
        updateBrain(brain);
    }

    public Point getOpponentPos(Object opponent, Ball ball) {
        if (timeToResponse > 0) {
            timeToResponse -= 1;
            return opponent.getPos();
        }

        Point opponentPos = opponent.getPos();
        Point ballPos = ball.getPos();

        if (ballPos.y > opponentPos.y)
            if (ball.getDirY() == -1) {
                opponentPos.y -= responseSpeed;
            } else {
                opponentPos.y += responseSpeed;
            }
        if (ballPos.y < opponentPos.y)
            opponentPos.y -= responseSpeed * 2;
        if (ballPos.x > opponentPos.x) {
            opponentPos.x += responseSpeed * 4;
            if (opponentPos.x > ballPos.x)
                opponentPos.x = ballPos.x;
        }
        if (ballPos.x < opponentPos.x) {
            opponentPos.x -= responseSpeed * 4;
            if (opponentPos.x < ballPos.x)
                opponentPos.x = ballPos.x;
        }

        return opponentPos;
    }

    public void updateBrain(int brain) {
        this.brain = brain;
        responseSpeed = 3 + brain / 3;
        responseDelay = 20 - brain;
    }

    public int getBrain() {
        return brain;
    }

    public void ballCollision() {
        timeToResponse = responseDelay;
    }
}
