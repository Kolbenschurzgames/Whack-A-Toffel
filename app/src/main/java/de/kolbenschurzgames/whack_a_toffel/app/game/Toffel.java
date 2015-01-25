package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

public class Toffel {
    private Bitmap toffel;
    private Bitmap hole;
    private boolean isVisible = false;
    private int x_position;
    private int y_position;
    private int counter = 0;

    public Toffel(Bitmap toffel, Bitmap hole, Point position) {
        this.toffel = toffel;
        this.hole = hole;
        this.x_position = position.x;
        this.y_position = position.y;
    }

    public void updateToffel(Canvas canvas) {
        if (isVisible) {
            canvas.drawBitmap(toffel, this.x_position, this.y_position, null);
            counter++;
            if (counter > 50) {
                isVisible = false;
                counter = 0;
            }
        } else {
            canvas.drawBitmap(hole, this.x_position, this.y_position, null);
            shouldBeShown();
        }
    }

    private void shouldBeShown() {
        double randomValue = Math.random();
        isVisible = randomValue < 0.01;
    }
}
