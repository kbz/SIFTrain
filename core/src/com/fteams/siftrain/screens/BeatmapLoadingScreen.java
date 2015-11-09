package com.fteams.siftrain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.assets.SimpleSongLoader;
import com.fteams.siftrain.entities.SimpleSong;

public class BeatmapLoadingScreen implements Screen {
    private Image backgroundImage = new Image(Assets.mainMenuBackgroundTexture);
    private Stage stage = new Stage();
    private Label validationResult = new Label("Loaded Beatmap Successfully!", Assets.menuSkin, "song_style_result");
    private Label errorLabel = new Label("Errors:", Assets.menuSkin, "song_style_result");
    private Label warningsLabel = new Label("Warnings:", Assets.menuSkin, "song_style_result");
    private TextButton back = new TextButton("Back", Assets.menuSkin, "item1");
    private TextButton play = new TextButton("Play", Assets.menuSkin, "item1");

    float loadingTime = 2f;
    boolean valid;
    boolean warnings;

    @Override
    public void show() {
        float scaleFactor = stage.getHeight() / GlobalConfiguration.BASE_HEIGHT;
        warnings = false;
        backgroundImage.setHeight(stage.getHeight());
        backgroundImage.setWidth(stage.getWidth());
        stage.addActor(backgroundImage);

        SimpleSongLoader loader = new SimpleSongLoader();
        SimpleSong song = loader.loadSong(Assets.selectedBeatmap);

        Table table = new Table();
        table.setX(stage.getWidth() * 0.15f);
        table.setY(stage.getHeight() * 0.15f);

        table.setWidth(stage.getWidth() * 0.7f);
        table.setHeight(stage.getHeight() * 0.7f);
        valid = song.getValid();

        if (song.getValid()) {
            table.add(validationResult).colspan(2).padBottom(stage.getHeight() * 0.04f).row();

            if (!loader.getWarnings().isEmpty()) {
                warnings = true;
                Table warningTable = new Table(Assets.menuSkin);
                warningTable.setWidth(stage.getWidth() * 0.7f);
                warningTable.setHeight(stage.getHeight() * 0.25f);
                table.add(warningsLabel).left().fillX().row();
                for (String warning : loader.getWarnings()) {
                    Label warningLabel = new Label(warning, Assets.menuSkin, "song_style_result");
                    warningLabel.setFontScale(scaleFactor);
                    errorLabel.setWrap(true);
                    warningTable.add(warningLabel).width(stage.getWidth() * 0.7f).left().fillX().row();
                }
                ScrollPane warningPane = new ScrollPane(warningTable);
                table.add(warningPane).row();
            }
            Assets.setSelectedSong(song);

            table.add(play).row();
            table.add(back).row();
        } else {
            table.add(errorLabel).left().fillX().colspan(2).row();
            Table errorTable = new Table(Assets.menuSkin);
            errorTable.setWidth(stage.getWidth() * 0.7f);
            errorTable.setHeight(stage.getHeight() * 0.25f);
            for (String error : loader.getErrors()) {
                Label errorLabel = new Label(error, Assets.menuSkin, "song_style_result");
                errorLabel.setFontScale(scaleFactor);
                errorLabel.setWrap(true);
                errorTable.add(errorLabel).width(stage.getWidth() * 0.7f).left().fillX().row();
            }
            ScrollPane errorPane = new ScrollPane(errorTable);
            table.add(errorPane).width(stage.getWidth() * 0.7f).height(stage.getHeight() * 0.25f).row();

            if (!loader.getWarnings().isEmpty()) {
                Table warningTable = new Table(Assets.menuSkin);
                warningTable.setWidth(stage.getWidth() * 0.7f);
                warningTable.setHeight(stage.getHeight() * 0.25f);
                table.add(warningsLabel).left().fillX().row();
                for (String warning : loader.getWarnings()) {
                    Label warningLabel = new Label(warning, Assets.menuSkin, "song_style_result");
                    warningLabel.setFontScale(scaleFactor);
                    warningLabel.setWrap(true);
                    warningTable.add(warningLabel).width(stage.getWidth() * 0.7f).left().fillX().row();
                }
                ScrollPane warningPane = new ScrollPane(warningTable);
                table.add(warningPane).width(stage.getWidth() * 0.7f).height(stage.getHeight() * 0.25f).row();
            }

            table.add(back).width(stage.getWidth() * 0.2f).height(stage.getHeight() * 0.1f).row();
            table.add().fill().expand();

        }

        back.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new SongSelectionScreen());
            }
        });

        play.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new LiveOptionsScreen());
            }
        });

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        loadingTime -= delta;
        if (loadingTime <= 0 && !warnings) {
            if (valid) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new SongScreen());

            }
        }

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
