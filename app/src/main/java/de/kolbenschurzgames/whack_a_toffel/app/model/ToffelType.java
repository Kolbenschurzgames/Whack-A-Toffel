package de.kolbenschurzgames.whack_a_toffel.app.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by alfriedl on 19.09.15.
 */
public enum ToffelType {

    REGULAR(1),
    EVIL(-1);

    private final int score;

    ToffelType(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    private static final Random RANDOM = new Random();
    private static final List<ToffelType> TYPES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = TYPES.size();

    public static ToffelType randomToffelType() {
        return TYPES.get(RANDOM.nextInt(SIZE));
    }
}
