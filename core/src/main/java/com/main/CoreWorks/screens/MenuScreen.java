package com.main.CoreWorks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.main.CoreWorks.Coreworks;

public class MenuScreen implements Screen {

    final Coreworks game;

    public MenuScreen(Coreworks game) {
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
        //draw text. Remember that x and y are in meters
        game.font.draw(game.batch, "Welcome to Coreworks!!! ", Coreworks.WORLD_WIDTH/2f - 120, Coreworks.WORLD_HEIGHT/2f + 50);
        game.font.draw(game.batch, "Tap anywhere to begin!", Coreworks.WORLD_WIDTH/2f - 120, Coreworks.WORLD_HEIGHT/2f - 50);
        game.batch.end();

        if (Gdx.input.justTouched()) {
            game.setScreen(new CombatScreen(game));
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
    }
}
