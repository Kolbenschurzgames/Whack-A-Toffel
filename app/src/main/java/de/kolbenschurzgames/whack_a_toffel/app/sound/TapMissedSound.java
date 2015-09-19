package de.kolbenschurzgames.whack_a_toffel.app.sound;

import de.kolbenschurzgames.whack_a_toffel.app.R;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by twankerl on 23.05.15.
 */
enum TapMissedSound {

    OPFAAA(R.raw.opfaaa);

    private final static List<TapMissedSound> TAPMISSED_SOUNDS = Arrays.asList(TapMissedSound.values());
    private final static int TAPMISSED_SOUNDS_SIZE = TAPMISSED_SOUNDS.size();

    private final static Random RANDOM = new Random();

    private int identifier;

    TapMissedSound(int identifier) {
        this.identifier = identifier;
    }

    int getIdentifier() {
        return identifier;
    }

    static TapMissedSound getRandomSound() {
        return TAPMISSED_SOUNDS.get(RANDOM.nextInt(TAPMISSED_SOUNDS_SIZE));
    }
}
