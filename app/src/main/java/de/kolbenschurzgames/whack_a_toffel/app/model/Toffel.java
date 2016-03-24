package de.kolbenschurzgames.whack_a_toffel.app.model;

import android.graphics.Point;

import java.util.Random;

public class Toffel {

    private static final double VISIBILITY_RANDOMNESS_THRESHOLD = 0.01;

    private final Random random = new Random();

    private final int xPosition;
    private final int yPosition;

    private ToffelType type;

    private int drawTicksCounter = 0;

    private boolean visible = false;

    public Toffel(Point position) {
        this.xPosition = position.x;
        this.yPosition = position.y;
        this.type = ToffelType.REGULAR;
    }

    public ToffelType getType() {
        return type;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getDrawTicksCounter() {
        return drawTicksCounter;
    }

    public void randomizeState() {
        visible = random.nextDouble() < VISIBILITY_RANDOMNESS_THRESHOLD;
        type = ToffelType.randomToffelType();
    }

    public void hide() {
        this.visible = false;
    }

    public void incrementDrawTicksCounter() {
        this.drawTicksCounter++;
    }

    public void resetDrawTicksCounter() {
        this.drawTicksCounter = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Toffel toffel = (Toffel) o;

        return xPosition == toffel.xPosition && yPosition == toffel.yPosition && type == toffel.type;
    }

    @Override
    public int hashCode() {
        int result = xPosition;
        result = 31 * result + yPosition;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Toffel{" +
                "xPosition=" + xPosition +
                ", yPosition=" + yPosition +
                ", type=" + type +
                ", drawTicksCounter=" + drawTicksCounter +
                ", visible=" + visible +
                '}';
    }
}
