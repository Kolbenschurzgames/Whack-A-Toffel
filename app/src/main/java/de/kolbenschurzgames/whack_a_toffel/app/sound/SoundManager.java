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

    private final static List<TapSound> SOUNDS = Arrays.asList(TapSound.values());
    private static final int SIZE = SOUNDS.size();
    private static final Random RANDOM = new Random();

    public void playRandomTapSound(Context context) {

        TapSound tapSound = SOUNDS.get(RANDOM.nextInt(SIZE));

        playSound(context, tapSound.getIdentifier());
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
