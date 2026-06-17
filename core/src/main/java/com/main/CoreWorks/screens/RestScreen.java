package com.main.CoreWorks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
import com.main.CoreWorks.Generators.RewardGenerator;
import com.main.CoreWorks.Rewards.Reward;
import com.main.CoreWorks.RunPersistence.MapNode;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.entities.Player;

public class RestScreen implements Screen {

    private final Coreworks game;
    private RunState runState;
    private Stage stage;
    private Skin skin;

    // Handles switching between select and confirm screen
    private enum RestScreenState{
        CHOOSING,
        CONFIRMING
    }

    // Any additional choices to be added to rest screen needs to be added here as well
    private enum ChoiceMade{
        HEAL,
        REWARD
    }

    private RestScreenState state = RestScreenState.CHOOSING;
    private ChoiceMade pendingChoice;
    private int healAmt;
    private Array<Reward> mysteryReward;

    public RestScreen(Coreworks game, RunState runState) {
        this.game = game;
        this.runState = runState;
    }

    @Override
    public void show() {
        stage = new Stage(game.viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        // Uses the default libgdx skin, eventually will replace with our own
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Builds the UI
        buildRestUI();
    }

    private void buildRestUI() {
        stage.clear();

        Table table = new Table();
        table.setFillParent(true);
        table.center().pad(20);
        stage.addActor(table);
        table.defaults().width(250).height(100).pad(10);

        if (state == RestScreenState.CHOOSING) {
            table.add(new Label("You can finally rest...", skin)).pad(15).row();
            table.add(new Label("What do you want to do?", skin)).pad(15).row();

            TextButton healButton = new TextButton("Heal up!", skin);
            healButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    healChosen();
                }
            });

            TextButton rewardButton = new TextButton("Upgrade a Building!", skin);
            rewardButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    upgradeChosen();
                }
            });

            table.add(healButton);
            table.add(rewardButton).row();
        }

        if (state == RestScreenState.CONFIRMING) {
            table.add(new Label("Confirm your choice!", skin)).pad(10).row();
            table.add(new Label("Once confirmed, you cannot go back!", skin)).pad(10).row();

            if (pendingChoice == ChoiceMade.HEAL) {
                table.add(new Label("You chose to heal.", skin)).pad(10).row();
                table.add(new Label(String.format("You will heal %s HP.", this.healAmt), skin)).pad(10).row();
            }
            if (pendingChoice == ChoiceMade.REWARD) {
                table.add(new Label("You chose to upgrade a building.", skin)).pad(10).row();
                table.add(new Label("You will get a randomised building upgrade.", skin)).pad(10).row();
            }

            TextButton confirmButton = new TextButton("Confirm", skin);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    runState.getCurrNode().setCompleted(true);
                    for (MapNode next : runState.getCurrNode().getNextNodes()) {
                        next.setUnlocked(true);
                    }
                    if (pendingChoice == ChoiceMade.HEAL) {
                        runState.getPlayer().heal(healAmt);
                        game.setScreen(new MapScreen(game, runState));
                        return;
                    }

                    if (pendingChoice == ChoiceMade.REWARD) {
                        game.setScreen(new RewardScreen(game, runState, mysteryReward));
                        return;
                    }
                }
            });

            TextButton cancelButton = new TextButton("Cancel", skin);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    pendingChoice = null;
                    state = RestScreenState.CHOOSING;
                    buildRestUI();
                }
            });

            table.add(confirmButton);
            table.add(cancelButton).row();
        }

        // Any additional states of the screen will be added below
    }

    public void healChosen() {
        Player player = runState.getPlayer();
        MapNode currNode = runState.getCurrNode();

        int missingHP = player.displayMaxHp() - player.displayCurrentHp();

        // Healing will be based on their missing HP or a fixed number, whichever is higher
        this.healAmt = (int) Math.max(10 * currNode.getTier(), 0.4 * missingHP);

        pendingChoice = ChoiceMade.HEAL;
        state = RestScreenState.CONFIRMING;
        buildRestUI();
    }

    public void upgradeChosen() {
        mysteryReward = RewardGenerator.generateRestNodeReward(runState);
        pendingChoice = ChoiceMade.REWARD;
        state = RestScreenState.CONFIRMING;
        buildRestUI();
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
