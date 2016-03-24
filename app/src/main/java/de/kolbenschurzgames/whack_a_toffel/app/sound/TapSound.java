package de.kolbenschurzgames.whack_a_toffel.app.sound;

import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelType;

/**
 * Created by twankerl on 23.05.15.
 */
enum TapSound {

    SCHOAS(R.raw.schoas),
    FINGA_WEG(R.raw.finga_weg);

    private int identifier;

    TapSound(int identifier) {
        this.identifier = identifier;
    }

    int getIdentifier() {
        return identifier;
    }
}
