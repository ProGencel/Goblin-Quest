package com.kitswiki.gquest.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class DialogManager {

    private ObjectMap<String, String[]> dialogs;

    public void readJson()
    {
        Json json = new Json();

        dialogs = json.fromJson(ObjectMap.class, String[].class, Gdx.files.internal("data/dialogues.json"));
    }

    public String[] getLines(String id) {
        String[] lines = dialogs.get(id);
        if (lines == null) {
            throw new GdxRuntimeException("Dialog bulunamadı: " + id);
        }
        return lines;
    }

}
