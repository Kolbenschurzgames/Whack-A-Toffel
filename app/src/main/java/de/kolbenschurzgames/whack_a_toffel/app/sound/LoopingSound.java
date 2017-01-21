package de.kolbenschurzgames.whack_a_toffel.app.sound;

import android.content.Context;
import android.media.MediaPlayer;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

/**
 * Created by tom on 27.05.15.
 */
@EBean(scope = EBean.Scope.Singleton)
public abstract class LoopingSound {

    @Bean
    protected SoundUtil soundUtil;

    private MediaPlayer currentPlayer;
    private int id;

    LoopingSound(int id) {
        this.id = id;
    }

    public void start(Context context) {
        if (currentPlayer == null) {
            currentPlayer = soundUtil.createMediaPlayerAndStart(context, this.id, true);
        }
    }

    public void stop() {
        currentPlayer.release();
        currentPlayer = null;
    }
}
