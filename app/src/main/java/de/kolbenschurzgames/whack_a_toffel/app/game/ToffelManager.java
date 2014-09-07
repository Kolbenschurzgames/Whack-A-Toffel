package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

public class ToffelManager {

    private Bitmap toffel;
    private Bitmap scaledToffel;
    private Bitmap hole;
    private Bitmap scaledHole;
    private int offset;
    private int padding_top;
    private int edge_length;

    private enum ToffelField {
        FIELD_TOP_LEFT,
        FIELD_TOP_MIDDLE,
        FIELD_TOP_RIGHT,
        FIELD_MIDDLE_LEFT,
        FIELD_MIDDLE_MIDDLE,
        FILED_MIDDLE_RIGHT,
        FIELD_BOTTOM_LEFT,
        FIELD_BOTTOM_MIDDLE,
        FIELD_BOTTOM_RIGHT
    }

    private final static int NUMBER_OF_TOFFELS = 9;

    private Toffel[] toffelSack = new Toffel[NUMBER_OF_TOFFELS];
    private Point[] toffelField = new Point[NUMBER_OF_TOFFELS];

    public ToffelManager(Bitmap toffel, Bitmap hole) {
        this.toffel = toffel;
        this.hole = hole;
    }

    public void setSizes(int width, int height) throws Exception {
        this.offset = 0;
        this.padding_top = (height - width) / 2;
        this.edge_length = width / 3;
        this.scaledHole = Bitmap.createScaledBitmap(hole, edge_length - offset, edge_length - offset, false);
        this.scaledToffel = Bitmap.createScaledBitmap(toffel, edge_length - offset, edge_length - offset, false);

        if (this.scaledHole == null || this.scaledToffel == null) {
            throw new Exception("Error while resizing graphics");
        }
    }

    public void plantToffels() {
        toffelField[ToffelField.FIELD_TOP_LEFT.ordinal()] = new Point(offset, padding_top + offset);
        toffelField[ToffelField.FIELD_TOP_MIDDLE.ordinal()] = new Point(edge_length + offset, padding_top + offset);
        toffelField[ToffelField.FIELD_TOP_RIGHT.ordinal()] = new Point((2 * edge_length) + offset, padding_top + offset);
        toffelField[ToffelField.FIELD_MIDDLE_LEFT.ordinal()] = new Point(offset, padding_top + edge_length + offset);
        toffelField[ToffelField.FIELD_MIDDLE_MIDDLE.ordinal()] = new Point(edge_length + offset, padding_top + edge_length + offset);
        toffelField[ToffelField.FILED_MIDDLE_RIGHT.ordinal()] = new Point((2 * edge_length) + offset, padding_top + edge_length + offset);
        toffelField[ToffelField.FIELD_BOTTOM_LEFT.ordinal()] = new Point(offset, padding_top + (2 * edge_length) + offset);
        toffelField[ToffelField.FIELD_BOTTOM_MIDDLE.ordinal()] = new Point(edge_length + offset, padding_top + (2 * edge_length) + offset);
        toffelField[ToffelField.FIELD_BOTTOM_RIGHT.ordinal()] = new Point((2 * edge_length) + offset, padding_top + (2 * edge_length) + offset);

        for (int i = 0; i < NUMBER_OF_TOFFELS; i++) {
            toffelSack[i] = new Toffel(this.scaledToffel, this.scaledHole, toffelField[i]);
        }
    }

    public void updateToffels(Canvas canvas) {
        for (int i = 0; i < NUMBER_OF_TOFFELS; i++) {
            Toffel toffel = toffelSack[i];
            toffel.updateToffel(canvas);
        }
    }
}
