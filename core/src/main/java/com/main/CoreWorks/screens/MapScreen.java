package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.RunPersistence.*;

public class MapScreen implements Screen {

    private final Coreworks game;
    private RunState runState;
    private ShapeRenderer shapeRenderer;
    private Stage stage;
    private Skin skin;
    private Texture texture;

    // The below fields are used to clamp the min / max camera movement
    private float mapMinX;
    private float mapMaxX;
    private float mapMinY;
    private float mapMaxY;

    // The below fields are used to clamp min and max zoom
    // Note that zoom < 1 is zooming IN, while zoom > 1 is zooming OUT
    private float minZoom = 0.5f;
    private float maxZoom = 2f;
    private float zoomRate = 0.1f;

    public MapScreen(Coreworks game, RunState runstate) {
        this.game = game;
        this.runState = runstate;
    }

    @Override
    public void show() {
        stage = new Stage(game.viewport, game.batch);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                // MouseWheel up is a negative value on the y-axis, so zooming in is an addition function
                game.camera.zoom += amountY * zoomRate;
                game.camera.zoom = MathUtils.clamp(game.camera.zoom, minZoom, maxZoom);
                clampCamera();
                game.camera.update();
                return true;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);

        // Uses the default libgdx skin, eventually will replace with our own
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        shapeRenderer = new ShapeRenderer();
        texture = createSimpleTexture();

        // Set the boundary of the map
        computeBoundary();

        // Add the Dragging actor
        stage.addActor(panningActor());

        // Build the stage below
        buildMapUI();
        updateCurrentActor();
    }

    // Helper method that calculates furthest possible X and Y coords for camera movement
    private void computeBoundary() {
        float buffer = Math.max(game.viewport.getWorldWidth(), game.viewport.getWorldHeight()) / 2f;

        mapMinX = Float.MAX_VALUE;
        mapMinY = Float.MAX_VALUE;
        mapMaxX = Float.MIN_VALUE;
        mapMaxY = Float.MIN_VALUE;

        for (MapNode node : runState.getRunMap().getNodes()) {
            mapMinX = Math.min(mapMinX, node.getX());
            mapMinY = Math.min(mapMinY, node.getY());
            mapMaxX = Math.max(mapMaxX, node.getX());
            mapMaxY = Math.max(mapMaxY, node.getY());
        }

        mapMinX -= buffer;
        mapMinY -= buffer;
        mapMaxX += buffer;
        mapMaxY += buffer;

    }

    // Helper method that creates a large background actor encompassing the whole map for panning camera
    private Actor panningActor() {
        Actor panner = new Actor();
        panner.setBounds(mapMinX, mapMinY, mapMaxX - mapMinX, mapMaxY - mapMinY);

        panner.addListener(new ActorGestureListener() {
            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                // We want to move the camera opposite of the direction we move the mouse in, so it is more intuitive (used by a lot of touchscreen apps on mobile)
                game.camera.position.add(-deltaX, -deltaY, 0f);

                // Clamp the camera to the bounds in case of exceeding boundaries
                clampCamera();

                // Then update the position
                game.camera.update();
            }
        });
        return panner;
    }

    // Helper method to actually clamp the camera
    private void clampCamera() {
        // Orthographic camera's position coords is taken from the CENTER of the camera rather than corner, unlike basically every other libGDX class
        // So, we need to make sure the camera's leftmost / rightmost / top / bottom boundaries do not exceed the map boundaries computed by the other helper function
        // Finding the camera center position to ensure its 4 boundaries do not exceed the map 4 boundaries is straightforward, just take map boundary - half the camera size to get the center position that the camera must be bound to
        // Need to account for camera zoom as well

        float halfCameraW = (game.viewport.getWorldWidth() * game.camera.zoom) / 2f;
        float halfCameraH = (game.viewport.getWorldHeight() * game.camera.zoom) / 2f;

        float minCameraX = mapMinX + halfCameraW;
        float minCameraY = mapMinY + halfCameraH;
        float maxCameraX = mapMaxX - halfCameraW;
        float maxCameraY = mapMaxY - halfCameraH;

        // Clamp the x position
        if (minCameraX > maxCameraX) {
            // If the map boundary is smaller than the viewport, min will be greater than max, which will cause weird viewport issues
            // So, we just make the camera completely unable to move by forcing the camera back to the center in this case.
            game.camera.position.x = (mapMinX + mapMaxX) / 2f;
        } else {
            game.camera.position.x = MathUtils.clamp(game.camera.position.x, minCameraX, maxCameraX);
        }

        // Clamp the y position
        if (minCameraY > maxCameraY) {
            // If the map boundary is smaller than the viewport, min will be greater than max, which will cause weird viewport issues
            // So, we just make the camera completely unable to move by forcing the camera back to the center in this case.
            game.camera.position.y = (mapMinY + mapMaxY) / 2f;
        } else {
            game.camera.position.y = MathUtils.clamp(game.camera.position.y, minCameraY, maxCameraY);
        }
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
        // The below code builds the table that will serve as the base table for all subsequent UI building in the Stage
        Table table = new Table();
        table.setFillParent(true);

        // Top align the table + add label in the current row
        table.top().pad(30);
        table.setTouchable(Touchable.disabled);
        stage.addActor(table);

        // Draws the map + its nodes based on the position generated in RunMapGenerator
        table.add(new Label("Dungeon Map", skin)).row();
        table.add(new Label("Click a unlocked node to continue (Drag to pan the map, mouseWheel to zoom in/out)", skin)).pad(10).row();
        for (MapNode node : runState.getRunMap().getNodes()) {
            // Relock all nodes that are not adjacent to the current node
            if (node.isUnlocked() && !node.isCompleted() && !(runState.getCurrNode() == node)) {
                if (!runState.getCurrNode().getNextNodes().contains(node, true)) {
                    node.setUnlocked(false);
                }
            }
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
            game.resetCamera();
            game.setScreen(new CombatScreen(game, runState, combatNode.getEnemies()));
            return;
        }

        // Handles RestNode
        if (node instanceof RestNode restNode) {
            runState.setCurrNode(node);
            game.resetCamera();
            game.setScreen(new RestScreen(game, runState));
            return;
        }

        // Handles BossNode
        if (node instanceof BossNode bossNode) {
            runState.setCurrNode(node);
            game.resetCamera();
            game.setScreen(new CombatScreen(game, runState, bossNode.getEnemies()));
            return;
        }

        // Handles EliteNode
        if (node instanceof EliteNode eliteNode) {
            runState.setCurrNode(node);
            game.resetCamera();
            game.setScreen(new CombatScreen(game, runState, eliteNode.getEnemies()));
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

