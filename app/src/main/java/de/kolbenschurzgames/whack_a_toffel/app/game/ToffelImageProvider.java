package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.DrawableRes;

import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelType;

/**
 * Created by alfriedl on 19.09.15.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ToffelImageProvider {

    @DrawableRes(R.drawable.hole_toffel)
    Drawable toffelResource;

    @DrawableRes(R.drawable.hole_empty)
    Drawable emptyHoleResource;

    @DrawableRes(R.drawable.hole_eviltoffel)
    Drawable evilToffelResource;

    private Bitmap toffel;
    private Bitmap scaledToffel;
    private Bitmap hole;

    private Bitmap scaledHole;
    private Bitmap evilToffel;
    private Bitmap scaledEvilToffel;

    @AfterInject
    void initBitmaps() {
        this.toffel = ((BitmapDrawable) toffelResource).getBitmap();
        this.hole = ((BitmapDrawable) emptyHoleResource).getBitmap();
        this.evilToffel = ((BitmapDrawable) evilToffelResource).getBitmap();
    }

    public void createScaledBitmaps(int offset, int edgeLength) {
        this.scaledHole = Bitmap.createScaledBitmap(hole, edgeLength - offset, edgeLength - offset, false);
        this.scaledToffel = Bitmap.createScaledBitmap(toffel, edgeLength - offset, edgeLength - offset, false);
        this.scaledEvilToffel = Bitmap.createScaledBitmap(evilToffel, edgeLength - offset, edgeLength - offset, false);
    }

    public Bitmap getImageForToffelType(ToffelType type) {
        switch (type) {
            case REGULAR:
                return scaledToffel;
            case EVIL:
                return scaledEvilToffel;
            default:
                throw new IllegalStateException("Toffel type " + type + " has no image representation");
        }
    }

    public Bitmap getScaledHole() {
        return scaledHole;
    }
}
