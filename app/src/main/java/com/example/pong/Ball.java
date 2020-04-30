package com.example.pong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Ball {
    final private int RADIUS;
    private Point pos;
    private Paint paint;
    private int speedX;
    private int speedY;

    public Ball(int radius, int x, int y) {
        RADIUS = radius;
        pos = new Point(x, y);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        speedX = 0;
        speedY = 0;
    }

    public void updatePos() {
        pos.x += getSpeedX();
        pos.y += getSpeedY();
    }

    public void handleCollisionWall(int SCREEN_WIDTH, int SCREEN_HEIGHT) {
        if (getRight() > SCREEN_WIDTH && getSpeedX() > 0 || getLeft() < 0 && getSpeedX() < 0)
            setSpeedX(getSpeedX() * -1);
        if (getBottom() > SCREEN_HEIGHT && getSpeedY() > 0 || getTop() < 0 && getSpeedY() < 0)
            setSpeedY(getSpeedY() * -1);
    }

    public void handleCollision(Object object) {
       int bottom_distance = object.getTop() - getBottom();
        int top_distance = getTop() - object.getBottom();
        int right_distance = object.getLeft() - getRight();
        int left_distance = getLeft() - object.getRight();

        if (bottom_distance < 0 && top_distance < 0 && right_distance < 0 && left_distance < 0) {
            Collision collision = Collision.TOP;
            int min = Math.abs(top_distance);

            if (Math.abs(bottom_distance) < min) {
                collision = Collision.BOTTOM;
                min = Math.abs(bottom_distance);
            }

            if (Math.abs(left_distance) < min) {
                collision = Collision.LEFT;
                min = Math.abs(left_distance);
            }

            if (Math.abs(right_distance) < min) {
                collision = Collision.RIGHT;
                // min = Math.abs(right_distance);
            }

            if (collision.equals(Collision.TOP)) {
                int newSpeedY = Math.min(-object.getySpeed() + 10, 40);
                System.out.println(newSpeedY);
                setSpeedY(newSpeedY);
                setSpeedX(getSpeedX() - object.getxSpeed() / 2);
                pos.y = object.getBottom() + getRADIUS();
            } else if (collision.equals(Collision.BOTTOM)) {
                int newSpeedY = Math.max(-object.getySpeed() - 10, -40);
                System.out.println(newSpeedY);
                setSpeedY(newSpeedY);
                setSpeedX(getSpeedX() - object.getxSpeed() / 2);
                pos.y = object.getTop() - getRADIUS();
            } else if (collision.equals(Collision.LEFT)) {
                int newSpeedX = Math.min(-object.getxSpeed() / 2 + 10, 20);
                System.out.println(newSpeedX);
                setSpeedX(newSpeedX);
                setSpeedY(getSpeedY() - object.getySpeed() / 2);
                pos.x = object.getRight() + getRADIUS();
            } else if (collision.equals(Collision.RIGHT)) {
                int newSpeedX = Math.max(-object.getxSpeed() / 2 - 10, -20);
                setSpeedX(newSpeedX);
                setSpeedY(getSpeedY() - object.getySpeed() / 2);
                pos.x = object.getLeft() - getRADIUS();
            }

            System.out.println("x:"+getSpeedX() + ", y:" + getSpeedY());
        }
    }

    public void update(int x, int y) {
        pos.set(x, y);
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(pos.x, pos.y, RADIUS, paint);
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public Point getPos() {
        return pos;
    }

    public int getTop() {
        return pos.y - RADIUS;
    }

    public int getBottom() {
        return pos.y + RADIUS;
    }

    public int getLeft() {
        return pos.x - RADIUS;
    }

    public int getRight() {
        return pos.x + RADIUS;
    }

    public int getRADIUS() { return RADIUS; }

    public int getSpeedX() {
        return speedX;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    public int getSpeedY() {
        return speedY;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }
}
