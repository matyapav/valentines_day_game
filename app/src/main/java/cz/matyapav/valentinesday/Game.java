package cz.matyapav.valentinesday;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Pavel Matyáš (matyapav@fel.cvut.cz).
 * @since 1.0.0..
 */
public class Game {

    private Activity activity;
    private RelativeLayout layout;
    private ArrayList<Heart> hearts = new ArrayList<>();
    private int score;
    private int lives = Constants.DEFAULT_LIVES;
    private int hearthSpeed = Constants.DEFAULT_HEART_SPEED;
    private double spawnSeconds = Constants.DEFAULT_SPAWN_SECONDS;

    private int nextLevelIn = 50;
    private int nextSpeedIn = 100;

    private View player;
    private TextView livesView;
    private TextView scoreView;
    private int topScore;

    private MediaPlayer mediaPlayer;
    private boolean musicMuted = false;

    private Handler heartSpawnHandler = new Handler();
    private Handler heartMoveHandler = new Handler();
    private Handler hideScoreHandler = new Handler();
    private Handler shootMoveHandler = new Handler();

    //TODO tohle by chtelo cele projit a refactorovat
    public Game(Activity activity, RelativeLayout layout, View player, TextView livesView, TextView scoreView) {
        this.activity = activity;
        this.player = player;
        this.livesView = livesView;
        this.scoreView = scoreView;
        this.layout = layout;
    }

    public void registerShootMoveHandler(final View shoot) {

        Runnable shotMoveRunnable = new Runnable() {
            @Override
            public void run() {
                boolean isOnScreen = updateShootPosition(shoot);
                boolean moveOn = true;
                if (!isOnScreen) {
                    moveOn = false;
                }
                if (detectHeartCollision(shoot)) {
                    moveOn = false;
                }
                ;
                if (moveOn) {
                    shootMoveHandler.post(this);
                }
            }
        };
        shootMoveHandler.post(shotMoveRunnable);
    }

    public void registerHeartsSpawnHandler() {
        Runnable heartsSpawnRunnable = new Runnable() {
            @Override
            public void run() {
                final Heart heart = createHeart();
                final View hearthView = heart.getHeartView();
                layout.addView(hearthView);
                hearts.add(heart);
                registerHeartMoveHandler(heart);
                heartSpawnHandler.postDelayed(this, (long) (spawnSeconds*1000));
            }
        };
        heartSpawnHandler.postDelayed(heartsSpawnRunnable, (long) (spawnSeconds * 1000));
    }

    public void registerHeartMoveHandler(final Heart heart) {
        Runnable heartMove = new Runnable() {
            @Override
            public void run() {
                boolean isOnScreen = updateHeartPosition(heart.getHeartView());
                boolean moveOn = true;
                if (!isOnScreen) {
                    hearts.remove(heart);
                    lives--;
                    livesView.setText("Lives: " + lives);
                    SoundManager.playSound(getActivity(), R.raw.oh);
                    moveOn = false;
                }
                if (lives == 0) {
                    endGame();
                    moveOn = false;
                }
                if (moveOn && heart.isWasShot().equals(WasShot.NO)) {
                    heartMoveHandler.post(this);
                }
            }
        };
        heartMoveHandler.post(heartMove);
    }

    private void endGame() {
        //clear handlers
        clearHandlers();

        boolean newBestScore = false;
        //set new best score if any
        if(score > topScore) {
            SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            newBestScore = true;
            editor.putInt("topScore", score);
            editor.commit();
        }

        //prepare game over activity
        Intent i = new Intent(getActivity(), GameOverActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("score", score);
        if(newBestScore){
            i.putExtra("newBest", true);
        }
        getActivity().startActivity(i);
        getActivity().finish();
    }

    public void clearHandlers(){
        heartSpawnHandler.removeCallbacksAndMessages(null);
        heartMoveHandler.removeCallbacksAndMessages(null);
        shootMoveHandler.removeCallbacksAndMessages(null);
        hideScoreHandler.removeCallbacksAndMessages(null);
    }

    public void clearGameSpace(){
        layout.removeAllViews();
        hearts.clear();
    }

    private boolean updateHeartPosition(View heart) {
        heart.setX(heart.getX() - hearthSpeed);
        if (heart.getX() < 0) {
            layout.removeView(heart);
            return false;
        }
        return true;
    }

    private boolean updateShootPosition(View shoot) {
        shoot.setX(shoot.getX() + 10);
        if (shoot.getX() > layout.getMeasuredWidth()) {
            layout.removeView(shoot);
            return false;
        }
        return true;
    }

    private boolean detectHeartCollision(View shoot) {
        int shootLeft = shoot.getLeft() + (int) shoot.getX();
        int shootRight = shoot.getRight() + (int) shoot.getX();
        int shootTop = shoot.getTop() + (int) shoot.getY();
        int shootBottom = shoot.getBottom() + (int) shoot.getY();
        Rect r1 = new Rect(shootLeft, shootTop, shootRight, shootBottom);
        Iterator<Heart> heartsIterator = hearts.iterator();
        while (heartsIterator.hasNext()) {
            Heart heart = heartsIterator.next();
            View heartView = heart.getHeartView();
            int heartLeft = heartView.getLeft() + (int) heartView.getX();
            int heartRight = heartView.getRight() + (int) heartView.getX();
            int heartTop = heartView.getTop() + (int) heartView.getY();
            int heartBottom = heartView.getBottom() + (int) heartView.getY();
            Rect r2 = new Rect(heartLeft, heartTop, heartRight, heartBottom);
            if (Rect.intersects(r1, r2)) {
                layout.removeView(heartView);
                layout.removeView(shoot);
                heartsIterator.remove();
                heart.setWasShot(WasShot.YES);
                setScore(heart.getPoints());
                showScoreInEventLog(heart.getPoints(), heartView.getX(), heartView.getY());
                SoundManager.playSound(getActivity(), R.raw.hit);
                return true;
            }
        }
        return false;
    }

    private void setScore(final int points){
        score += points;
        scoreView.setText("Score: " + score);
        if(score > nextLevelIn){

            if(spawnSeconds > 0.75) {
                spawnSeconds = (spawnSeconds - 0.2);
            }
            nextLevelIn += 50;
        }
        if(score > nextSpeedIn){
            hearthSpeed = (int) (hearthSpeed*Constants.SPEED_MULTIPLIER);
            nextSpeedIn += 100;
        }
    }

    private void showScoreInEventLog(int score, float x, float y){
        final TextView addScore = new TextView(getActivity());
        addScore.setText("+ " + score);
        addScore.setTextSize(20);
        addScore.setPadding(0, 0, 0, 0);
        addScore.setX(x);
        addScore.setY(y);
        layout.addView(addScore);


        Runnable hideScore = new Runnable() {
            @Override
            public void run() {
                addScore.setVisibility(View.GONE);
            }
        };
        hideScoreHandler.postDelayed(hideScore, 1000);
    }

    public View createShoot() {
        final View shoot = new View(activity.getApplicationContext());
        shoot.setBackground(activity.getResources().getDrawable(R.drawable.arrow));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(45, 15);
        shoot.setLayoutParams(params);
        shoot.setX(60);
        shoot.setY(player.getY() + activity.getResources().getDimension(R.dimen.playerHeight) / 2 - 20);
        SoundManager.playSound(getActivity(), R.raw.bowsound);
        return shoot;
    }

    private Heart createHeart() {
        int size = 40 + (int) (Math.random() * 100);
        int points = (int) (200 / size);
        ImageView heartView = new ImageView(activity.getApplicationContext());
        heartView.setBackground(activity.getResources().getDrawable(R.drawable.heart));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(size, size);
        heartView.setLayoutParams(params);
        heartView.setX(layout.getMeasuredWidth());
        heartView.setY(100 + (int) (Math.random() * (layout.getMeasuredHeight() - 200)));
        Heart heart = new Heart(heartView, size, size, points, WasShot.NO);

        return heart;
    }

    public RelativeLayout getLayout() {
        return layout;
    }

    public Activity getActivity() {
        return activity;
    }

    public View getPlayer() {
        return player;
    }

    public void setTopScore(int topScore) {
        this.topScore = topScore;
    }

    public void startMusic(){
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.gametheme);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void toggleMusic(){
        musicMuted = !musicMuted;
        if(musicMuted){
            mediaPlayer.setVolume(0,0);
        }else{
            mediaPlayer.setVolume(1,1);
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}


