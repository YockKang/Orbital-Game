package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Rewards.AddUpgradeReward;
import com.main.CoreWorks.Rewards.Reward;
import com.main.CoreWorks.RunPersistence.RunState;

/*
Note: Reward Screen does NOT handle unlocking of next nodes, it should be settled before coming to this screen
 */
public class RewardScreen implements Screen {

    private final Coreworks game;
    private RunState runState;
    private Array<Reward> rewards;

    // Rendering Reward screen using Scene2D default graphics
    private Stage stage;
    private Skin skin;

    // Handles the different selection screens without creating a new screen each time and wasting resources
    // Add any additional "screens" that rely on Reward class (like different types of confirm screen) as an enum here
    private enum screenType {
        CHOOSE_REWARD,
        CHOOSE_BUILDING,
        UPGRADE_CONFIRMATION
    }

    // Default is the reward selection screen
    private screenType screen = screenType.CHOOSE_REWARD;

    private Building selectedBuilding;
    private Reward selectedReward;
    private Array<Building> selectableBuildings;

    public RewardScreen(Coreworks game, RunState runState, Array<Reward> rewards) {
        this.game = game;
        this.runState = runState;
        this.rewards = rewards;
    }

    @Override
    public void show() {
        stage = new Stage(game.viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        // Uses the default libgdx skin, eventually will replace with our own
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Draws the UI based on which screen needs to be shown
        selectScreen();
    }

    // The code will be used to redraw the UI based on what should be displayed
    // Add any additional rewards + their corresponding screen under the switch
    private void selectScreen() {
        switch (screen) {
            case UPGRADE_CONFIRMATION -> buildUpgradeConfirmationUI();
            case CHOOSE_BUILDING -> buildBuildingSelectionUI();
            default -> buildRewardSelectionUI();
        }
    }

    private void buildRewardSelectionUI() {
        stage.clear();

        // The below code builds the table that will serve as the base table for all subsequent UI building in the Stage
        Table table = new Table();
        table.setFillParent(true);

        // Top align the table + add label in the current row
        table.top().pad(30);
        stage.addActor(table);
        table.add(new Label("Choose a reward", skin)).padBottom(30);

        // Next row
        table.row();

        // Create a new table to standardize the size of each reward card in a row
        // Current size: 350 by 250
        Table rewardCards = new Table();
        rewardCards.defaults().pad(15).width(350).height(250);

        // Within the table, use another table to standardize the name, description and a choose button within each cell in the middle layered table
        for (int i = 0; i < rewards.size; i++) {
            Reward reward = rewards.get(i);

            Table rewardCard = new Table(skin);
            // The background will use LibGDX default skin rounded appearance
            rewardCard.setBackground("default-round");

            // Adds the name and description into 2 separate rows
            rewardCard.add(new Label(reward.getName(),skin)).pad(15).row();
            rewardCard.add(new Label(reward.getDescription(),skin)).pad(15).row();

            // Adds the select button
            TextButton select = new TextButton("Select", skin);
            select.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedReward = reward;

                    // if reward does not need target, then just apply and go next
                    if (!reward.needTarget()) {
                        reward.apply(runState);
                        game.setScreen(new MapScreen(game, runState));
                    }

                    // if reward needs a target and is upgrading a building, then get all buildings as target and set the screen to choose building
                    if (reward instanceof AddUpgradeReward upgradeReward) {
                        selectableBuildings = runState.getOwnedBuildings();
                        screen = screenType.CHOOSE_BUILDING;
                        selectScreen();
                    }

                    // Any additional rewards that require a new "screen" should be added below
                }
            });
            rewardCard.add(select).pad(15);

            // Add the reward card into the row of cards
            rewardCards.add(rewardCard);
        }

        // Adds the entire row of reward cards into the original table
        table.add(rewardCards).row();
    }

    private void buildBuildingSelectionUI() {
        stage.clear();

        // The below code builds the table that will serve as the base table for all subsequent UI building in the Stage
        Table table = new Table();
        table.setFillParent(true);

        // Top align the table + add label in the current row
        table.top().pad(30);
        stage.addActor(table);

        table.add(new Label("Choose a building to upgrade", skin)).pad(20).row();

        // Builds the table that shows the available buildings (Similar to rewardCards table)
        Table buildingTable = new Table();
        buildingTable.defaults().pad(10).width(200).height(100);

        // Fallback in case there is no buildings available or some weird nullPointer exception
        if (selectableBuildings == null || selectableBuildings.size == 0) {
            table.add(new Label("No Valid Buildings available!", skin)).pad(20).row();

            // Builds the back button
            TextButton back = new TextButton("Back", skin);
            back.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    screen = screenType.CHOOSE_REWARD;
                    selectScreen();
                }
            });

            table.add(back).pad(20).row();
            return;
        }

        // Builds the display of all the buildings with a limit on how many displayed per row.
        int buildingPerRow = 5;
        int count = 0;
        for (int i = 0; i < selectableBuildings.size; i++) {
            Building building = selectableBuildings.get(i);

            // For now, the selection button is just the name of the building, should replace with sprites when we eventually have them
            TextButton buildingButton = new TextButton(building.displayName(), skin);
            buildingButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedBuilding = building;

                    // Sets the screens to the different confirmation screens based on what type of reward it is that needs building inputs
                    if (selectedReward instanceof AddUpgradeReward upgradeReward) {
                        screen = screenType.UPGRADE_CONFIRMATION;
                        selectScreen();
                    }

                    // More rewards requiring buildings below
                }
            });

            // Adds the building selection button
            buildingTable.add(buildingButton).pad(15);

            // Automatically use a new row when too many buildings are in a row
            count++;
            if (count % buildingPerRow == 0) {
                buildingTable.row();
            }
        }

        // Adds the whole row of building selection into the base table
        table.add(buildingTable).row();

        // In case of regret, have a back button to return to previous screen
        // Builds the back button
        TextButton back = new TextButton("Back", skin);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen = screenType.CHOOSE_REWARD;
                selectScreen();
            }
        });

        table.add(back).pad(20).width(100).height(50).row();
    }

    private void buildUpgradeConfirmationUI() {
        stage.clear();

        // Handles weird nullPointer exceptions (should not trigger under normal circumstances)
        if (selectedBuilding == null || (!(selectedReward instanceof AddUpgradeReward upgradeReward))) {
            game.setScreen(new MapScreen(game, runState));
            return;
        }

        // We should only reach this UI if the reward is an upgrade reward
        AddUpgradeReward reward = (AddUpgradeReward) selectedReward;

        // The below code builds the table that will serve as the base table for all subsequent UI building in the Stage
        Table table = new Table();
        table.setFillParent(true);

        // Top align the table + add label in the current row
        table.top().pad(30);
        stage.addActor(table);

        table.add(new Label("Confirm upgrade?", skin)).pad(20).row();
        table.add(new Label("Target: " + selectedBuilding.displayName(), skin)).pad(20).row();
        table.add(new Label("Upgrade: " + reward.getDescription(), skin)).pad(20).row();

        // Display before and after stats here
        table.add(new Label(" Changes: " + reward.getUpgrade().displayChanges(selectedBuilding).toString(), skin)).pad(20).row();

        // Add any needed buttons into the table below
        Table buttons = new Table();
        buttons.defaults().pad(20).width(150).height(75);

        // Adds the confirm button
        TextButton confirm = new TextButton("Confirm", skin);
        confirm.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                reward.setTarget(selectedBuilding);
                reward.apply(runState);
                game.resetCamera();
                game.setScreen(new MapScreen(game, runState));
            }
        });

        // Adds the cancel button
        TextButton cancel = new TextButton("Cancel", skin);
        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen = screenType.CHOOSE_BUILDING;
                selectScreen();
            }
        });

        // Adds all buttons into the same row in a line
        buttons.add(confirm).pad(10);
        buttons.add(cancel).pad(10);

        table.add(buttons).pad(20).row();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
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
        stage.dispose();
        skin.dispose();
    }
}
