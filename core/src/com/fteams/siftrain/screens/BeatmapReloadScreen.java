package com.fteams.siftrain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;

public class BeatmapReloadScreen implements Screen {
    private Image backgroundImage = new Image(Assets.mainMenuBackgroundTexture);
    private Stage stage = new Stage();
    private ProgressBar loadingProgress = new ProgressBar(0.0f, 100f, 0.1f, false, Assets.menuSkin);
    int phase = 0;
    Label infoLabel = new Label("Performing a hard reload...\n\n" +
            "If any .osz or .osu beatmaps were added or updated, " +
            "be sure to remove their their corresponding .rs files.\n\n" +
            "For .osz beatmap containers, sound files will be extracted " +
            "into beatmaps/soundfiles/, the beatmap files (.osu) will be " +
            "converted into .rs format and placed in beatmaps/datafiles/ " +
            "this is done in order to reduce loading times during gameplay. \n\n" +
            "This operation can take anywhere between a couple of seconds to a couple of " +
            "minutes depending on the amount of beatmaps loaded, please wait....", Assets.menuSkin, "song_style_result");
    @Override
    public void show() {
        float scaleFactor = stage.getHeight() / GlobalConfiguration.BASE_HEIGHT;
        backgroundImage.setSize(stage.getWidth(), stage.getHeight());
        loadingProgress.setSize(stage.getWidth() * 0.7f, stage.getHeight() * 0.07f);
        loadingProgress.setX(stage.getWidth() * 0.15f);
        loadingProgress.setY(stage.getHeight() * 0.1f);

        infoLabel.setX(stage.getWidth() * 0.1f);
        infoLabel.setY(stage.getHeight() * 0.2f);
        infoLabel.setWrap(true);
        infoLabel.setWidth(stage.getWidth() * 0.8f);
        infoLabel.setHeight(stage.getHeight() * 0.7f);
        infoLabel.setFontScale(scaleFactor);

        stage.addActor(backgroundImage);
        stage.addActor(loadingProgress);
        stage.addActor(infoLabel);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

        // done loading
        if (Assets.update())
        {

            if (phase == 1) {
                Assets.setSongs();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new SettingsScreen());
            }
            if (phase == 0)
            {
                phase++;
                // this will cause any newly created .rs files to be loaded.
                Assets.reloadBeatmaps();
            }
        }
        loadingProgress.setValue((Assets.getProgress() - 0.5f)* 200f);
        loadingProgress.act(delta);
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

    }
}
