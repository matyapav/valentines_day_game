package cz.matyapav.valentinesday;

import android.view.View;

/**
 * @author Pavel Matyáš (matyapav@fel.cvut.cz).
 * @since 1.0.0..
 */
//TODO add heart ability
public class Heart {

    private View heartView;
    private int sizeX;
    private int sizeY;
    private WasShot wasShot;
    private int points;

    public Heart(View heartView, int sizeX, int sizeY, int points, WasShot wasShot) {
        this.heartView = heartView;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.points = points;
        this.wasShot = wasShot;
    }

    public View getHeartView() {
        return heartView;
    }

    public void setHeartView(View heartView) {
        this.heartView = heartView;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public WasShot isWasShot() {
        return wasShot;
    }

    public void setWasShot(WasShot wasShot) {
        this.wasShot = wasShot;
    }
}
