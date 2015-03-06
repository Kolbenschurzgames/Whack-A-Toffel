package de.kolbenschurzgames.whack_a_toffel.app.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alfriedl on 20.09.14.
 */
public class Highscore implements Comparable<Highscore> {

	private final String name;
	private final int score;
	private final Date date;

	public Highscore(String name, int score, Date date) {
		this.name = name;
		this.score = score;
		this.date = date;
	}

	public static List<Highscore> parseJsonArrayToList(JSONArray array) throws JSONException {
		List<Highscore> list = new ArrayList<Highscore>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			Highscore highscore = parseJsonObjectToHighscore(obj);
			list.add(highscore);
		}
		return list;
	}

	private static Highscore parseJsonObjectToHighscore(JSONObject obj) throws JSONException {
		String name = obj.getString("name");
		int score = obj.getInt("score");
		Date date = new Date(obj.getLong("timestamp"));
		return new Highscore(name, score, date);
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public Date getDate() {
		return date;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("name", this.name);
		json.put("score", this.score);
		json.put("timestamp", this.date.getTime());
		return json;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Highscore highscore = (Highscore) o;

		if (score != highscore.score) return false;
		if (!date.equals(highscore.date)) return false;
		if (!name.equals(highscore.name)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + score;
		result = 31 * result + date.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Highscore{" +
				"name='" + name + '\'' +
				", score=" + score +
				", date=" + date +
				'}';
	}

	@Override
	public int compareTo(Highscore another) {
		return this.getScore() - another.getScore();
	}
}
