package com.example.pong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Object {
    final private int WIDTH;
    final private int HEIGHT;
    private Point pos;
    private Point lastPos;
    private Rect rect;
    private Paint paint;
    private int xSpeed;
    private int ySpeed;

    public Object(int width, int height, int x, int y) {
        WIDTH = width;
        HEIGHT = height;
        pos = new Point(x, y);
        lastPos = new Point(x, y);
        rect = new Rect();
        paint = new Paint();
        paint.setColor(Color.BLACK);
    }

    public void update(int x, int y) {
        lastPos.set(pos.x, pos.y);
        ySpeed = pos.y - y;
        xSpeed = pos.x - x;
        pos.set(x, y);
    }

    public void draw(Canvas canvas) {
        rect.set(pos.x - WIDTH/2, pos.y - HEIGHT/2, pos.x + WIDTH/2, pos.y + HEIGHT/2);
        canvas.drawRect(rect, paint);
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public Point getPos() {
        return pos;
    }

    public Point getLastPos() { return lastPos; }

    public int getWIDTH() { return WIDTH; }

    public int getHEIGHT() { return HEIGHT; }

    public int getTop() {
        return pos.y - HEIGHT / 2;
    }

    public int getBottom() {
        return pos.y + HEIGHT / 2;
    }

    public int getLeft() {
        return pos.x - WIDTH / 2;
    }

    public int getRight() {
        return pos.x + WIDTH / 2;
    }

    public int getxSpeed() {
        return xSpeed;
    }

    public void setxSpeed(int xSpeed) {
        this.xSpeed = xSpeed;
    }

    public int getySpeed() {
        return ySpeed;
    }

    public void setySpeed(int ySpeed) {
        this.ySpeed = ySpeed;
    }
}
