package com.example.pong;

import android.graphics.Canvas;
// import android.graphics.Color; `1
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Object {
    final private int WIDTH;
    final private int HEIGHT;
    private Point pos;
    private Rect rect;
    /*private Paint paint; `1
    private Paint whitePaint;
    private int xSpeed;
    private int ySpeed;*/

    public Object(int width, int height, int x, int y) {
        WIDTH = width;
        HEIGHT = height;
        pos = new Point(x, y);
        rect = new Rect();
    }

    public void update(Point newPos, int SCREEN_BLOCK, int minX, int minY, int maxX, int maxY) {
        adjustPosForScreen(newPos, SCREEN_BLOCK, minX, minY, maxX, maxY);

        int x = newPos.x, y = newPos.y;

        if (newPos.x > pos.x) {
            x = Math.min(newPos.x, pos.x + 60);
        } else if (newPos.x < pos.x) {
            x = Math.max(newPos.x, pos.x - 60);
        }

        if (newPos.y > pos.y) {
            y = Math.min(newPos.y, pos.y + 80);
        } else if (newPos.y < pos.y) {
            y = Math.max(newPos.y, pos.y - 25);
        }

        // xSpeed = pos.x - newPos.x;
        // ySpeed = pos.y - newPos.y;

        pos.set(x, y);
    }

    private Point adjustPosForScreen(Point newPos, int SCREEN_BLOCK, int minX, int minY, int maxX, int maxY) {
        if (newPos.x - getWIDTH() / 2 < minX)
            newPos.x = minX + getWIDTH() / 2;
        if (newPos.y - getHEIGHT() / 2 < minY)
            newPos.y = minY + SCREEN_BLOCK / 2;
        if (newPos.x + getWIDTH() / 2 > maxX)
            newPos.x = maxX - getWIDTH() / 2;
        if (newPos.y > maxY)
            newPos.y = maxY;

        return pos;
    }

    public void draw(Canvas canvas, Paint paint) {
        rect.set(pos.x - WIDTH/2, pos.y - HEIGHT/2, pos.x + WIDTH/2, pos.y + HEIGHT/2);
        canvas.drawRect(rect, paint);
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public Point getPos() {
        return pos;
    }

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

    /*public int getxSpeed() { `1
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
    }*/
}
