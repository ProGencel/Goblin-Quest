package com.kitswiki.gquest.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class DialogBox extends Table {

    private final Label label;
    private String[] lines;
    private int lineIndex;
    private float charTimer;
    private int visibleChars;
    private boolean open;

    private Array<DialogManager.Choice> choices;
    private boolean choosing;
    private int selected;
    private DialogManager.Choice picked;

    private final int MAX_CHOICES_ON_SCREEN = 5;
    private int scrollOffSet;

    public DialogBox(Skin skin) {
        super(skin);

        this.setBackground(skin.getDrawable("dialog"));

        label = new Label("", skin);
        label.setAlignment(Align.topLeft);
        label.setWrap(true);
        this.add(label).expand().fill().pad(10);

        setVisible(false);
    }

    public void start(String[] lines, Array<DialogManager.Choice> choices) {
        this.lines = lines;
        this.choices = choices;
        this.choosing = false;
        this.picked = null;
        this.lineIndex = 0;
        this.visibleChars = 0;
        this.charTimer = 0;
        this.open = true;
        label.setText("");
        setVisible(true);
    }

    public void advance() {
        if (!open || choosing)
        {
            return;
        }

        String currentLine = lines[lineIndex];

        if (visibleChars < currentLine.length()) {
            visibleChars = currentLine.length();
            label.setText(currentLine);
        }
        else {
            lineIndex++;
            if (lineIndex < lines.length) {
                visibleChars = 0;
                charTimer = 0;
                label.setText("");
            }
            else if(choices != null)
            {
                choosing = true;
                selected = 0;
                scrollOffSet = 0;
                refreshChoices();
            }
            else
            {
                close();
            }
        }
    }

    public void moveSelection(int dir)
    {
        if(!choosing)
        {
            return;
        }

        selected = (selected + dir + choices.size) % choices.size;
        refreshChoices();
    }

    private void refreshChoices()
    {

        if(selected < scrollOffSet)
        {
            scrollOffSet = selected;
        }

        else if(selected >= scrollOffSet + MAX_CHOICES_ON_SCREEN)
        {
            scrollOffSet = selected - MAX_CHOICES_ON_SCREEN + 1;
        }

        int end = Math.min(scrollOffSet + MAX_CHOICES_ON_SCREEN, choices.size);

        StringBuilder sb = new StringBuilder();

        for(int i = scrollOffSet;i <end;i++)
        {
            if (i > scrollOffSet)
            {
                sb.append('\n');
            }
            sb.append(i == selected ? "> ":"  ").append(choices.get(i).text);
        }
        label.setText(sb);
    }

    public void confirmChoice()
    {
        picked = choices.get(selected);
        choosing = false;
        close();
    }

    public DialogManager.Choice consumePicked()
    {
        DialogManager.Choice c = picked;
        picked = null;
        return c;
    }

    public void close() {
        this.open = false;
        setVisible(false);
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isChoosing()
    {
        return choosing;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!open || lines == null || lineIndex >= lines.length) return;

        String currentLine = lines[lineIndex];

        if (visibleChars < currentLine.length()) {
            charTimer += delta;
            if (charTimer >= 0.03f) {
                visibleChars++;
                charTimer = 0;
                label.setText(currentLine.substring(0, visibleChars));
            }
        }
    }
}
