package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.model.Toffel;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelField;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.DrawableRes;

import java.util.HashMap;
import java.util.Map;

@EBean(scope = EBean.Scope.Singleton)
class ToffelManager {
	private final static int NUMBER_OF_TOFFELS = 9;

	@DrawableRes(R.drawable.hole_toffel)
	Drawable toffelResource;
	@DrawableRes(R.drawable.hole_empty)
	Drawable emptyHoleResource;

	private Bitmap toffel;
	private Bitmap scaledToffel;
	private Bitmap hole;
	private Bitmap scaledHole;

	private int offset;
	private int padding_top;
	private int edge_length;

	private Map<ToffelField, Toffel> toffelSack = new HashMap<ToffelField, Toffel>(NUMBER_OF_TOFFELS);

	@AfterInject
	void initBitmaps() {
		this.toffel = ((BitmapDrawable) toffelResource).getBitmap();
		this.hole = ((BitmapDrawable) emptyHoleResource).getBitmap();
	}

	void initializeToffelHood(int width, int height) throws IllegalStateException {
		this.offset = 0;
		this.padding_top = (height - width) / 2;
		this.edge_length = width / 3;
		this.scaledHole = Bitmap.createScaledBitmap(hole, edge_length - offset, edge_length - offset, false);
		this.scaledToffel = Bitmap.createScaledBitmap(toffel, edge_length - offset, edge_length - offset, false);

		if (this.scaledHole != null && this.scaledToffel != null) {
			plantToffels();
		} else {
			throw new IllegalStateException("Error while resizing graphics");
		}
	}

	private void plantToffels() {
		toffelSack.put(ToffelField.FIELD_TOP_LEFT, new Toffel(this.scaledToffel, this.scaledHole, new Point(offset, padding_top + offset)));
		toffelSack.put(ToffelField.FIELD_TOP_MIDDLE, new Toffel(this.scaledToffel, this.scaledHole, new Point(edge_length + offset, padding_top + offset)));
		toffelSack.put(ToffelField.FIELD_TOP_RIGHT, new Toffel(this.scaledToffel, this.scaledHole, new Point((2 * edge_length) + offset, padding_top + offset)));
		toffelSack.put(ToffelField.FIELD_MIDDLE_LEFT, new Toffel(this.scaledToffel, this.scaledHole, new Point(offset, padding_top + edge_length + offset)));
		toffelSack.put(ToffelField.FIELD_MIDDLE_MIDDLE, new Toffel(this.scaledToffel, this.scaledHole, new Point(edge_length + offset, padding_top + edge_length + offset)));
		toffelSack.put(ToffelField.FILED_MIDDLE_RIGHT, new Toffel(this.scaledToffel, this.scaledHole, new Point((2 * edge_length) + offset, padding_top + edge_length + offset)));
		toffelSack.put(ToffelField.FIELD_BOTTOM_LEFT, new Toffel(this.scaledToffel, this.scaledHole, new Point(offset, padding_top + (2 * edge_length) + offset)));
		toffelSack.put(ToffelField.FIELD_BOTTOM_MIDDLE, new Toffel(this.scaledToffel, this.scaledHole, new Point(edge_length + offset, padding_top + (2 * edge_length) + offset)));
		toffelSack.put(ToffelField.FIELD_BOTTOM_RIGHT, new Toffel(this.scaledToffel, this.scaledHole, new Point((2 * edge_length) + offset, padding_top + (2 * edge_length) + offset)));
	}

	void updateToffels(Canvas canvas) {
		for (Toffel toffel : toffelSack.values()) {
			toffel.updateToffel(canvas);
		}
	}

	ToffelTap getTapResult(float x, float y) {
		boolean isTapped = false;
		ToffelField field = ToffelField.NONE;

		for (Map.Entry<ToffelField, Toffel> toffelEntry : toffelSack.entrySet()) {
			Toffel t = toffelEntry.getValue();
			if (t.isTapped(x, y)) {
				isTapped = true;
				field = toffelEntry.getKey();
				break;
			}
		}

		return new ToffelTap(field, isTapped);
	}

	void toffelTapped(ToffelField field) {
		Toffel toffel = toffelSack.get(field);
		if (toffel != null) {
			toffel.hideToffel();
		} else {
			Log.w("ToffelManager", "toffelTapped invoked for Field " + field.name() + " with no toffel");
		}
	}
}
