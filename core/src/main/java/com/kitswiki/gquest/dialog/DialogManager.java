package com.kitswiki.gquest.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class DialogManager {

    public static class DialogEntry
    {
        public String id;
        public boolean once;
        public String[] requires;
        public String[] excludes;
        public String[] lines;
    }

    private ObjectMap<String, DialogEntry[]> dialogs;

    public void readJson()
    {
        Json json = new Json();

        dialogs = json.fromJson(ObjectMap.class, DialogEntry[].class, Gdx.files.internal("data/dialogues.json"));
    }

    public String[] pick(String npcId, GameState state)
    {
        DialogEntry[] entries = dialogs.get(npcId);
        if(entries == null)
        {
            throw new GdxRuntimeException("Dialog cannot find : " + npcId);
        }

        for(DialogEntry e : entries)
        {
            String onceFlag = npcId + "." + e.id;

            if(e.once && state.has(onceFlag) || !hasAll(e.requires, state) || hasAny(e.excludes, state))
            {
                continue;
            }

            if(e.once)
            {
                state.set(onceFlag);
            }

            return e.lines;
        }

        throw new GdxRuntimeException("There is no records with wanted properties");
    }

    private boolean hasAll(String[] flags, GameState state)
    {
        if(flags == null)
        {
            return true;
        }

        for(String f : flags)
        {
            if(!state.has(f))
            {
                return false;
            }
        }

        return true;

    }

    private boolean hasAny(String[] flags, GameState state)
    {
        if(flags == null)
        {
            return false;
        }

        for(String f : flags)
        {
            if(state.has(f))
            {
                return true;
            }
        }

        return false;
    }

}
