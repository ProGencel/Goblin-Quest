package com.kitswiki.gquest.dialog;

import com.badlogic.gdx.utils.ObjectSet;

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

}
