package de.kolbenschurzgames.whack_a_toffel.app.sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import org.androidannotations.annotations.EBean;

import java.io.IOException;

import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelType;

/**
 * Created by twankerl on 23.05.15.
 */
@EBean(scope = EBean.Scope.Singleton)
public class SoundUtil {

    public void playToffelTappedSound(Context context, ToffelType toffelType) {
        TapSound sound = getTapSoundForToffelType(toffelType);
        createMediaPlayerAndStart(context, sound.getIdentifier(), false);
    }

    public void playToffelMissedSound(Context context) {
        TapMissedSound sound = TapMissedSound.getRandomSound();
        createMediaPlayerAndStart(context, sound.getIdentifier(), false);
    }

    MediaPlayer createMediaPlayerAndStart(Context context, int resourceId, final boolean looping) {
        AssetFileDescriptor assetFd = context.getResources().openRawResourceFd(resourceId);

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(looping);
                mediaPlayer.start();
            }
        });

        try {
            mediaPlayer.setDataSource(assetFd.getFileDescriptor(), assetFd.getStartOffset(), assetFd.getLength());
        } catch (IOException e) {
            Log.e("SoundUtil", "Error while setting Data Source", e);
        }
        mediaPlayer.prepareAsync();
        return mediaPlayer;
    }

    private TapSound getTapSoundForToffelType(ToffelType type) {
        switch (type) {
            case REGULAR:
            default:
                return TapSound.SCHOAS;
            case EVIL:
                return TapSound.FINGA_WEG;
        }
    }
}
