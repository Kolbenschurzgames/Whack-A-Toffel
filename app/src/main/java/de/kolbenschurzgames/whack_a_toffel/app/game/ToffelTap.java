package de.kolbenschurzgames.whack_a_toffel.app.game;

import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelField;

/**
 * Created by alfriedl on 13.02.15.
 */
final class ToffelTap {

	private ToffelField field;
	private boolean tapped;

	ToffelTap(ToffelField field, boolean tapped) {
		this.field = field;
		this.tapped = tapped;
	}

	ToffelField getField() {
		return field;
	}

	boolean isTapped() {
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
		final StringBuilder sb = new StringBuilder("ToffelTap{");
		sb.append("field=").append(field);
		sb.append(", tapped=").append(tapped);
		sb.append('}');
		return sb.toString();
	}
}
