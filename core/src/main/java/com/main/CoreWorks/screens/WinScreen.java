package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.RunPersistence.MapNode;
import com.main.CoreWorks.RunPersistence.RunState;

public class WinScreen implements Screen {

    private final Coreworks game;
    private RunState runState;

    public WinScreen(Coreworks game, RunState runState) {

        this.game = game;
        this.runState = runState;
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

        game.font.setColor(Color.GREEN);
        game.font.getData().setScale(4f);

        // Center the text
        GlyphLayout layout = new GlyphLayout(game.font, "YOU WIN!");
        float x = (Coreworks.WORLD_WIDTH - layout.width) / 2;
        float y = (Coreworks.WORLD_HEIGHT - layout.height) / 2;

        game.font.draw(game.batch, layout, x, y);

        game.batch.end();

        // Reset font
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(720f / 480f);

        if (Gdx.input.justTouched()) {
            // If there are no more next nodes, return back to main menu
            // Will definitely be changed in the future when we have proper run end screen for winning / losing the whole game
            if (runState.getCurrNode().getNextNodes().size == 0) {
                game.setScreen(new MenuScreen(game));
                return;
            }
            for (MapNode next : runState.getCurrNode().getNextNodes()) {
                next.setUnlocked(true);
            }
            game.setScreen(new MapScreen(game, runState));
            dispose();
        }
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
