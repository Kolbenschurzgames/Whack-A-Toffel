package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.HashMap;
import java.util.Map;

import de.kolbenschurzgames.whack_a_toffel.app.model.Toffel;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelField;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelTap;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelType;

@EBean(scope = EBean.Scope.Singleton)
public class ToffelManager {

    private static final int NUMBER_OF_TOFFELS = 9;
    private static final int DRAW_TICKS_VISIBILITY_THRESHOLD = 30;

    @Bean
    ToffelImageProvider imageProvider;

    private int offset;
    private int paddingTop;
    private int edgeLength;

    private final Map<ToffelField, Toffel> toffelSack = new HashMap<>(NUMBER_OF_TOFFELS);

    void initializeToffelHood(int width, int height) {
        this.offset = 0;
        this.paddingTop = (height - width) / 2;
        this.edgeLength = width / 3;
        plantToffels();
        imageProvider.createScaledBitmaps(offset, edgeLength);
    }

    private void plantToffels() {
        toffelSack.put(ToffelField.FIELD_TOP_LEFT, new Toffel(new Point(offset, paddingTop + offset)));
        toffelSack.put(ToffelField.FIELD_TOP_MIDDLE, new Toffel(new Point(edgeLength + offset, paddingTop + offset)));
        toffelSack.put(ToffelField.FIELD_TOP_RIGHT, new Toffel(new Point((2 * edgeLength) + offset, paddingTop + offset)));
        toffelSack.put(ToffelField.FIELD_MIDDLE_LEFT, new Toffel(new Point(offset, paddingTop + edgeLength + offset)));
        toffelSack.put(ToffelField.FIELD_MIDDLE_MIDDLE, new Toffel(new Point(edgeLength + offset, paddingTop + edgeLength + offset)));
        toffelSack.put(ToffelField.FIELD_MIDDLE_RIGHT, new Toffel(new Point((2 * edgeLength) + offset, paddingTop + edgeLength + offset)));
        toffelSack.put(ToffelField.FIELD_BOTTOM_LEFT, new Toffel(new Point(offset, paddingTop + (2 * edgeLength) + offset)));
        toffelSack.put(ToffelField.FIELD_BOTTOM_MIDDLE, new Toffel(new Point(edgeLength + offset, paddingTop + (2 * edgeLength) + offset)));
        toffelSack.put(ToffelField.FIELD_BOTTOM_RIGHT, new Toffel(new Point((2 * edgeLength) + offset, paddingTop + (2 * edgeLength) + offset)));
    }

    void updateToffels(Canvas canvas) {
        for (Toffel toffel : toffelSack.values()) {
            updateToffel(toffel, canvas);
        }
    }

    private void updateToffel(Toffel toffel, Canvas canvas) {
        if (toffel.isVisible()) {
            drawToffel(toffel, canvas);
            toffel.incrementDrawTicksCounter();
            if (toffel.getDrawTicksCounter() > DRAW_TICKS_VISIBILITY_THRESHOLD) {
                toffel.hide();
                toffel.resetDrawTicksCounter();
            }
        } else {
            canvas.drawBitmap(imageProvider.getScaledHole(), toffel.getXPosition(), toffel.getYPosition(), null);
            toffel.randomizeState();
        }
    }

    private void drawToffel(Toffel toffel, Canvas canvas) {
        Bitmap toffelImage = imageProvider.getImageForToffelType(toffel.getType());
        canvas.drawBitmap(toffelImage, toffel.getXPosition(), toffel.getYPosition(), null);
    }

    public ToffelTap getTapResult(float x, float y) {
        boolean isTapped = false;
        ToffelField field = ToffelField.NONE;
        ToffelType type = ToffelType.REGULAR;

        for (Map.Entry<ToffelField, Toffel> toffelEntry : toffelSack.entrySet()) {
            Toffel t = toffelEntry.getValue();
            if (isToffelFieldTapped(t, x, y)) {
                isTapped = t.isVisible();
                field = toffelEntry.getKey();
                type = t.getType();
                break;
            }
        }

        return new ToffelTap(field, isTapped, type);
    }

    private boolean isToffelFieldTapped(Toffel toffel, float x, float y) {
        Bitmap img = imageProvider.getImageForToffelType(toffel.getType());
        return x >= toffel.getXPosition() && x < toffel.getXPosition() + img.getWidth() &&
                y >= toffel.getYPosition() && y < toffel.getYPosition() + img.getHeight();
    }

    public void toffelTapped(ToffelField field) {
        Toffel toffel = toffelSack.get(field);
        if (toffel != null) {
            toffel.hide();
        } else {
            Log.w("ToffelManager", "toffelTapped invoked for field " + field.name() + " with no toffel");
        }
    }
}
