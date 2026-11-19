package com.kitswiki.gquest.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;
import com.kitswiki.gquest.components.InventoryComponent;

import java.util.Locale;

public class DialogManager {

    private final InventoryComponent i;
    private ObjectMap<String, DialogEntry[]> dialogs;

    public static class DialogEntry
    {
        public String id;
        public boolean once;
        public String[] requires;
        public String[] excludes;
        public String[] lines;
        public Choice[] choices;
        public String[] give;
        public boolean linkOnly;
        public String reqObj;
        public String action;
    }

    public static class Choice
    {
        public String text;
        public String[] requires;
        public String[] excludes;
        public String[] set;
        public String next;
        public String shop;
    }

    public DialogManager(InventoryComponent i) {
        this.i = i;
    }

    public void readJson(I18NBundle bundle)
    {
        Json json = new Json();
        if(dialogs != null)
        {
            dialogs = null;
        }
        dialogs = json.fromJson(ObjectMap.class, DialogEntry[].class, Gdx.files.internal("data/dialogues.json"));

        try {
            resolveTexts(bundle);
        }catch (Exception e)
        {
            throw new NullPointerException("CHECK GAME SESSION 'lang' variable!!!");
        }
    }

    public DialogEntry pick(String npcId, GameState state)
    {
        DialogEntry[] entries = dialogs.get(npcId);
        if(entries == null)
        {
            throw new GdxRuntimeException("Dialog cannot find : " + npcId);
        }
        for(DialogEntry e : entries)
        {
            String onceFlag = npcId + "." + e.id;

            if(e.linkOnly || e.once && state.has(onceFlag) || !hasAll(e.requires, state) || hasAny(e.excludes, state))
            {
                continue;
            }
            if(e.reqObj != null && !checkInv(e))
            {
                continue;
            }

            if(e.once)
            {
                state.set(onceFlag);
            }

            applyGive(e);
            return e;
        }

        throw new GdxRuntimeException("There is no records with wanted properties");
    }

    public DialogEntry goTo(String npcId, String entryId)
    {
        DialogEntry[] entries = dialogs.get(npcId);
        if(entries == null)
        {
            throw new GdxRuntimeException("Dialog cannot find : "+npcId);
        }

        for(DialogEntry e : entries)
        {
            if(entryId.equals(e.id))
            {
                applyGive(e);
                return e;
            }
        }

        throw new GdxRuntimeException("Dialog entry cannot find : "+npcId+"."+entryId);
    }

    public Array<Choice> visibleChoices(DialogEntry entry, GameState gameState)
    {
        if(entry.choices == null)
        {
            return null;
        }
        Array<Choice> results = new Array<>();
        for(Choice c : entry.choices)
        {
            if(hasAll(c.requires, gameState) && !hasAny(c.excludes, gameState))
            {
                results.add(c);
            }
        }

        return results.size > 0 ? results : null;
    }

    private boolean checkInv(DialogEntry entry)
    {
        if(i.inventory.contains(entry.reqObj, false))
        {
            return true;
        }
        return false;
    }

    private void applyGive(DialogEntry entry)
    {
        if(entry.give == null)
        {
            return;
        }
        for(String s : entry.give)
        {
            String[] ss = s.split(":");
            switch (ss[0])
            {
                case "gold" -> i.gold += Integer.parseInt(ss[1]);
                default -> throw new GdxRuntimeException("Unknown type :" + s);
            }
        }
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

    private void resolveTexts(I18NBundle bundle) {
        for (DialogEntry[] entries : dialogs.values()) {
            for (DialogEntry e : entries) {
                if (e.lines != null) {
                    for (int n = 0; n < e.lines.length; n++) {
                        e.lines[n] = bundle.get(e.lines[n]);
                    }
                }
                if (e.choices != null) {
                    for (Choice c : e.choices) {
                        c.text = bundle.get(c.text);
                    }
                }
            }
        }
    }

}
