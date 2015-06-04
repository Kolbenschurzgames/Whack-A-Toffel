package de.kolbenschurzgames.whack_a_toffel.app.sound;

import org.androidannotations.annotations.EBean;

import de.kolbenschurzgames.whack_a_toffel.app.R;

/**
 * Created by tom on 27.05.15.
 */
@EBean(scope = EBean.Scope.Singleton)
public class GameSound extends LoopingSound {

    public GameSound() {
        super(R.raw.main_melody);
    }
}
