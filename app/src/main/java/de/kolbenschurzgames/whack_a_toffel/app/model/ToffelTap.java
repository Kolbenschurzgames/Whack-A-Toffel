package de.kolbenschurzgames.whack_a_toffel.app.model;

/**
 * Created by alfriedl on 13.02.15.
 */
public final class ToffelTap {

	private ToffelField field;
	private boolean tapped;

	public ToffelTap(ToffelField field, boolean tapped) {
		this.field = field;
		this.tapped = tapped;
	}

	public ToffelField getField() {
		return field;
	}

	public boolean isTapped() {
		return tapped;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ToffelTap toffelTap = (ToffelTap) o;

		if (tapped != toffelTap.tapped) return false;
		if (field != toffelTap.field) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = field != null ? field.hashCode() : 0;
		result = 31 * result + (tapped ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ToffelTap{" +
				"field=" + field +
				", tapped=" + tapped +
				'}';
	}
}
