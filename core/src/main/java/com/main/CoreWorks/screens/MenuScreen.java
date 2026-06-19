package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.Generators.RunMapGenerator;
import com.main.CoreWorks.RunPersistence.RunMap;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.database.PlayerDatabase;
import com.main.CoreWorks.entities.Player;

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
        game.font.draw(game.batch, "Welcome to Coreworks!!! ", Coreworks.VIEWPORT_WIDTH /2f - 120, Coreworks.VIEWPORT_HEIGHT /2f + 50);
        game.font.draw(game.batch, "Tap anywhere to begin!", Coreworks.VIEWPORT_WIDTH /2f - 120, Coreworks.VIEWPORT_HEIGHT /2f - 50);
        game.batch.end();

        if (Gdx.input.justTouched()) {
            // Creates the initial runState
            // For now, hardcoded one player type only, eventually might allow selection of different player types with unique abilities for more replayability
            Player player = PlayerDatabase.createEngineer();
            // Hardcoded factory grid as well, will eventually tie grid size to different player types
            FactoryGrid factoryGrid = new FactoryGrid(4, 4);
            RunState runState = new RunState(player, factoryGrid);
            // Generates hardcoded RunMap for testing (Uncomment)
            // RunMap runMap = RunMapGenerator.generateHardcodedRunMap(runState);
            // Generates procedurally generated runMap
            RunMap runMap = RunMapGenerator.generateRandomRunMapF1(runState);
            runState.setRunMap(runMap);
            runState.setCurrNode(runMap.getStartNode());

            // Moves to map screen
            game.resetCamera();
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
    }
}
