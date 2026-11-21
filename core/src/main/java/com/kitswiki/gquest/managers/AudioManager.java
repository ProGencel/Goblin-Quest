package com.kitswiki.gquest.managers;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.kitswiki.gquest.hud.mainMenu.SettingdDialog;

public class AudioManager {

    private float VOLUME;
    private float musicMultiplier = 1f;

    public enum TYPE
    {
        WALK,
        TOWN_THEME,
        DIALOG,
        CAVE,
    }

    public enum OPT
    {
        LOOP,
        ONCE
    }

    private final AssetManager assetManager;
    private Music currentMusic;

    public AudioManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public float readVOLUME()
    {
        return SettingdDialog.prefs.getInteger("vol",50) / 100f;
    }

    public void playSound(TYPE type, OPT opt,float volume)
    {
        Sound sound = assetManager.get(getPath(type), Sound.class);
        switch (opt)
        {
            case LOOP -> sound.loop(volume);
            case ONCE -> sound.play(volume);
        }
    }

    public void checkVolume()
    {
        float currvol = readVOLUME();
        if(VOLUME != currvol)
        {
            VOLUME = currvol;
            currentMusic.setVolume(musicMultiplier * VOLUME);
        }
    }

    public void stopSound(TYPE type)
    {
        assetManager.get(getPath(type),Sound.class).stop();
    }

    public void playWRandPitch(TYPE type)
    {
        float pitch = MathUtils.random(0.9f,1.1f);
        assetManager.get(getPath(type), Sound.class).play(VOLUME,pitch,0f);
    }

    public void playMusic(TYPE type)
    {
        Music next = assetManager.get(getPath(type), Music.class);
        if (currentMusic == next || next.isPlaying()) {
            return;
        }

        if (currentMusic != null) {
            currentMusic.stop();
        }

        currentMusic = next;
        currentMusic.setLooping(true);

        musicMultiplier = (type == TYPE.TOWN_THEME) ? 0.7f : 1f;
        currentMusic.setVolume(musicMultiplier * VOLUME);
        currentMusic.play();
    }

    public void stopMusic()
    {
        if(currentMusic != null)
        {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    private String getPath(TYPE type)
    {
        switch (type)
        {
            case WALK -> {
                return "audio/walk.wav";
            }
            case TOWN_THEME -> {
                return  "audio/TownTheme.mp3";
            }
            case DIALOG -> {
                return "audio/dialog.wav";
            }
            case CAVE -> {
                return  "audio/cave.ogg";
            }
            default -> throw new IllegalArgumentException("cant find audio");
        }
    }
}
