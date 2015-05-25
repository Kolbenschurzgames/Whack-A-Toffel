package de.kolbenschurzgames.whack_a_toffel.app.sound;

import android.content.Context;
import android.media.MediaPlayer;

import org.androidannotations.annotations.EBean;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by twankerl on 23.05.15.
 */
@EBean(scope = EBean.Scope.Singleton)
public class SoundManager {

    private final static List<TapMissedSound> TAPMISSED_SOUNDS = Arrays.asList(TapMissedSound.values());
    private final static int TAPMISSED_SOUNDS_SIZE = TAPMISSED_SOUNDS.size();

    private final static Random RANDOM = new Random();

    public void playToffelTappedSound(Context context) {

        if (RANDOM.nextBoolean()) {
            playSound(context, TapSound.SCHOAS.getIdentifier());
        }
    }

    public void playToffelMissedSound(Context context) {
        TapMissedSound sound = TAPMISSED_SOUNDS.get(RANDOM.nextInt(TAPMISSED_SOUNDS_SIZE));
        playSound(context, sound.getIdentifier());
    }

    private void playSound(Context context, int id) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, id);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
    }

}
