package de.kolbenschurzgames.whack_a_toffel.app.sound;

import org.androidannotations.annotations.EBean;

import de.kolbenschurzgames.whack_a_toffel.app.R;

/**
 * Created by tom on 25.05.15.
 */
@EBean(scope = EBean.Scope.Singleton)
public class TitleSound extends LoopingSound {

    public TitleSound() {
        super(R.raw.titelmusik);
    }
}
