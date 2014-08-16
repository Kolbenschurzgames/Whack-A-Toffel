package de.kolbenschurzgames.whack_a_toffel.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import de.kolbenschurzgames.whack_a_toffel.app.game.GameActivity;

public class MainActivity extends Activity {

	private Button startGameButton;
	private Button exitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startGameButton = (Button) findViewById(R.id.button_start_game);
		startGameButton.setOnClickListener(new OnStartButtonClickListener());
		exitButton = (Button) findViewById(R.id.button_exit);
		exitButton.setOnClickListener(new OnExitButtonClickListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_start_game:
				startNewGame();
				return true;
			case R.id.menu_item_exit:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void startNewGame() {
		Intent startGameIntent = new Intent(MainActivity.this, GameActivity.class);
		startActivity(startGameIntent);
	}

	private class OnStartButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			startNewGame();
		}
	}

	private class OnExitButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}
}
