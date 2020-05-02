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
    private int dirY;
    private int dirX;

    public Ball(int radius, int x, int y) {
        RADIUS = radius;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        pos = new Point(x, y);
        speedX = 10;
        speedY = 10;
        dirX = 1;
        dirY = 1;
    }

    public void updatePos() {
        pos.x += getSpeedX() * dirX;
        pos.y += getSpeedY() * dirY;
    }

    public void handleCollisionWall(int SCREEN_WIDTH, int SCREEN_HEIGHT) {
        Collision collision = testCollisionWall(SCREEN_WIDTH, SCREEN_HEIGHT);

        if (collision == null)
            return;

        if (collision.equals(Collision.LEFT) || collision.equals(Collision.RIGHT)) {
            dirX *= -1;
            // setSpeedX((int)Math.round(getSpeedX() * -0.8));
            // setSpeedX((int)Math.round(getSpeedX() * -1));
        }
        if (collision.equals(Collision.TOP) || collision.equals(Collision.BOTTOM)) {
            dirY *= -1;
            reset(SCREEN_WIDTH, SCREEN_HEIGHT);
            // setSpeedY(getSpeedY() * -2);
            // setSpeedY(getSpeedY() * -1);
        }


        /*if (getRight() > SCREEN_WIDTH && getSpeedX() > 0 || getLeft() < 0 && getSpeedX() < 0)
            setSpeedX((int)Math.round(getSpeedX() * -0.8));
        if (getBottom() > SCREEN_HEIGHT && getSpeedY() > 0 || getTop() < 0 && getSpeedY() < 0)
            setSpeedY(getSpeedY() * -2);*/

        System.out.println("getSpeedX:" + getSpeedX() + ", getSpeedY:" + getSpeedY() + ", dirX:" + dirX + ", dirY:" + dirY);
    }

    private void reset(int SCREEN_WIDTH, int SCREEN_HEIGHT) {
        pos.x = SCREEN_WIDTH / 2;
        pos.y = SCREEN_HEIGHT / 2;
        speedX = 10;
        speedY = 10;
        dirX = 1;
        dirY = 1;
    }

    public Collision testCollisionWall(int SCREEN_WIDTH, int SCREEN_HEIGHT) {
        if (getTop() < 0 && dirY < 0)
            return Collision.TOP;
        if (getBottom() > SCREEN_HEIGHT && dirY > 0)
            return Collision.BOTTOM;
        if (getLeft() < 0 && dirX < 0)
            return Collision.LEFT;
        if (getRight() > SCREEN_WIDTH && dirX > 0)
            return Collision.RIGHT;

        return null;
    }

    public void handleCollision(Object object) {
        /*int distance_from_top = getTop() - object.getBottom();
        int distance_from_bottom = object.getTop() - getBottom();
        int distance_from_left = getLeft() - object.getRight();
        int distance_from_right = object.getLeft() - getRight();

        if (distance_from_bottom >= 0 || distance_from_top >= 0 || distance_from_right >= 0 || distance_from_left > 0)
            return;*/

        Collision collision = testCollision(object);

        if (collision == null)
            return;

        if (collision.equals(Collision.TOP)) {
            dirY *= -1;
            // int newSpeedY = Math.min(-object.getySpeed() / 4 + 10, 40);
            // setSpeedY(-getSpeedY() + 1);
            // setSpeedX(getSpeedX() + 1);
            // setSpeedX(getSpeedX() - object.getxSpeed() / 4);
            speedX += 1;
            speedY += 1;
            pos.y = object.getBottom() + getRADIUS() + 25;
        } else if (collision.equals(Collision.BOTTOM)) {
            dirY *= -1;
            // int newSpeedY = Math.max(-object.getySpeed() / 4 - 10, -40);
            // setSpeedY(-getSpeedY() - 1);
            // setSpeedX(getSpeedX() + 1);
            speedX += 1;
            speedY += 1;
            // setSpeedX(getSpeedX() - object.getxSpeed() / 4);
            pos.y = object.getTop() - getRADIUS() - 25;
        } else if (collision.equals(Collision.LEFT)) {
            dirX *= -1;
            // int newSpeedX = Math.min(-object.getxSpeed() / 4 + 10, 20);
            // setSpeedX(newSpeedX);
            // setSpeedY(getSpeedY() - object.getySpeed() / 4);
            pos.x = object.getRight() + getRADIUS() + 5;
        } else if (collision.equals(Collision.RIGHT)) {
            dirX *= -1;
            // int newSpeedX = Math.max(-object.getxSpeed() / 4 - 10, -20);
            // setSpeedX(newSpeedX);
            // setSpeedY(getSpeedY() - object.getySpeed() / 4);
            pos.x = object.getLeft() - getRADIUS() - 5;
        }

        System.out.println("x:"+getSpeedX() + ", y:" + getSpeedY());
    }

    public Collision testCollision(Object object) {
        int distance_from_top = getTop() - object.getBottom();
        int distance_from_bottom = object.getTop() - getBottom();
        int distance_from_left = getLeft() - object.getRight();
        int distance_from_right = object.getLeft() - getRight();

        if (distance_from_bottom >= 0 || distance_from_top >= 0 || distance_from_right >= 0 || distance_from_left > 0)
            return null;

        Collision collision = Collision.TOP;
        int min = Math.abs(distance_from_top);

        if (Math.abs(distance_from_bottom) < min) {
            collision = Collision.BOTTOM;
            min = Math.abs(distance_from_bottom);
        }

        if (Math.abs(distance_from_left) < min) {
            collision = Collision.LEFT;
            min = Math.abs(distance_from_left);
        }

        if (Math.abs(distance_from_right) < min) {
            collision = Collision.RIGHT;
            min = Math.abs(distance_from_right);
        }

        return collision;
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
