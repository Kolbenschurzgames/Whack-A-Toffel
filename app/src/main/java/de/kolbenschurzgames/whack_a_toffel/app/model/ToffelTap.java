package de.kolbenschurzgames.whack_a_toffel.app.model;

import java.util.Objects;

/**
 * Created by alfriedl on 13.02.15.
 */
public final class ToffelTap {

    private final ToffelField field;
    private final boolean tapped;
    private final ToffelType toffelType;

    public ToffelTap(ToffelField field, boolean tapped, ToffelType type) {
        this.field = field;
        this.tapped = tapped;
        this.toffelType = type;
    }

    public ToffelField getField() {
        return field;
    }

    public boolean isTapped() {
        return tapped;
    }

    public ToffelType getToffelType() {
        return toffelType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToffelTap toffelTap = (ToffelTap) o;
        return tapped == toffelTap.tapped &&
                field == toffelTap.field;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, tapped);
    }

    @Override
    public String toString() {
        return "ToffelTap{" +
                "field=" + field +
                ", tapped=" + tapped +
                ", toffelType=" + toffelType +
                '}';
    }
}
