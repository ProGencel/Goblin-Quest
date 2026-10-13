package com.kitswiki.gquest.dialog;

import com.badlogic.gdx.utils.ObjectSet;
import com.kitswiki.gquest.datas.SavedData;

public class GameState {

    private final ObjectSet<String> flags = new ObjectSet<>();

    public boolean has(String flag)
    {
        return flags.contains(flag);
    }

    public void set(String flag)
    {
        flags.add(flag);
    }

    public void writeTo(SavedData data)
    {
        data.flags.clear();
        for(String f : flags)
        {
            data.flags.add(f);
        }
    }

    public void readFrom(SavedData data)
    {
        flags.clear();
        for(String f : data.flags)
        {
            flags.add(f);
        }
    }

}
