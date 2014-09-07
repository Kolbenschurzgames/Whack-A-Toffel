package de.kolbenschurzgames.whack_a_toffel.app;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import de.kolbenschurzgames.whack_a_toffel.app.game.GameActivity;
import de.kolbenschurzgames.whack_a_toffel.app.game.GameActivity_;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.HighscoreActivity_;
import org.androidannotations.annotations.*;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends Activity {

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

	@Click(R.id.button_highscores)
	protected void showHighscores() {
		HighscoreActivity_.intent(MainActivity.this).start();
	}
}
