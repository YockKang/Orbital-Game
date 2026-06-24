package com.main.CoreWorks;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.*;
import com.main.CoreWorks.Factory.Upgrade.*;
import com.main.CoreWorks.Resources.*;
import com.main.CoreWorks.database.*;
import com.main.CoreWorks.screens.*;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Coreworks extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;
    public OrthographicCamera camera;
    // The below represents how many pixels the camera can see, the actual map can be much bigger if panning camera (via arrow keys or WASD or click and drag) is enabled
    public static final float VIEWPORT_WIDTH = 1280;
    public static final float VIEWPORT_HEIGHT = 720;

    private Array<FileHandle> resourceFiles = new Array<>();
    private Array<FileHandle> recipeFiles = new Array<>();
    private Array<FileHandle> buildingFiles = new Array<>();
    private boolean devMode = true;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        // use libGDX's default font
        font = new BitmapFont();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);

        // Center the camera in the middle of the screen
        camera.position.set(VIEWPORT_WIDTH /2f, VIEWPORT_HEIGHT /2f, 0);
        camera.update();

        // font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        font.setUseIntegerPositions(false);
        font.getData().setScale(720f / 480f);

        // load game assets and databases
        // 1. load FactoryData

        JsonValue manifestData = JsonProcessor.read(Gdx.files.internal("FactoryData/Manifest.json"));
        fileScanner(manifestData, "Release");
        if (devMode) {
            fileScanner(manifestData, "Dev");
        }


        for (FileHandle fileHandle : resourceFiles) {
            ResourceDatabase.register(JsonProcessor.read(fileHandle));
        }

        for (FileHandle fileHandle : recipeFiles) {
            RecipeDatabase.register(JsonProcessor.read(fileHandle));
        }
        RecipeGroupDatabase.update();

        for (FileHandle fileHandle : buildingFiles) {
            BuildingDatabase.register(JsonProcessor.read(fileHandle));
            BuildingTierDatabase.register(JsonProcessor.read(fileHandle));
        }

        FileHandle enemyFile = Gdx.files.internal("Enemies/Enemies.json");
        EnemyDatabase.register(JsonProcessor.read(enemyFile));

        FileHandle enemyGroupFile = Gdx.files.internal("Enemies/EnemyGroups/EnemyGroups.json");
        EnemyGroupDatabase.register(JsonProcessor.read(enemyGroupFile));


        UpgradeTypeRegistry.registerDefault();
        ModifierRegistry.registerDefault();



        // For now, starting the game leads to a placeholder menu screen
        this.setScreen(new MenuScreen(this)); // eventually will replace with the Main Menu screen
    }

    // Helper method to reset the camera since rn everything uses the same camera
    public void resetCamera() {
        camera.zoom = 1f;
        camera.position.set(VIEWPORT_WIDTH /2f, VIEWPORT_HEIGHT /2f, 0);
        camera.update();
    }

    @Override
    public void render() {
        // A common mistake is to forget to call super.render() with a Game implementation. Without this call, the Screen that you set in the create() method will not be rendered if you override the render method in your Game class!
        // ^^ According to the libGDX wiki
        super.render();
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        batch.dispose();
        font.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height,true);
        super.resize(width, height);
    }

    private void fileScanner(JsonValue manifestData, String group) {
        JsonValue groupData = manifestData.get(group);
        for (String file : groupData.get("Resources").asStringArray()) {
            FileHandle fileHandle = Gdx.files.internal("FactoryData/Resources/" + file);
            resourceFiles.add(fileHandle);
        }

        for (String file : groupData.get("Recipes").asStringArray()) {
            FileHandle fileHandle = Gdx.files.internal("FactoryData/Recipes/" + file);
            recipeFiles.add(fileHandle);
        }
        RecipeGroupDatabase.update();

        for (String file : groupData.get("Buildings").asStringArray()) {
            FileHandle fileHandle = Gdx.files.internal("FactoryData/Buildings/" + file);
            buildingFiles.add(fileHandle);
        }

    }
}
