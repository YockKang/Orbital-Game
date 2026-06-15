package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.RunPersistence.CombatNode;
import com.main.CoreWorks.RunPersistence.MapNode;
import com.main.CoreWorks.RunPersistence.MapNodeActor;
import com.main.CoreWorks.RunPersistence.RunState;

public class MapScreen implements Screen {

    private final Coreworks game;
    private RunState runState;
    private ShapeRenderer shapeRenderer;
    private Stage stage;
    private Skin skin;
    private Texture texture;


    public MapScreen(Coreworks game, RunState runstate) {
        this.game = game;
        this.runState = runstate;
    }

    @Override
    public void show() {
        stage = new Stage(game.viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        // Uses the default libgdx skin, eventually will replace with our own
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        shapeRenderer = new ShapeRenderer();
        texture = createSimpleTexture();

        // Build the stage below
        buildMapUI();
        updateCurrentActor();
    }

    private Texture createSimpleTexture() {
        // Create a 1x1 Pixmap and set its color
        // We will be using this as the default texture of all nodes
        // Eventually, when each node has its unique sprite, we should not need this anymore.
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        // Change the pixmap into a texture
        Texture texture1 = new Texture(pixmap);
        pixmap.dispose();

        return texture1;
    }

    private void buildMapUI() {
        stage.clear();

        // The below code builds the table that will serve as the base table for all subsequent UI building in the Stage
        Table table = new Table();
        table.setFillParent(true);

        // Top align the table + add label in the current row
        table.top().pad(30);
        stage.addActor(table);

        // Draws the map + its nodes based on the position generated in RunMapGenerator
        table.add(new Label("Dungeon Map", skin)).row();
        table.add(new Label("Click a unlocked node to continue", skin)).pad(10).row();
        for (MapNode node : runState.getRunMap().getNodes()) {
            // For now, all MapNodes have the exact same size, texture and skin, but eventually when we add more node types, their sizes / sprites will be more unique
            // which will be changed here by changing the input params in MapNodeActor
            MapNodeActor nodeActor = new MapNodeActor(node, texture, skin, 70, 70);
            nodeActor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleNodeInput(node);
                }
            });
            stage.addActor(nodeActor);
        }
    }

    private void handleNodeInput(MapNode node) {
        // If not unlocked or already completed, do nothing
        if (!node.isUnlocked() || node.isCompleted()) {
            return;
        }

        // Add all the different types of nodes and what to do in them below

        // Handles combatNode
        if (node instanceof CombatNode combatNode) {
            runState.setCurrNode(node);
            game.setScreen(new CombatScreen(game, runState, combatNode.getEnemies()));
            return;
        }
    }

    // Need to update the current MapNodeActor based on runState's current node so it will be highlighted correctly in a different color
    private void updateCurrentActor() {
        for (Actor actor : stage.getActors()) {
            if (actor instanceof MapNodeActor mapNodeActor) {
                mapNodeActor.setCurr(mapNodeActor.getNode() == runState.getCurrNode());
            }
        }
    }

    @Override
    public void render(float delta) {
        // Clears the screen + update camera if needed
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.camera.update();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);

        // All drawing functions below
        drawConnection();

        stage.act(delta);
        stage.draw();
    }

    private void drawConnection() {
        // Draws the line that connects between nodes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.LIGHT_GRAY);

        for (MapNode node : runState.getRunMap().getNodes()) {
            for (MapNode next : node.getNextNodes()) {
                shapeRenderer.line(node.getX(), node.getY(), next.getX(), next.getY());
            }
        }

        shapeRenderer.end();
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
        shapeRenderer.dispose();
        stage.dispose();
        skin.dispose();
        texture.dispose();
    }
}

