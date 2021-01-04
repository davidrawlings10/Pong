package com.example.pong;

import android.graphics.Point;
import java.util.Random;

public class OpponentBrain {
    private int brain;
    private int responseSpeed;
    private int responseDelay;
    private int timeToResponse;
    private Random random;

    public OpponentBrain() {
        brain = 0;
        updateBrain(brain);
        random = new Random();
        random.setSeed(/*TODO: how to seed this*/ 0);
    }

    public Point getOpponentPos(Object opponent, Ball ball, PointStage pointStage) {
        if (timeToResponse > 0) {
            timeToResponse -= 1;
            return opponent.getPos();
        }

        Point opponentPos = opponent.getPos();
        Point ballPos = ball.getPos();

        if (ballPos.y > opponentPos.y) {
            if (ball.getDirY() == -1) {
                opponentPos.y -= (Math.min(responseSpeed, 5));
            } else {
                if (pointStage.equals(PointStage.PLAYING)) {
                    opponentPos.y += (Math.min(responseSpeed, 5));
                }
            }
        }
        if (ballPos.y < opponentPos.y) {
            opponentPos.y -= responseSpeed * 2;
        }

        if (ballPos.x > opponentPos.x) {
            opponentPos.x += responseSpeed * 8;
            if (opponentPos.x > ballPos.x) {
                opponentPos.x = ballPos.x;
            }
        }
        if (ballPos.x < opponentPos.x) {
            opponentPos.x -= responseSpeed * 8;
            if (opponentPos.x < ballPos.x) {
                opponentPos.x = ballPos.x;
            }
        }

        return opponentPos;
    }

    public void updateBrain(int brain) {
        this.brain = brain;
        responseSpeed = 3 + brain / 3;
        int responseDelayPotential = 30 - brain * 2;
        responseDelay = Math.max(responseDelayPotential, 0);
    }

    public int getBrain() {
        return brain;
    }

    public void ballCollision() {
        timeToResponse = responseDelay + random.nextInt(10);
    }
}
