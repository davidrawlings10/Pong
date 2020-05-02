package com.example.pong;

import android.graphics.Point;

public class OpponentBrain {
    public static Point getOpponentPos(Object opponent, Ball ball, int SCREEN_HEIGHT) {
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
}
