package com.main.CoreWorks;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.*;
import com.main.CoreWorks.database.*;
import com.main.CoreWorks.screens.*;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Coreworks extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;
    public OrthographicCamera camera;
    // The below represents a 800 x 480 coordinate system, regardless of the screen resolution
    public static final float WORLD_WIDTH = 1280;
    public static final float WORLD_HEIGHT = 720;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        // use libGDX's default font
        font = new BitmapFont();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // Center the camera in the middle of the screen
        camera.position.set(WORLD_WIDTH/2f, WORLD_HEIGHT/2f, 0);
        camera.update();

        // font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        font.setUseIntegerPositions(false);
        font.getData().setScale(720f / 480f);

        // load game assets and databases
        Array<FileHandle> resourceFiles = new Array<>();
        fileScanner(resourceFiles, Gdx.files.internal("assets/FactoryData/Resources"));
        resourceFiles.iterator().forEach(
            fh -> ResourceDatabase.register(JsonProcessor.read(fh)));

        Array<FileHandle> recipeFiles = new Array<>();
        fileScanner(recipeFiles, Gdx.files.internal("assets/FactoryData/Recipes"));
        recipeFiles.iterator().forEach(
            fh -> RecipeDatabase.register(JsonProcessor.read(fh)));

        RecipeGroupDatabase.update();

        Array<FileHandle> buildingFiles = new Array<>();
        fileScanner(buildingFiles, Gdx.files.internal("assets/FactoryData/Buildings"));
        buildingFiles.iterator().forEach(
            fh -> BuildingDatabase.register(JsonProcessor.read(fh)));
        buildingFiles.iterator().forEach(
            fh -> BuildingTierDatabase.register(JsonProcessor.read(fh)));


        Array<FileHandle> enemyFiles = new Array<>();
        fileScanner(enemyFiles, Gdx.files.internal("assets/Enemies"));
        enemyFiles.iterator().forEach(
            fh -> EnemyDatabase.register(JsonProcessor.read(fh)));

        Array<FileHandle> enemyGroupFiles = new Array<>();
        fileScanner(enemyGroupFiles, Gdx.files.internal("assets/EnemyGroups"));
        enemyGroupFiles.iterator().forEach(
            fh -> EnemyGroupDatabase.register(JsonProcessor.read(fh)));



        // For now, starting the game leads to a placeholder menu screen
        this.setScreen(new MenuScreen(this)); // eventually will replace with the Main Menu screen
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

    private void fileScanner(Array<FileHandle> out, FileHandle folder) {
        for (FileHandle child : folder.list()) {
            if (child.isDirectory()) {
                fileScanner(out, child);
            }
            else if (child.extension().equals("json")) {
                out.add(child);
            }
        }
    }
}
