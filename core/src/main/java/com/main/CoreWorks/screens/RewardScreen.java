package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.main.CoreWorks.Rewards.Reward;
import com.main.CoreWorks.RunPersistence.RunState;

public class RewardScreen implements Screen {

    private final Coreworks game;
    private RunState runState;
    private Array<Reward> rewards;

    // Rendering Reward screen using Scene2D default graphics
    private Stage stage;
    private Skin skin;

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

        // The below code builds the scene2D
        Table table = new Table();
        table.setFillParent(true);

        // Top align the table + add label in the current row
        table.top().pad(30);
        stage.addActor(table);
        table.add(new Label("Choose a reward", skin)).padBottom(30);

        // Next row
        table.row();

        // Create a new table to standardize the size of each reward card in a row
        // Current size: 320 by 180
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
                    reward.apply(runState);
                    game.setScreen(new MapScreen(game, runState));
                }
            });

            rewardCard.add(select).pad(15);

            // Add the reward card into the row of cards
            rewardCards.add(rewardCard);
        }

        // Adds the entire row of reward cards into the row
        table.add(rewardCards).row();
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
