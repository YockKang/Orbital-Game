package com.main.CoreWorks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.Factory.IOPort;
import com.main.CoreWorks.database.EnemyDatabase;
import com.main.CoreWorks.database.PlayerDatabase;
import com.main.CoreWorks.entities.Enemy;
import com.main.CoreWorks.simulators.CombatController;
import com.main.CoreWorks.simulators.CombatSim;
import com.main.CoreWorks.simulators.FactorySim;

public class CombatScreen implements Screen {

    Coreworks game;
    CombatController controller;
    private float accumulator = 0f;
    private static final float TIME_STEP = 1/4f; // 4 Ticks per second
    private int tickCount = 0;
    private Vector2 mouse2DCoords = new Vector2();
    private ShapeRenderer shapeRenderer;
    private Coords hoveredGridCoords = null;
    private boolean hoveredCanPlace = false;
    private boolean isPaused = true;


    // Hardcoded grid size for milestone 1 testing purposes
    // Should be deleted eventually since it should be handled by the global runState which carries over the factory
    private final int gridWidth = 4;
    private final int gridHeight = 4;

    // Temp Layout since we have not decided how we want the final UI to look like yet
    // Rmb that everything is drawn in a coordinate system (check Coreworks class for the public static final screen size)
    private final int gridSize = 400;

    private final int tileSize = Math.min(gridSize / gridWidth, gridSize / gridHeight);

    private final int gridMidX = (int) (Coreworks.WORLD_WIDTH / 2);
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

    public CombatScreen(Coreworks game) {

        this.game = game;
        // Since this is milestone 1, we will be hardcoding the encounter and grid for now
        // Eventually combatSim should be handled by the Map Screen and node generation code to create the combat encounter
        // and factorySim would be carried over via the global runState class or something
        FactorySim factorySim = new FactorySim(new FactoryGrid(gridHeight,gridWidth));
        Array<Enemy> enemies = new Array<>();
        enemies.add(EnemyDatabase.createMissileDrone());
        enemies.add(EnemyDatabase.createDisablingDrone());
        CombatSim combatSim = new CombatSim(PlayerDatabase.createEngineer(), enemies);
        this.controller = new CombatController(factorySim, combatSim);
    }

    @Override
    public void show() {
        game.viewport.apply();
        game.camera.update();
        shapeRenderer = new ShapeRenderer();
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
        drawInventory();
        drawCombatHUD();
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

    public void drawCombatHUD() {
        game.batch.begin();
        // Below draws the combat log
        Array<String> log = controller.getCombatSim().getCombatLog();
        for (int i = 0; i < log.size; i++) {
            game.font.draw(game.batch, log.get(i), inventoryStartX, 700 - i * 20);
        }

        // Below draws the Entities HUD
        game.font.draw(game.batch, "Coreworks - Milestone 1", 40, 710);
        game.font.draw(game.batch, "Ticks: " + tickCount, 40, 680);
        game.font.draw(game.batch, controller.getCombatSim().getPlayer().toString(), 210, 675);
        game.font.draw(game.batch, controller.getCombatSim().getEnemies().toString(), 940, 675);

        // Below draws the selected building HUD
        game.font.draw(game.batch, selectedBuilding == null ? "Selected: None" : "Selected: " + selectedBuilding, 60, 575);

        // Below draws the rotation
        if (selectedBuilding != null) {
            game.font.draw(game.batch, "Current rotation: " + selectedBuilding.getRotation(), 940, 175);
            game.font.draw(game.batch, "Press R to rotate", 940, 200);
        }

        // Below draws the pause Screen
        if (isPaused) {
            game.font.draw(game.batch, "PAUSED", 600, 420);
            game.font.draw(game.batch, "Press Space to Resume", 540, 380);
        }

        // Below draws the hints
        game.font.draw(game.batch, "Left click Inventory - Select", 40, 225);
        game.font.draw(game.batch, "Left click Grid - Place", 40, 175);
        game.font.draw(game.batch, "Right click - Deselect or Remove building", 40, 125);

        game.batch.end();

        // Below draws the screen transitions
        if (controller.isWin()) {
            game.setScreen(new WinScreen(game));
            return;
        } else if (controller.isLost()) {
            game.setScreen(new LoseScreen(game));
            return;
        }
    }

    public void drawInventory() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Draws the Outline of the grid
        for (int i = 0; i < controller.getCombatSim().getPlayer().getInventory().size; i++) {
            int leftBoundInventoryBorder = inventoryStartX + i * (inventorySlotSize + inventorySlotGap);

            // Highlight the building if it is currently selected
            if (selectedBuilding == controller.getCombatSim().getPlayer().getBuildingAt(i)) {
                shapeRenderer.setColor(Color.GREEN);
            } else {
                shapeRenderer.setColor(Color.WHITE);
            }

            shapeRenderer.rect(leftBoundInventoryBorder, inventoryStartY, inventorySlotSize, inventorySlotSize);
        }

        shapeRenderer.end();

        game.batch.begin();

        // Below draws the Inventory itself
        game.font.draw(game.batch, "Inventory", inventoryStartX, inventoryStartY + 125);

        for (int i = 0; i < controller.getCombatSim().getPlayer().getInventory().size; i++) {
            Building building = controller.getCombatSim().getPlayer().getBuildingAt(i);
            int leftBoundInventoryBorder = inventoryStartX + i * (inventorySlotSize + inventorySlotGap);
            game.font.draw(game.batch, building.displayName(), leftBoundInventoryBorder + 10, inventoryStartY + 56);
        }

        game.batch.end();
    }


    /*
    All mouse inputs should be handled here
     */

    private void externalInput() {
        // Keyboard inputs handled below

        // Press R to rotate building
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (selectedBuilding == null) {
                return;
            }
            int nextRotation = (selectedBuilding.getRotation() + 1) % 4;
            selectedBuilding.setRotation(nextRotation);
        }

        // Pause will be tied to Spacebar
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            isPaused = !isPaused;
        }

        // Mouse inputs handled below
        Vector2 mouseTranslatedCoords = translateMouseToWorld();

        float mouseTranslatedX = mouseTranslatedCoords.x;
        float mouseTranslatedY = mouseTranslatedCoords.y;

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
        Building clickedBuilding = getInventoryBuildingAt(mouseTranslatedX, mouseTranslatedY);
        if (clickedBuilding != null) {
            selectedBuilding = clickedBuilding;
            return;
        }
        if (hoveredGridCoords != null && selectedBuilding != null && !selectedBuilding.isOnGrid()) {
            boolean successfulPlacement = controller.getFactorySim().getGrid().placeBuilding(selectedBuilding, hoveredGridCoords.x, hoveredGridCoords.y, selectedBuilding.getRotation());
            if (successfulPlacement) {
                controller.getCombatSim().getPlayer().removeBuilding(selectedBuilding);
                selectedBuilding = null;
            }
        }
        hoveredGridCoords = getGridAt(mouseTranslatedX, mouseTranslatedY);
        if (hoveredGridCoords != null) {
            selectedBuilding = controller.getFactorySim().getGrid().getBuildingAt(hoveredGridCoords.x, hoveredGridCoords.y);
        }
        // Need to add what to do when a non-inventory building (ie on the grid) is clicked. Display name card? highlight it? etc etc
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
        } else {
            Building building = controller.getFactorySim().getGrid().getBuildingAt(coords.x, coords.y);
            if (building == null) {
                return;
            }
            controller.getFactorySim().getGrid().removeBuilding(building);
            controller.getCombatSim().getPlayer().addBuilding(building);
        }
    }

    // Returns the inventory building clicked by mouse if mouse over inventory
    private Building getInventoryBuildingAt(float mouseTranslatedX, float mouseTranslatedY) {
        for (int i = 0; i < controller.getCombatSim().getPlayer().getInventory().size; i++) {
            int leftBoundInventorySlot = inventoryStartX + i * (inventorySlotSize + inventorySlotGap);
            int rightBoundInventorySlot = leftBoundInventorySlot + inventorySlotSize;
            int topBoundInventorySlot = inventoryStartY + inventorySlotSize;

            boolean clickedSlotX = mouseTranslatedX >= leftBoundInventorySlot && mouseTranslatedX < rightBoundInventorySlot;
            boolean clickedSlotY = mouseTranslatedY >= inventoryStartY && mouseTranslatedY < topBoundInventorySlot;

            if (clickedSlotX && clickedSlotY) {
                return controller.getCombatSim().getPlayer().getBuildingAt(i);
            }
        }
        return null;
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

    /*
    Helper Coordinate class to easily store x and y coordinates (something like Pair class from lectures)
     */

    static final class Coords {
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
    }
}
