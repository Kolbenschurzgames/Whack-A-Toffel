package de.kolbenschurzgames.whack_a_toffel.app;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import de.kolbenschurzgames.whack_a_toffel.app.game.GameActivity_;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.HighscoreActivity_;
import de.kolbenschurzgames.whack_a_toffel.app.sound.TitleSound;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends Activity {

    @Bean
    protected TitleSound titleSound;

    @ViewById(R.id.button_start_game)
    protected Button startGameButton;

    @ViewById(R.id.button_highscores)
    protected Button highscoresButton;

    @OptionsItem(R.id.menu_item_start_game)
    @Click(R.id.button_start_game)
    protected void startNewGame() {
        Intent startGameIntent = new Intent(MainActivity.this, GameActivity_.class);
        startActivity(startGameIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        titleSound.start(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        titleSound.stop();
    }


    @Click(R.id.button_highscores)
    protected void showHighscores() {
        HighscoreActivity_.intent(MainActivity.this).start();
    }
}
