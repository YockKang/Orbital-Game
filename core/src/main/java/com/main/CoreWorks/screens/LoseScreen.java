package com.main.CoreWorks.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.main.CoreWorks.Coreworks;

public class LoseScreen implements Screen {

    private final Coreworks game;

    public LoseScreen(Coreworks game) {
        this.game = game;
    }

    @Override
    public void show() {
        // TBD
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        game.font.setColor(Color.RED);
        game.font.getData().setScale(4f);

        // Center the text
        GlyphLayout layout = new GlyphLayout(game.font, "YOU LOST!");
        float x = (Coreworks.WORLD_WIDTH - layout.width) / 2;
        float y = (Coreworks.WORLD_HEIGHT - layout.height) / 2;

        game.font.draw(game.batch, layout, x, y);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // TBD
    }

    @Override
    public void resume() {
        // TBD
    }

    @Override
    public void hide() {
        // TBD
    }

    @Override
    public void dispose() {
        // TBD
    }
}
