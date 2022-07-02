package main.main.pongsoccerchallenge;

import android.graphics.Point;

public class Ball {
    final private int RADIUS;
    private Point pos;
    private int speedX;
    private int speedY;
    private int dirY;
    private int dirX;

    public Ball(int radius) {
        RADIUS = radius;
        pos = new Point();
    }

    public void updatePos() {
        pos.x += speedX * dirX;
        pos.y += speedY * dirY;
    }

    public void reset(int fieldCenterX, int fieldCenterY, int opponentBrain) {
        pos.x = fieldCenterX;
        pos.y = fieldCenterY;
        speedX = 7 + opponentBrain / 2;
        speedY = 7 + opponentBrain / 2;
        dirX = 1;
        dirY = -1;
    }

    public void handleCollisionWall(CollisionDirection collisionDirection) {
        if (collisionDirection.equals(CollisionDirection.LEFT) || collisionDirection.equals(CollisionDirection.RIGHT)) {
            dirX *= -1;
        }
        if (collisionDirection.equals(CollisionDirection.TOP) || collisionDirection.equals(CollisionDirection.BOTTOM)) {
            dirY *= -1;
        }
    }

    public CollisionDirection testCollisionWall(int SCREEN_WIDTH, int SCREEN_HEIGHT) {
        CollisionDirection collisionDirection = null;

        if (getTop() < 0 && dirY < 0) {
            collisionDirection = CollisionDirection.TOP;
        } else if (getBottom() > SCREEN_HEIGHT && dirY > 0) {
            collisionDirection = CollisionDirection.BOTTOM;
        } else if (getLeft() < 0 && dirX < 0) {
            collisionDirection = CollisionDirection.LEFT;
        } else if (getRight() > SCREEN_WIDTH && dirX > 0) {
            collisionDirection = CollisionDirection.RIGHT;
        }

        if (collisionDirection != null) {
            handleCollisionWall(collisionDirection);
        }

        return collisionDirection;
    }

    public void handleCollision(Object object, CollisionDirection collisionDirection) {
        if (collisionDirection.equals(CollisionDirection.TOP)) {
            dirY *= -1;
            if (speedX < 40) {
                speedX += 1;
            }
            if (speedY < 40) {
                speedY += 1;
            }
            pos.y = object.getBottom() + RADIUS + 25;
        } else if (collisionDirection.equals(CollisionDirection.BOTTOM)) {
            dirY *= -1;
            if (speedX < 40) {
                speedX += 1;
            }
            if (speedY < 40) {
                speedY += 1;
            }
            pos.y = object.getTop() - RADIUS - 25;
        } else if (collisionDirection.equals(CollisionDirection.LEFT)) {
            dirX *= -1;
            pos.x = object.getRight() + RADIUS + 5;
        } else if (collisionDirection.equals(CollisionDirection.RIGHT)) {
            dirX *= -1;
            pos.x = object.getLeft() - RADIUS - 5;
        }
    }

    public CollisionDirection testCollision(Object object) {
        int distance_from_top = getTop() - object.getBottom();
        int distance_from_bottom = object.getTop() - getBottom();
        int distance_from_left = getLeft() - object.getRight();
        int distance_from_right = object.getLeft() - getRight();

        if (distance_from_bottom >= 0 || distance_from_top >= 0 || distance_from_right >= 0 || distance_from_left > 0)
            return null;

        CollisionDirection collisionDirection = CollisionDirection.TOP;
        int min = Math.abs(distance_from_top);

        if (Math.abs(distance_from_bottom) < min) {
            collisionDirection = CollisionDirection.BOTTOM;
            min = Math.abs(distance_from_bottom);
        }

        if (Math.abs(distance_from_left) < min) {
            collisionDirection = CollisionDirection.LEFT;
            min = Math.abs(distance_from_left);
        }

        if (Math.abs(distance_from_right) < min) {
            collisionDirection = CollisionDirection.RIGHT;
            min = Math.abs(distance_from_right);
        }

        handleCollision(object, collisionDirection);

        return collisionDirection;
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

    public int getDirY() {
        return dirY;
    }

    public void setDirY(int dirY) {
        this.dirY = dirY;
    }

    public int getDirX() {
        return dirX;
    }

    public void setDirX(int dirX) {
        this.dirX = dirX;
    }
}
