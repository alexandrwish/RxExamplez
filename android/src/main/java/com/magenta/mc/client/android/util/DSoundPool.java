package com.magenta.mc.client.android.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Vibrator;
import android.util.SparseIntArray;

import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.sound.DSound;

import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

public class DSoundPool extends SoundPool {

    public static final long VIBRATION_TIME = 1500;
    private static DSoundPool instance;
    private final Set<String> bips = new HashSet<>();
    private SparseIntArray soundPoolMap;
    private AudioManager audioManager;
    private Vibrator vibrator;

    private DSoundPool() {
        super(4, AudioManager.STREAM_RING, 100);
    }

    public static DSoundPool getInstance() {
        return instance;
    }

    public static void init(Context context) {
        instance = new DSoundPool();
        instance.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        instance.soundPoolMap = new SparseIntArray();
        //Put your sounds here, from raw resources
        instance.soundPoolMap.put(DSound.SOUND_TADA.getNum(), instance.load(context, R.raw.tada, 1));
        instance.soundPoolMap.put(DSound.SOUND_SUCCESS.getNum(), instance.load(context, R.raw.success, 2));
        instance.soundPoolMap.put(DSound.SOUND_ERROR.getNum(), instance.load(context, R.raw.error, 3));
        // Get instance of Vibrator from current Context
        instance.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void playSound(int sound, boolean vibration) {
        if (!Settings.get().getAudioAlert()) return;
        final int streamVolumeCurrent = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        final int streamVolumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        /* Volume calculations */
        float volume = ((float) streamVolumeCurrent / (float) streamVolumeMax);
        int systemRingerMode = audioManager.getRingerMode();
        boolean changeChanel = systemRingerMode == AudioManager.RINGER_MODE_SILENT || systemRingerMode == AudioManager.RINGER_MODE_VIBRATE;
        if (changeChanel) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, streamVolumeMax, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_ALLOW_RINGER_MODES);
            setVolume(AudioManager.STREAM_RING, 1.0F, 1.0F);
        }
        /* Play the sound with the correct volume */
        play(soundPoolMap.get(sound), 1.0F, 1.0F, 1, 1, 1f);
        if (vibration) {
            vibrator.vibrate(VIBRATION_TIME);
        }
        if (changeChanel) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, streamVolumeCurrent, AudioManager.FLAG_PLAY_SOUND);
                }
            }, VIBRATION_TIME);
        }
        MCLoggerFactory.getLogger(getClass()).debug(String.format("Play sound: %s with volume = %s %s.", DSound.getNameById(sound), String.valueOf(volume), vibration ? "with vibration" : "without vibration"));
    }

    //Default sound TADA
    public void playSound() {
        playSound(0, false);
    }

    //Default sound TADA with vibration
    public void playSoundWithVibration() {
        playSound(0, true);
    }

    public void startBip(int count, boolean vibration, int delay, String bipName) {
        MCLoggerFactory.getLogger().info(String.format("Start bip [%s] with count = %s.", bipName, String.valueOf(count)));
        synchronized (bips) {
            bips.add(bipName);
        }
        bip(count - 1, vibration, delay, bipName);
    }

    public void bip(final int count, final boolean vibration, final int delay, final String bipName) {
        playSound(0, vibration);
        if (count > 0 && canContinueBip(bipName)) {
            MobileApp.getInstance().getTimer().schedule(new TimerTask() {
                public void run() {
                    bip(count - 1, vibration, delay, bipName);
                }
            }, delay * 1000);
        } else if (count == 0) {
            synchronized (bips) {
                bips.remove(bipName);
            }
            MCLoggerFactory.getLogger().info(String.format("Stop bip [%s].", bipName));
        }
    }

    private boolean canContinueBip(String bipName) {
        if (StringUtils.isBlank(bipName)) return true;
        synchronized (bips) {
            return bips.contains(bipName);
        }
    }

    public void stopBip(String bipName) {
        synchronized (bips) {
            bips.remove(bipName);
        }
        MCLoggerFactory.getLogger().info(String.format("Stop bip [%s] by user.", bipName));
    }
}