package com.fteams.siftrain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;

public class SettingsScreen extends ChangeListener implements Screen, InputProcessor {
    private Texture texture = Assets.mainMenuBackgroundTexture;
    private Image splashImage = new Image(texture);

    private Stage stage = new Stage();
    private Label titleLabel = new Label("Settings/設定", Assets.menuSkin, "default");

    private Label songVolumeLabel = new Label("Song Volume", Assets.menuSkin, "song_style_result");
    private Label feedbackVolumeLabel = new Label("Touch Feedback Volume", Assets.menuSkin, "song_style_result");
    private Label offsetLabel = new Label("Global offset", Assets.menuSkin, "song_style_result");
    private Label teamStrengthLabel = new Label("Team Strength", Assets.menuSkin, "song_style_result");
    private Label pathToBeatmaps = new Label("Path to Beatmaps", Assets.menuSkin, "song_style_result");

    private Label songVolumeValueLabel;
    private Label feedbackVolumeValueLabel;
    private Label offsetValueLabel;
    private Label teamStrengthValueLabel;

    private Slider songVolumeSlider;
    private Slider feedbackVolumeSlider;
    private Slider offsetSlider;
    private Slider teamSrengthSlider;

    private Label pathValueLabel = new Label(GlobalConfiguration.pathToBeatmaps, Assets.menuSkin, "song_style_result");

    private TextButton returnButton = new TextButton("Return to Main Menu", Assets.menuSkin, "item1");

    @Override
    public void show() {
        float fontScale = stage.getHeight() / GlobalConfiguration.BASE_HEIGHT;
        splashImage.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(splashImage);

        songVolumeSlider = new Slider(0, 100f, 1f, false, Assets.menuSkin);
        songVolumeSlider.setWidth(stage.getWidth() * 0.7f);
        songVolumeSlider.setHeight(stage.getHeight() * 0.03f);
        songVolumeSlider.setX(stage.getWidth() / 2 - songVolumeSlider.getWidth() / 2);
        songVolumeSlider.setY(stage.getHeight() / 2 + stage.getHeight() * 0.2f + songVolumeSlider.getHeight() / 2);
        songVolumeSlider.setValue(GlobalConfiguration.songVolume);
        songVolumeSlider.addListener(this);

        feedbackVolumeSlider = new Slider(0, 100f, 1f, false, Assets.menuSkin);
        feedbackVolumeSlider.setWidth(stage.getWidth() * 0.7f);
        feedbackVolumeSlider.setHeight(stage.getHeight() * 0.03f);
        feedbackVolumeSlider.setX(stage.getWidth() / 2 - feedbackVolumeSlider.getWidth() / 2);
        feedbackVolumeSlider.setY(stage.getHeight() / 2 + stage.getHeight() * 0.1f + feedbackVolumeSlider.getHeight() / 2);
        feedbackVolumeSlider.setValue(GlobalConfiguration.feedbackVolume);
        feedbackVolumeSlider.addListener(this);

        offsetSlider = new Slider(-150f, 150f, 1f, false, Assets.menuSkin);
        offsetSlider.setWidth(stage.getWidth() * 0.7f);
        offsetSlider.setHeight(stage.getHeight() * 0.03f);
        offsetSlider.setX(stage.getWidth() / 2 - offsetSlider.getWidth() / 2);
        offsetSlider.setY(stage.getHeight() / 2 - stage.getHeight() * 0.00f + offsetSlider.getHeight() / 2);
        offsetSlider.setValue(GlobalConfiguration.offset);
        offsetSlider.addListener(this);

        teamSrengthSlider = new Slider(0, 70000, 1f, false, Assets.menuSkin);
        teamSrengthSlider.setWidth(stage.getWidth() * 0.7f);
        teamSrengthSlider.setHeight(stage.getHeight() * 0.03f);
        teamSrengthSlider.setX(stage.getWidth() / 2 - teamSrengthSlider.getWidth() / 2);
        teamSrengthSlider.setY(stage.getHeight() / 2 - stage.getHeight() * 0.10f + teamSrengthSlider.getHeight() / 2);
        teamSrengthSlider.setValue(GlobalConfiguration.teamStrength);
        teamSrengthSlider.addListener(this);
        stage.addActor(titleLabel);

        songVolumeLabel.setX(songVolumeSlider.getX());
        songVolumeLabel.setY(songVolumeSlider.getY() + songVolumeSlider.getHeight());
        songVolumeLabel.setFontScale(fontScale);
        songVolumeValueLabel = new Label(Integer.toString(GlobalConfiguration.songVolume), Assets.menuSkin, "song_style_result");
        songVolumeValueLabel.setX(songVolumeSlider.getX() + songVolumeSlider.getWidth() - songVolumeValueLabel.getWidth() * fontScale);
        songVolumeValueLabel.setY(songVolumeSlider.getY() + songVolumeSlider.getHeight());
        songVolumeValueLabel.setFontScale(fontScale);

        titleLabel.setX(stage.getWidth() / 2 - fontScale * titleLabel.getWidth() / 2);
        titleLabel.setY(songVolumeLabel.getY() + songVolumeLabel.getHeight()*fontScale);
        titleLabel.setFontScale(fontScale);

        feedbackVolumeLabel.setX(feedbackVolumeSlider.getX());
        feedbackVolumeLabel.setY(feedbackVolumeSlider.getY() + feedbackVolumeSlider.getHeight());
        feedbackVolumeLabel.setFontScale(fontScale);
        feedbackVolumeValueLabel = new Label(Integer.toString(GlobalConfiguration.feedbackVolume), Assets.menuSkin, "song_style_result");
        feedbackVolumeValueLabel.setX(feedbackVolumeSlider.getX() + feedbackVolumeSlider.getWidth() - feedbackVolumeValueLabel.getWidth() * fontScale);
        feedbackVolumeValueLabel.setY(feedbackVolumeSlider.getY() + feedbackVolumeSlider.getHeight());
        feedbackVolumeValueLabel.setFontScale(fontScale);

        offsetLabel.setX(offsetSlider.getX());
        offsetLabel.setY(offsetSlider.getY() + offsetSlider.getHeight());
        offsetLabel.setFontScale(fontScale);
        offsetValueLabel = new Label((GlobalConfiguration.offset > 0 ? "+" : "") + Integer.toString(GlobalConfiguration.offset) + " ms.", Assets.menuSkin, "song_style_result");
        offsetValueLabel.setX(offsetSlider.getX() + offsetSlider.getWidth() - offsetValueLabel.getWidth() * fontScale);
        offsetValueLabel.setY(offsetSlider.getY() + offsetSlider.getHeight());
        offsetValueLabel.setFontScale(fontScale);

        teamStrengthLabel.setX(teamSrengthSlider.getX());
        teamStrengthLabel.setY(teamSrengthSlider.getY() + teamSrengthSlider.getHeight());
        teamStrengthLabel.setFontScale(fontScale);
        teamStrengthValueLabel = new Label(Integer.toString(GlobalConfiguration.teamStrength), Assets.menuSkin, "song_style_result");
        teamStrengthValueLabel.setX(teamSrengthSlider.getX() + teamSrengthSlider.getWidth() - teamStrengthValueLabel.getWidth() * fontScale);
        teamStrengthValueLabel.setY(teamSrengthSlider.getY() + teamSrengthSlider.getHeight());
        teamStrengthValueLabel.setFontScale(fontScale);

        stage.addActor(songVolumeLabel);
        stage.addActor(feedbackVolumeLabel);
        stage.addActor(offsetLabel);
        stage.addActor(teamStrengthLabel);

        stage.addActor(songVolumeSlider);
        stage.addActor(feedbackVolumeSlider);
        stage.addActor(offsetSlider);
        stage.addActor(teamSrengthSlider);

        stage.addActor(songVolumeValueLabel);
        stage.addActor(feedbackVolumeValueLabel);
        stage.addActor(offsetValueLabel);
        stage.addActor(teamStrengthValueLabel);

        returnButton.setWidth(stage.getWidth() * 0.3f);
        returnButton.setHeight(stage.getHeight() * 0.2f);
        returnButton.setX(stage.getWidth() / 2 - returnButton.getWidth() / 2);
        returnButton.setY(stage.getHeight() * 0.1f);
        returnButton.getLabel().setFontScale(fontScale);

        pathToBeatmaps.setX(offsetSlider.getX());
        pathToBeatmaps.setY(stage.getHeight() / 2 - stage.getHeight() * 0.20f + teamSrengthSlider.getHeight());
        pathToBeatmaps.setFontScale(fontScale);
        stage.addActor(pathToBeatmaps);

        pathValueLabel.setX(offsetSlider.getX() + offsetSlider.getWidth() - pathValueLabel.getWidth() * fontScale);
        pathValueLabel.setY(stage.getHeight() / 2 - stage.getHeight() * 0.20f + teamSrengthSlider.getHeight());
        pathValueLabel.setFontScale(fontScale);

        stage.addActor(pathValueLabel);

        stage.addActor(returnButton);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GlobalConfiguration.storeConfiguration();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
            }
        });

        InputMultiplexer impx = new InputMultiplexer();
        impx.addProcessor(this);
        impx.addProcessor(stage);

        Gdx.input.setInputProcessor(impx);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        if (actor == songVolumeSlider) {
            GlobalConfiguration.songVolume = (int) ((Slider) actor).getValue();
            songVolumeValueLabel.setText(Integer.toString(GlobalConfiguration.songVolume));
            songVolumeValueLabel.setX(actor.getX() + actor.getWidth() - songVolumeValueLabel.getWidth());
        }
        if (actor == feedbackVolumeSlider) {
            GlobalConfiguration.feedbackVolume = (int) ((Slider) actor).getValue();
            feedbackVolumeValueLabel.setText(Integer.toString(GlobalConfiguration.feedbackVolume));
            feedbackVolumeValueLabel.setX(actor.getX() + actor.getWidth() - feedbackVolumeValueLabel.getWidth());
        }
        if (actor == offsetSlider) {
            GlobalConfiguration.offset = (int) ((Slider) actor).getValue();
            offsetValueLabel.setText((GlobalConfiguration.offset > 0 ? "+" : "") + Integer.toString(GlobalConfiguration.offset) + " ms.");
            offsetValueLabel.setX(actor.getX() + actor.getWidth() - offsetValueLabel.getWidth());
        }
        if (actor == teamSrengthSlider) {
            GlobalConfiguration.teamStrength = (int) ((Slider) actor).getValue();
            teamStrengthValueLabel.setText(Integer.toString(GlobalConfiguration.teamStrength));
            teamStrengthValueLabel.setX(actor.getX() + actor.getWidth() - teamStrengthValueLabel.getWidth());
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK) {
            // do nothing
            // Return without saving the changes
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
