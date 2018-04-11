package cz.matyapav.valentinesday;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {



    Game game;
    Button buttonUp;
    Button buttonDown;
    CheckBox buttonSound;
    ImageButton buttonShoot;

    TextView scoreView;
    TextView livesView;
    RelativeLayout heartsLayout;
    View player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        init();
        game = new Game(this, heartsLayout, player, livesView, scoreView);
        game.setTopScore(settings.getInt("topScore", 0));
        game.startMusic();
        Controls controls = new Controls(buttonUp, buttonDown, buttonShoot, buttonSound, game);
        controls.init();
    }

    private void init(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initializeViews();
        startBackgroundAnimation();
    }

    private void initializeViews(){
        heartsLayout = (RelativeLayout) findViewById(R.id.heartLayout);
        player = findViewById(R.id.player);
        scoreView = (TextView) findViewById(R.id.score);
        livesView = (TextView) findViewById(R.id.lives);
        scoreView.setText("Score: 0");
        livesView.setText("Lives: " + Constants.DEFAULT_LIVES);
        buttonUp = (Button) findViewById(R.id.upButton);
        buttonDown = (Button) findViewById(R.id.downButton);
        buttonShoot = (ImageButton) findViewById(R.id.shootButton);
        buttonSound = (CheckBox) findViewById(R.id.soundButton);
    }



    private void startBackgroundAnimation(){
        //start backgound animation
        final ImageView backgroundOne = (ImageView) findViewById(R.id.background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(20000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = -width * progress;
                backgroundOne.setTranslationX(translationX + width);
                backgroundTwo.setTranslationX(translationX);
            }
        });
        animator.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.game.clearHandlers();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //TODO zjistit jak handlery jenom pausnout a pak pustit .. tedka vsechno mazu a krome hudby poustim odznova
    @Override
    protected void onPause() {
        super.onPause();
        game.getMediaPlayer().pause();
        game.clearHandlers();
        game.clearGameSpace();
    }

    @Override
    protected void onResume() {
        super.onResume();
        game.getMediaPlayer().start();
        game.registerHeartsSpawnHandler();
    }
}


