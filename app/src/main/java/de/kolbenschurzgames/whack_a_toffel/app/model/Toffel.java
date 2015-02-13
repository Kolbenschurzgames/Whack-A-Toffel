package de.kolbenschurzgames.whack_a_toffel.app.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

public class Toffel {
    private Bitmap toffel;
    private Bitmap hole;
    private boolean isVisible = false;
    private int xPosition;
    private int yPosition;
    private int counter = 0;

    public Toffel(Bitmap toffel, Bitmap hole, Point position) {
        this.toffel = toffel;
        this.hole = hole;
        this.xPosition = position.x;
        this.yPosition = position.y;
    }

    public void updateToffel(Canvas canvas) {
        if (isVisible) {
            canvas.drawBitmap(toffel, this.xPosition, this.yPosition, null);
            counter++;
            if (counter > 50) {
                isVisible = false;
                counter = 0;
            }
        } else {
            canvas.drawBitmap(hole, this.xPosition, this.yPosition, null);
            shouldBeShown();
        }
    }

    private void shouldBeShown() {
        double randomValue = Math.random();
        isVisible = randomValue < 0.01;
    }

    public boolean isTapped(float x, float y) {
        return isVisible &&
                x >= this.xPosition && x < this.xPosition + toffel.getWidth() &&
                y >= yPosition && y < yPosition + toffel.getHeight();
    }

    public void hideToffel() {
        this.isVisible = false;
    }

}
