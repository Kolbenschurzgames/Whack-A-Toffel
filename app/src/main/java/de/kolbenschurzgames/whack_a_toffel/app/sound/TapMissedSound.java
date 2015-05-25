package de.kolbenschurzgames.whack_a_toffel.app.sound;

import de.kolbenschurzgames.whack_a_toffel.app.R;

/**
 * Created by twankerl on 23.05.15.
 */
enum TapMissedSound {

    FINGA_WEG(R.raw.finga_weg),
    OPFAAA(R.raw.opfaaa);

    private int identifier;

    TapMissedSound(int identifier) {
        this.identifier = identifier;
    }

    int getIdentifier() {
        return identifier;
    }
}
