package cz.matyapav.valentinesday;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import java.util.HashMap;

/**
 * @author Pavel Matyáš (matyapav@fel.cvut.cz).
 * @since 1.0.0..
 */
public class SoundManager {


    public static final int S1 = R.raw.oh;
    public static final int S2 = R.raw.bowsound;
    public static final int S3 = R.raw.hit;

    private static SoundPool soundPool;
    private static HashMap<Integer,Integer> soundPoolMap;
    private static boolean muted = false;


    private static void initSounds(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<>();
        soundPoolMap.put( S1, soundPool.load(context, R.raw.oh, 1));
        soundPoolMap.put( S2, soundPool.load(context, R.raw.bowsound, 2) );
        soundPoolMap.put(S3, soundPool.load(context, R.raw.hit, 3));
    }

    public static void playSound(Context context, int soundID) {
        if(!muted) {
            if (soundPool == null || soundPoolMap == null) {
                initSounds(context);
            }
            float volume = 1;
            soundPool.play(soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
        }
    }

    public static void toggleSounds(){
        muted = !muted;
    }


}
