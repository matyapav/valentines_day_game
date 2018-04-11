package cz.matyapav.valentinesday;

import android.app.Activity;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * @author Pavel Matyáš (matyapav@fel.cvut.cz).
 * @since 1.0.0..
 */
public class Controls {

    private Button buttonUp;
    private Button buttonDown;
    private ImageButton buttonShoot;
    private CheckBox buttonSound;
    private Game game;

    public Controls( Button buttonUp, Button buttonDown, ImageButton buttonShoot, CheckBox buttonSound, Game game) {
        this.buttonDown = buttonDown;
        this.buttonShoot = buttonShoot;
        this.buttonUp = buttonUp;
        this.buttonSound = buttonSound;
        this.game = game;
    }

    public void init(){
        buttonUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                movePlayerUp();
                return false;
            }
        });
        buttonDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                movePlayerDown();
                return false;
            }
        });
        buttonShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoot();
            }
        });
        buttonSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSound();
            }
        });
    }

    private void toggleSound() {
        game.toggleMusic();
        SoundManager.toggleSounds();
    }

    private void movePlayerUp(){
        View player = game.getPlayer();
        if(player.getY()-Constants.MOVING_SPEED > 0) {
            player.setY(player.getY() - Constants.MOVING_SPEED);
        }
    }

    private void movePlayerDown(){
        View player = game.getPlayer();
        Activity activity = game.getActivity();
        RelativeLayout layout = game.getLayout();
        float maxHeight = layout.getMeasuredHeight() - activity.getResources().getDimension(R.dimen.playerHeight);
        if (player.getY() + Constants.MOVING_SPEED < maxHeight) {
            player.setY(player.getY() + Constants.MOVING_SPEED);
        }
    }

    private void shoot(){
        final View shoot = game.createShoot();
        RelativeLayout layout = game.getLayout();
        layout.addView(shoot);
        game.registerShootMoveHandler(shoot);
    }
    
    
}
