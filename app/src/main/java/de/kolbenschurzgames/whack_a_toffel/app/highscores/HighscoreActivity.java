package de.kolbenschurzgames.whack_a_toffel.app.highscores;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.model.Highscore;
import de.kolbenschurzgames.whack_a_toffel.app.network.NetworkUtils;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceCallback;
import de.kolbenschurzgames.whack_a_toffel.app.network.WebServiceHelper;
import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.StringRes;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by alfriedl on 19.09.14.
 */
@EActivity(R.layout.activity_highscores)
public class HighscoreActivity extends Activity implements WebServiceCallback<Highscore> {

    @ViewById(R.id.highscores_text_view)
    TextView textView;

    @ViewById(R.id.highscores_table)
    TableLayout highscoresTable;

    @ViewById(R.id.highscores_scroll_view)
    ScrollView scrollView;

    @ViewById(R.id.highscores_progress)
    ProgressBar progressBar;

    @Bean
    WebServiceHelper webServiceHelper;

    @StringRes(R.string.no_connection)
    String noConnectionString;

    @StringRes(R.string.load_highscores_error)
    String loadHighscoresErrorString;

    @Extra("highlight")
    String idToHighlight;

    private LayoutInflater inflater;

    private TableRow highlightedRow;

    @AfterViews
    protected void init() {
        inflater = getLayoutInflater();
        if (NetworkUtils.isConnectionAvailable(this)) {
            fetchHighscores();
        } else {
            displayError(noConnectionString);
        }
    }

    @Background
    void fetchHighscores() {
        webServiceHelper.getListOfHighscores(this);
    }

    @Override
    public void onResultListReceived(List<Highscore> highscores) {
        sortHighscoresDescending(highscores);
        displayHighscores(highscores);
    }

    private void sortHighscoresDescending(List<Highscore> highscores) {
        Collections.sort(highscores, Collections.reverseOrder());
    }

    @Override
    public void onError(Error e) {
        Log.e("Highscores", e.getMessage(), e);
        displayError(loadHighscoresErrorString);
    }

    @UiThread
    void displayError(String errorMsg) {
        highscoresTable.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.GONE);
        textView.setText(errorMsg);
        textView.setVisibility(View.VISIBLE);
    }

    @UiThread
    void displayHighscores(List<Highscore> highscores) {
        progressBar.setVisibility(View.GONE);
        textView.setVisibility(View.INVISIBLE);
        highscoresTable.setVisibility(View.VISIBLE);

        populateHighscoresTable(highscores);
        if (highlightedRow != null) {
            centerRow(highlightedRow);
        }
    }

    private void centerRow(final TableRow row) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                int y = (row.getTop() + row.getBottom() - scrollView.getHeight()) / 2;
                scrollView.smoothScrollTo(0, y);
            }
        });
    }

    private void populateHighscoresTable(List<Highscore> highscores) {
        for (int i = 0; i < highscores.size(); i++) {
            Highscore highscore = highscores.get(i);
            int position = i + 2;
            TableRow row = buildHighscoreTableRow(highscore, position);
            highscoresTable.addView(row, position);
        }
    }

    private TableRow buildHighscoreTableRow(Highscore highscore, int position) {
        final TableRow row = (TableRow) inflater.inflate(R.layout.table_row_highscore, null);
        ((TextView) row.getChildAt(0)).setText(Integer.toString(position));
        ((TextView) row.getChildAt(1)).setText(highscore.getName());
        ((TextView) row.getChildAt(2)).setText(Integer.toString(highscore.getScore()));
        ((TextView) row.getChildAt(3)).setText(buildLocalizedDateTimeString(highscore.getDate()));
        if (idToHighlight != null && idToHighlight.equals(highscore.getId())) {
            row.setBackgroundColor(Color.parseColor("#66FFFFFF"));
            ((TextView) row.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) row.getChildAt(1)).setTextColor(Color.BLACK);
            ((TextView) row.getChildAt(2)).setTextColor(Color.BLACK);
            ((TextView) row.getChildAt(3)).setTextColor(Color.BLACK);
            highlightedRow = row;
        }
        return row;
    }

    String buildLocalizedDateTimeString(Date date) {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
        String dateString = dateFormat.format(date);
        String timeString = timeFormat.format(date);
        return dateString + " " + timeString;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }
}
