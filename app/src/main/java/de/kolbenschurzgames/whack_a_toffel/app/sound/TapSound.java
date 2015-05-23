package de.kolbenschurzgames.whack_a_toffel.app.sound;

import de.kolbenschurzgames.whack_a_toffel.app.R;

/**
 * Created by twankerl on 23.05.15.
 */
public enum TapSound {

    SCHOAS(R.raw.schoas),
    FINGA_WEG(R.raw.finga_weg),
    OPFAAA(R.raw.opfaaa)
    ;

    private int identifier;

    TapSound(int identifier) {
        this.identifier = identifier;
    }

    public int getIdentifier() {
        return identifier;
    }
}
