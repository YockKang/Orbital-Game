package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Factory.*;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.entities.*;
import com.main.CoreWorks.simulators.*;

public class CombatScreen implements Screen {

    Coreworks game;
    RunState runState;
    CombatController controller;
    private float accumulator = 0f;
    private static final float TIME_STEP = 1/4f; // 4 Ticks per second
    private int tickCount = 0;
    private Vector2 mouse2DCoords = new Vector2();
    private ShapeRenderer shapeRenderer;
    private Coords hoveredGridCoords = null;
    private boolean hoveredCanPlace = false;
    private boolean isPaused = true;

    // Below field handles the scene2D UI
    private Stage stage;
    private Skin skin;
    private boolean needRefresh = true;


    // Hardcoded grid size for milestone 1 testing purposes
    // Should be deleted eventually since it should be handled by the global runState which carries over the factory
    private final int gridWidth = 4;
    private final int gridHeight = 4;

    // Temp Layout since we have not decided how we want the final UI to look like yet
    // Rmb that everything is drawn in a coordinate system (check Coreworks class for the public static final screen size)
    private final int gridSize = 400;

    private final int tileSize = Math.min(gridSize / gridWidth, gridSize / gridHeight);

    private final int gridMidX = (int) (Coreworks.VIEWPORT_WIDTH / 2);
    private final int gridMidY = 400;

    private final int gridStartX = gridMidX - tileSize * gridWidth / 2 ;
    private final int gridEndX = gridMidX - tileSize * gridWidth / 2;
    private final int gridEndY = gridMidY + tileSize * gridHeight / 2;
    private final int gridStartY = gridMidY - tileSize * gridHeight / 2;

    private final int inventoryStartX = 512;
    private final int inventoryStartY = 40;
    // Each inventory slot is a square for now
    private final int inventorySlotSize = 96;
    private final int inventorySlotGap = 16;

    private Building selectedBuilding;

    public CombatScreen(Coreworks game, RunState runstate, Array<Enemy> enemies) {

        this.game = game;
        this.runState = runstate;
        FactorySim factorySim = new FactorySim(runstate.getFactoryGrid());
        CombatSim combatSim = new CombatSim(runstate.getPlayer(), enemies);
        this.controller = new CombatController(factorySim, combatSim);
    }

    @Override
    public void show() {
        stage = new Stage(game.viewport, game.batch);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        game.viewport.apply();
        game.camera.update();
        shapeRenderer = new ShapeRenderer();

        // The below builds the scene2D UI overlay for everything but the grid and its related functions
        buildCombatUI();
    }

    public void buildCombatUI() {
        stage.clear();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.top();

        // The below builds the top part of the UI + pause screen
        Table topTable = new Table();
        topTable.top().left().pad(10);
        topTable.add(new Label("Coreworks", skin)).pad(20);
        topTable.add(new Label("Ticks: " + tickCount, skin)).pad(20);
        if (selectedBuilding == null) {
            topTable.add(new Label("Selected: None ", skin)).pad(20);
        } else {
            topTable.add(new Label("Selected: " + selectedBuilding.displayName(), skin)).pad(20);
            topTable.add(new Label("Press R to rotate \n Current rotation: " + selectedBuilding.getRotation(), skin)).pad(20);
        }
        if (isPaused) {
            topTable.add(new Label("PAUSED \n Press Space to Continue", skin)).pad(20);
        }
        table.add(topTable).colspan(3).expandX().row();

        // Create a middle table to handle other parts of the HUD
        Table middleTable = new Table();

        // The below builds the combat Log
        Table logTable = new Table();
        logTable.top().center();
        logTable.add(new Label("Combat Log:", skin)).row();
        Array<String> log = controller.getCombatSim().getCombatLog();
        int start = Math.max(0, log.size - 8); // displays the 8 most recent interactions
        for (int i = start; i < log.size; i++) {
            logTable.add(new Label(log.get(i), skin)).right().row();
        }
        middleTable.add(logTable).expand().right().top().row();

        // The below builds the enemy display
        Table enemyTable = new Table();
        enemyTable.top().center().pad(10);
        enemyTable.defaults().width(200);
        enemyTable.add(new Label("Enemies:", skin)).row();
        Array<Enemy> enemies = controller.getCombatSim().getEnemies();
        int enemyCount = 0;
        int maxEnemyPerRow = 2;
        for (Enemy enemy : enemies) {
            // Draw the enemy in a table (disguised as a card) to look neater
            Table enemyCard = new Table(skin);
            enemyCard.setBackground("default-round");
            enemyCard.defaults().pad(5);
            enemyCard.add(new Label(enemy.displayName(), skin)).row();
            enemyCard.add(new Label(String.format("HP: %s/%s", enemy.displayCurrentHp(), enemy.displayMaxHp()), skin)).row();
            enemyCard.add(new Label("Shield: " + enemy.displayShield(), skin)).row();
            enemyCard.add(new Label(String.format("Next move in %s ticks", enemy.getMoveTimer()), skin)).row();
            enemyTable.add(enemyCard).pad(5);
            enemyCount++;
            if (enemyCount % maxEnemyPerRow == 0) {
                enemyTable.row();
            }
        }
        middleTable.add(enemyTable).right().top();

        // The below builds the inventoryTable
        Table inventoryTable = new Table();
        inventoryTable.left();
        inventoryTable.add(new Label("Inventory", skin)).row();
        Table buildingsInInv = new Table();
        buildingsInInv.defaults().width(130).height(65).pad(5);
        Array<Building> inventory = controller.getCombatSim().getPlayer().getInventory();
        int maxBuildingsPerRow = 3;
        int buildingCount = 0;

        for (Building building : inventory) {
            TextButton buildingButton = new TextButton(building.displayName(), skin);
            if (building == selectedBuilding) {
                buildingButton.setColor(Color.GREEN);
            }
            buildingButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedBuilding = building;
                    needRefresh = true;
                }
            });
            buildingsInInv.add(buildingButton);
            buildingCount++;
            if (buildingCount % maxBuildingsPerRow == 0) {
                buildingsInInv.row();
            }
        }
        inventoryTable.add(buildingsInInv);

        table.add(inventoryTable).bottom().right();

        table.add(middleTable).expand().fill().row();

        needRefresh = false;
    }

    @Override
    public void render(float delta) {
        // Anti-"Lag spike spiral of death" code
        delta = Math.min(delta, 1/8f);

        externalInput();

        // Tick Advancement code below
        if (!isPaused && !controller.isWin() && !controller.isLost()) {
            accumulator += delta;
            while (accumulator >= TIME_STEP) {
                System.out.println();
                System.out.println("Tick " + tickCount);
                controller.advanceTick(tickCount);
                tickCount += 1;
                accumulator -= TIME_STEP;
                needRefresh = true;
            }
        }

        // Clears the screen + update camera if needed
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.camera.update();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);

        // Drawing functions below
        drawGrid();
        drawPlacementPreview();
        drawBuildings();
        drawIOPorts();

        // Draws the Scene2D UI
        if (needRefresh) {
            buildCombatUI();
        }
        stage.act(delta);
        stage.draw();

        // Handles win/loss screen transitions
        checkWinLoss();
    }

    /*
    All drawing related functions should be handled from here on
     */

    public void drawPlacementPreview() {
        if (selectedBuilding == null || hoveredGridCoords == null || selectedBuilding.isOnGrid()) {
            return;
        }

        boolean[][] rotatedShape = selectedBuilding.getProjectedShape();

        // Set Green if valid, Red if not valid
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (hoveredCanPlace) {
            shapeRenderer.setColor(Color.GREEN);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        // Draws the preview
        for (int y = 0; y < rotatedShape.length; y++) {
            for (int x = 0; x < rotatedShape[y].length; x++) {
                if (!rotatedShape[y][x]) { // If not filled, do not draw anything
                    continue;
                }

                // Get the base x,y coord in the grid 2D shape array for drawing
                int gridX = hoveredGridCoords.x + x;
                int gridY = hoveredGridCoords.y + y;

                // For now, no drawing of any parts of the building outside of grid
                // When we have sprites maybe can change this part
                if (gridX < 0 || gridY < 0 || gridX >= gridWidth || gridY >= gridHeight) {
                    continue;
                }

                float tileX = gridStartX + gridX * tileSize;
                float tileY = gridEndY - (gridY + 1) * tileSize;

                shapeRenderer.rect(tileX, tileY, tileSize, tileSize);
            }
        }

        shapeRenderer.end();
    }

    public void drawIOPorts() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.CYAN);

        for (Building building : controller.getFactorySim().getGrid().getBuildings()) {
            // Code here handles the IOPort drawing, Line for now until we get proper sprites
            for (IOPort port : building.getPorts()) {
                int[] globalPortCoords = building.getPortGlobalCoords(port);
                int portDir = building.getPortGlobalDirection(port);

                float drawX = gridStartX + globalPortCoords[0] * tileSize + tileSize / 2f;
                float drawY = gridEndY - globalPortCoords[1] * tileSize - tileSize / 2f;

                drawCardinalArrow(drawX, drawY, portDir, 20, 4);
            }
        }

        shapeRenderer.end();
    }

    public void drawCardinalArrow(float x, float y, int direction, float length, float arrowSize) {
        float endX = x;
        float endY = y;

        // Draw the arrow line
        switch (direction) {
            case 0:
                endY += length;
                break;
            case 1:
                endX += length;
                break;
            case 2:
                endY -= length;
                break;
            case 3:
                endX -= length;
                break;
        }

        shapeRenderer.rectLine(x, y, endX, endY, 1);

        // Draw the arrowhead
        switch (direction) {
            case 0:
                shapeRenderer.triangle(endX, endY, endX - arrowSize, endY - arrowSize, endX + arrowSize, endY - arrowSize);
                break;
            case 1:
                shapeRenderer.triangle(endX, endY, endX - arrowSize, endY - arrowSize, endX - arrowSize, endY + arrowSize);
                break;
            case 2:
                shapeRenderer.triangle(endX, endY, endX - arrowSize, endY + arrowSize, endX + arrowSize, endY + arrowSize);
                break;
            case 3:
                shapeRenderer.triangle(endX, endY, endX + arrowSize, endY - arrowSize, endX + arrowSize, endY + arrowSize);
                break;
        }
    }

    public void drawGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draws the Outline of occupied grids
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                int bottomLeftCorner = gridStartX + x * tileSize;
                int topLeftCorner = gridEndY - (y + 1) * tileSize; // offset by one since libGDX stores its object origins in the bottom left
                Building occupied = controller.getFactorySim().getGrid().getBuildingAt(x, y);

                // If there is a non-disabled building, draw it as blue
                if (occupied != null && occupied.isEnabled()) {
                    shapeRenderer.setColor(Color.BLUE);
                    shapeRenderer.rect(bottomLeftCorner, topLeftCorner, tileSize, tileSize);
                }

                // If there is a disabled building, draw it as yellow
                if (occupied != null && !occupied.isEnabled()) {
                    shapeRenderer.setColor(Color.YELLOW);
                    shapeRenderer.rect(bottomLeftCorner, topLeftCorner, tileSize, tileSize);
                }
            }
        }

        shapeRenderer.end();

        // Draws the outline of unoccupied grids
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.WHITE);

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                int bottomLeftCorner = gridStartX + x * tileSize;
                int topLeftCorner = gridEndY - (y + 1) * tileSize; // offset by one since libGDX shape draws the object origins in the bottom left, but we start from top left
                shapeRenderer.rect(bottomLeftCorner, topLeftCorner, tileSize, tileSize);
            }
        }

        shapeRenderer.end();
    }

    public void drawBuildings() {
        game.batch.begin();

        for (Building building : controller.getFactorySim().getGrid().getBuildings()) {
            int[] coords = building.getGlobalCoord(0, 0);
            float nameX = gridStartX + coords[0] * tileSize + 15;
            float nameY = gridEndY - coords[1] * tileSize - 35;
            game.font.draw(game.batch, building.displayName(), nameX, nameY);
        }

        game.batch.end();
    }

    public void checkWinLoss() {
        // Below draws the screen transitions
        if (controller.isWin()) {
            controller.getFactorySim().clear();
            game.setScreen(new WinScreen(game, runState));
            return;
        } else if (controller.isLost()) {
            controller.getFactorySim().clear();
            game.setScreen(new LoseScreen(game));
            return;
        }
    }

    /*
    All mouse inputs should be handled here
     */

    private void externalInput() {
        // Keyboard inputs handled below

        // Press R to rotate building
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (selectedBuilding == null || selectedBuilding.isOnGrid()) {
                return;
            }
            int nextRotation = (selectedBuilding.getRotation() + 1) % 4;
            selectedBuilding.setRotation(nextRotation);
        }

        // Pause will be tied to Spacebar
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            isPaused = !isPaused;
            needRefresh = true;
        }

        // Mouse inputs handled below
        Vector2 mouseTranslatedCoords = translateMouseToWorld();

        float mouseTranslatedX = mouseTranslatedCoords.x;
        float mouseTranslatedY = mouseTranslatedCoords.y;

        // Handles potential bugs with scene2D UI and grid inputs by prioritizing scene2D UI if there is an overlap
        if (stage.hit(mouseTranslatedX, mouseTranslatedY, true) != null) {
            return;
        }

        // Handles Placement preview via mouse hovering
        hoveredGridCoords = getGridAt(mouseTranslatedX, mouseTranslatedY);
        if (selectedBuilding != null && hoveredGridCoords != null) {
            hoveredGridCoords = getGridAt(
                mouseTranslatedX - (float) (selectedBuilding.getProjectedShape()[0].length * tileSize) / 2 + tileSize/2,
                mouseTranslatedY + (float) (selectedBuilding.getProjectedShape().length * tileSize) / 2 - tileSize/2);
            if (hoveredGridCoords != null) {
                hoveredCanPlace = controller.getFactorySim().getGrid().checkValidPosition(selectedBuilding, hoveredGridCoords.x, hoveredGridCoords.y, selectedBuilding.getRotation());
            } else {
                hoveredCanPlace = false;
            }
        } else {
            hoveredCanPlace = false;
        }

        // Handles left and right clicks
        if (Gdx.input.justTouched()) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                leftClick(mouseTranslatedX, mouseTranslatedY);
            }

            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                rightClick(mouseTranslatedX, mouseTranslatedY);
            }
        }
    }

    // Translates mouse coordinates to world coordinates
    private Vector2 translateMouseToWorld() {
        mouse2DCoords.set(Gdx.input.getX(), Gdx.input.getY());
        game.viewport.unproject(mouse2DCoords);
        return mouse2DCoords;
    }

    /*
    Left clicks will handle (c.a.a Milestone 1)
        1. Selecting a building from inventory
        2. Placing it on the grid if grid is clicked + a building is selected in inventory
     */

    private void leftClick(float mouseTranslatedX, float mouseTranslatedY) {
        if (hoveredGridCoords != null && selectedBuilding != null && !selectedBuilding.isOnGrid()) {
            boolean successfulPlacement = controller.getFactorySim().getGrid().placeBuilding(selectedBuilding, hoveredGridCoords.x, hoveredGridCoords.y, selectedBuilding.getRotation());
            if (successfulPlacement) {
                controller.getCombatSim().getPlayer().removeBuilding(selectedBuilding);
                needRefresh = true;
                selectedBuilding = null;
                return;
            }
        }
        hoveredGridCoords = getGridAt(mouseTranslatedX, mouseTranslatedY);
        if (hoveredGridCoords != null) {
            selectedBuilding = controller.getFactorySim().getGrid().getBuildingAt(hoveredGridCoords.x, hoveredGridCoords.y);
            needRefresh = true;
        }
    }

    /*
    Right clicks will handle (c.a.a Milestone 1)
        1. Deselecting a building
        2. Removing building from the grid if grid is clicked back into inventory
     */

    private void rightClick(float mouseTranslatedX, float mouseTranslatedY) {
        Coords coords = getGridAt(mouseTranslatedX, mouseTranslatedY);
        if (selectedBuilding != null || coords == null) {
            selectedBuilding = null;
            needRefresh = true;
        } else {
            Building building = controller.getFactorySim().getGrid().getBuildingAt(coords.x, coords.y);
            if (building == null) {
                return;
            }
            controller.getFactorySim().getGrid().removeBuilding(building);
            controller.getCombatSim().getPlayer().addBuilding(building);
            needRefresh = true;
        }
    }

    // Generic code that translates mouse clicks on grid into an x and y coord of a 2D array (in this case grid's 2D array)
    private Coords getGridAt(float mouseTranslatedX, float mouseTranslatedY) {
        boolean insideWholeGridX = mouseTranslatedX >= gridStartX && mouseTranslatedX < gridStartX + gridWidth * tileSize;
        boolean insideWholeGridY = mouseTranslatedY <= gridEndY && mouseTranslatedY > gridEndY - gridHeight * tileSize;

        if (!insideWholeGridX || !insideWholeGridY) {
            return null;
        }

        // Since the whole grid is scaled up by size (including both its x and y coords), we can divide the mouse grid coordinates by the tile size to scale it back down to get the unscaled tile coordinate
        int unscaledTileX = (int) ((mouseTranslatedX - gridStartX) / (tileSize));
        int unscaledTileY = (int) ((gridEndY - mouseTranslatedY) / (tileSize));

        return new Coords(unscaledTileX, unscaledTileY);
    }

    // Get quadrant of a grid (may be helpful for pointing at stuff)
    private DirectedCoords getGridQuadrantAt(float mouseTranslatedX, float mouseTranslatedY) {
        boolean insideWholeGridX = mouseTranslatedX >= gridStartX && mouseTranslatedX < gridStartX + gridWidth * tileSize;
        boolean insideWholeGridY = mouseTranslatedY <= gridEndY && mouseTranslatedY > gridEndY - gridHeight * tileSize;

        if (!insideWholeGridX || !insideWholeGridY) {
            return null;
        }

        // Since the whole grid is scaled up by size (including both its x and y coords), we can divide the mouse grid coordinates by the tile size to scale it back down to get the unscaled tile coordinate
        int unscaledTileX = (int) ((mouseTranslatedX - gridStartX) / (tileSize));
        int unscaledTileY = (int) ((gridEndY - mouseTranslatedY) / (tileSize));

        int tileX = (int) ((mouseTranslatedX - gridStartX) % tileSize);
        int tileY = (int) ((gridEndY - mouseTranslatedY) % tileSize);
        boolean topRight = tileX > tileY;
        boolean botRight = tileX > (tileSize - tileY);

        int dir;

        if (topRight) {
            if (botRight) {
                dir = 1;
            } else {
                dir = 0;
            }
        } else {
            if (botRight) {
                dir = 2;
            } else {
                dir = 3;
            }
        }

        return new DirectedCoords(unscaledTileX, unscaledTileY, dir);
    }

    /*
    Helper Coordinate class to easily store x and y coordinates (something like Pair class from lectures)
     */

    static class Coords {
        final int x;
        final int y;

        Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "x: "+ x + " y: "+ y;
        }
    }

    static class DirectedCoords extends Coords {
        final int dir;

        DirectedCoords(int x, int y, int direction) {
            super(x, y);
            this.dir = direction % 4;
        }

        @Override
        public String toString() {
            String trueDir = "";
            switch (dir) {
                case 0:
                    trueDir = "up";
                    break;
                case 1:
                    trueDir = "right";
                    break;
                case 2:
                    trueDir = "down";
                    break;
                case 3:
                    trueDir = "left";
                    break;
            }
            return "x: "+ x + " y: "+ y + "direction " + dir + "(" + trueDir + ")";
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
        shapeRenderer.dispose();
        stage.dispose();
        skin.dispose();
    }
}
