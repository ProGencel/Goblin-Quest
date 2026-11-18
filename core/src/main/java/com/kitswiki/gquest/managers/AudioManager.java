package com.kitswiki.gquest.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

public class AudioManager {

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
    private String currentPath;
    private Music currentMusic;

    public AudioManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void playSound(TYPE type, OPT opt,float volume)
    {
        selectPath(type);
        Sound sound = assetManager.get(currentPath, Sound.class);
        switch (opt)
        {
            case LOOP -> sound.loop(volume);
            case ONCE -> sound.play(volume);
        }
    }

    public void stopSound()
    {
        assetManager.get(currentPath,Sound.class).stop();
    }

    public void playWRandPitch(TYPE type)
    {
        selectPath(type);
        float pitch = MathUtils.random(0.9f,1.1f);
        assetManager.get(currentPath, Sound.class).play(1,pitch,0f);
    }

    public void playMusic(TYPE type)
    {
        selectPath(type);
        if(currentMusic == assetManager.get(currentPath,Music.class))
        {
            return;
        }
        currentMusic = assetManager.get(currentPath,Music.class);
        currentMusic.setLooping(true);
        if(type.equals(TYPE.TOWN_THEME))
        {
            currentMusic.setVolume(0.7f);
        }
        else if(type.equals(TYPE.CAVE))
        {
            currentMusic.setVolume(1);
        }
        currentMusic.play();
    }

    public void stopMusic()
    {
        if(currentMusic != null)
        {
            currentMusic.stop();
            currentMusic = null;
            currentPath = null;
        }
    }

    private void selectPath(TYPE type)
    {
        switch (type)
        {
            case WALK -> currentPath = "audio/walk.wav";
            case TOWN_THEME -> currentPath = "audio/TownTheme.mp3";
            case DIALOG -> currentPath = "audio/dialog.wav";
            case CAVE -> currentPath = "audio/cave.ogg";
            default -> throw new IllegalArgumentException("cant find audio");
        }
    }

}
