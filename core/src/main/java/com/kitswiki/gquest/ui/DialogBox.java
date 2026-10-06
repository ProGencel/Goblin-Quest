package com.kitswiki.gquest.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class DialogBox extends Table {
    private Label label;
    private String[] lines;
    private int lineIndex;
    private float charTimer;
    private int visibleChars;
    private boolean open;

    public DialogBox(Skin skin) {
        super(skin);

        this.setBackground(skin.getDrawable("dialog"));

        label = new Label("", skin);
        label.setWrap(true);
        this.add(label).expand().fill().pad(10);

        setVisible(false);
    }

    public void start(String[] lines) {
        this.lines = lines;
        this.lineIndex = 0;
        this.visibleChars = 0;
        this.charTimer = 0;
        this.open = true;
        label.setText("");
        setVisible(true);
    }

    public void advance() {
        if (!open) return;

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
            } else {
                close();
            }
        }
    }

    public void close() {
        this.open = false;
        setVisible(false);
    }

    public boolean isOpen() {
        return open;
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
