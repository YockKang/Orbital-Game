package com.main.CoreWorks.RunPersistence;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/*
The color of the different nodes are drawn here
 */

public class MapNodeActor extends Actor {
    protected MapNode node;
    protected Texture texture;
    protected Skin skin;

    private boolean curr = false;

    public MapNodeActor(MapNode node, Texture texture, Skin skin, float width, float height) {
        this.node = node;
        this.skin = skin;
        this.texture = texture;

        // Sets the size of the node
        setSize(width, height);

        // Center the node around the (x,y) coords
        setPosition(node.getX() - getWidth() / 2f, node.getY() - getHeight() / 2f);
    }

    public MapNode getNode() {
        return node;
    }

    public void setCurr(boolean curr) {
        this.curr = curr;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getNodeColor());
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());

        // Reset batch color for text
        batch.setColor(Color.WHITE);

        // Draws the text inside the node (Uses LibGDX bitmapFont stored in default font file name, but can create our own bitmapFont if desired)
        // Then center the text based on its length and height
        BitmapFont font = skin.getFont("default-font");
        GlyphLayout layout = new GlyphLayout(font, node.getName());
        float textX = getX() + (getWidth() - layout.width) / 2f;
        float textY = getY() + (getHeight() + layout.height) / 2f;
        font.draw(batch, layout, textX, textY);
    }

    private Color getNodeColor() {
        // This draws the color of non-adjacent nodes to current node
        if (!node.isUnlocked()) {
            return Color.GRAY;
        }

        // This draws the color of the current node
        if (curr) {
            return Color.GOLD;
        }

        // This draws color of completed nodes
        if (node.isCompleted()) {
            return Color.CYAN;
        }

        // Sets the colors of the different node types below
        if (node instanceof CombatNode) {
            return Color.ORANGE;
        }

        if (node instanceof RestNode) {
            return Color.GREEN;
        }

        if (node instanceof BossNode) {
            return Color.RED;
        }

        // Default color for everything else not specifically included
        return Color.MAGENTA;
    }
}
