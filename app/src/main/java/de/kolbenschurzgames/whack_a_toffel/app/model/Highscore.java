package de.kolbenschurzgames.whack_a_toffel.app.model;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by alfriedl on 20.09.14.
 */
public class Highscore implements Comparable<Highscore> {

    private final String name;
    private final int score;
    private final Date date;
    private final String id;

    public Highscore(String name, int score, Date date) {
        this(name, score, date, null);
    }

    public Highscore(String name, int score, Date date, String id) {
        this.name = name;
        this.score = score;
        this.date = date;
        this.id = id;
    }

    public static List<Highscore> parseJsonArrayToList(JSONArray array) throws JSONException {
        List<Highscore> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Highscore highscore = parseJsonObjectToHighscore(obj);
            list.add(highscore);
        }
        return list;
    }

    public static Highscore parseJsonObjectToHighscore(JSONObject obj) throws JSONException {
        String name = obj.getString("name");
        int score = obj.getInt("score");
        Date date = new Date(obj.getLong("timestamp"));
        String id = obj.optString("_id");
        return new Highscore(name, score, date, id);
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

    public String getId() {
        return id;
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
        return score == highscore.score &&
                Objects.equals(name, highscore.name) &&
                Objects.equals(date, highscore.date) &&
                Objects.equals(id, highscore.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score, date, id);
    }

    @Override
    public String toString() {
        return "Highscore{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", date=" + date +
                ", _id='" + id + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Highscore another) {
        return this.getScore() - another.getScore();
    }
}
