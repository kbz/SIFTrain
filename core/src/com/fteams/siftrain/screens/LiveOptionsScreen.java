package com.fteams.siftrain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.controller.SongLoader;
import com.fteams.siftrain.entities.SimpleNotesInfo;
import com.fteams.siftrain.entities.SimpleSongInfo;
import com.fteams.siftrain.util.SongUtils;

import java.util.List;

public class LiveOptionsScreen extends ChangeListener implements Screen, InputProcessor {
    private Image backgroundImage = new Image(Assets.mainMenuBackgroundTexture);
    private Stage stage = new Stage();

    private TextButton nextButton = new TextButton("Next", Assets.menuSkin, "item1");
    private TextButton backButton = new TextButton("Back", Assets.menuSkin, "item1");

    private Table table = new Table();

    private CheckBox abModeChooser;
    private Label playbackRateLabel;
    private Label playbackRateValueLabel;

    private Slider playbackRateSlider;
    private Slider aSlider;
    private Slider bSlider;

    private Label aLabel;
    private Label bLabel;

    String[] modes = {"Normal", "A-B repeat"};

    private Float newPlaybackRate;
    private Float newAPosition;
    private Float newBPosition;

    private Music theSong;

    @Override
    public void show() {

        theSong = SongLoader.loadSongFile();

        float fontScale = stage.getHeight() / GlobalConfiguration.BASE_HEIGHT;
        backgroundImage.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(backgroundImage);

        nextButton.getLabel().setFontScale(fontScale);
        backButton.getLabel().setFontScale(fontScale);

        backButton.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (theSong != null)
                {
                    theSong.stop();
                    theSong.dispose();
                }
                ((Game) Gdx.app.getApplicationListener()).setScreen(new SongSelectionScreen());
            }
        }));
        nextButton.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (theSong != null)
                {
                    theSong.stop();
                    theSong.dispose();
                }
                ((Game) Gdx.app.getApplicationListener()).setScreen(new SongScreen());
            }
        }));

        // Playback Rate related stuff:
        newPlaybackRate = GlobalConfiguration.playbackRate == null ? 1.0f : GlobalConfiguration.playbackRate;

        playbackRateLabel = new Label("Playback Rate:" + (newPlaybackRate.compareTo(1.0f) == 0 ? "" : "(no music)"), Assets.menuSkin, "song_style_result");
        playbackRateLabel.setFontScale(fontScale);

        playbackRateValueLabel = new Label(String.format("%.2f", newPlaybackRate) + "x", Assets.menuSkin);
        playbackRateValueLabel.setFontScale(fontScale);

        playbackRateSlider = new Slider(0.5f, 2.0f, 0.05f, false, Assets.menuSkin);
        playbackRateSlider.setValue(newPlaybackRate);
        playbackRateSlider.addListener(this);

        table.setWidth(stage.getWidth() * 0.8f);
        table.setHeight(stage.getHeight() * 0.8f);
        table.setX(stage.getWidth() * 0.1f);
        table.setY(stage.getHeight() * 0.1f);

        table.add(playbackRateLabel).width(stage.getWidth() * 0.15f).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).left();
        table.add(playbackRateValueLabel).right().fillX().row();
        table.add(playbackRateSlider).width(stage.getWidth() * 0.8f).height(playbackRateLabel.getHeight() * fontScale).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).colspan(2).row();
        table.add().height(playbackRateValueLabel.getHeight() / 2f).row();

        // AB - repeat mode
        abModeChooser = new CheckBox("Game Mode (Tap to change): " + modes[GlobalConfiguration.playbackMode == null ? 0 : GlobalConfiguration.playbackMode], Assets.menuSkin);

        float songDuration = getDuration(Assets.selectedSong.song_info);
        newAPosition = GlobalConfiguration.aTime == null ? 0f : GlobalConfiguration.aTime;
        newBPosition = GlobalConfiguration.bTime == null || GlobalConfiguration.bTime > songDuration ? songDuration : GlobalConfiguration.bTime;

        // just to make sure aTime and bTime are not null after this point, we 'set them'
        GlobalConfiguration.aTime = newAPosition;
        GlobalConfiguration.bTime = newBPosition;

        abModeChooser.getImageCell().width(0);
        abModeChooser.getLabel().setFontScale(fontScale);
        abModeChooser.addListener(this);

        aSlider = new Slider(0f, songDuration - 6f, 1f, false, Assets.menuSkin);
        aSlider.setVisible(GlobalConfiguration.playbackMode != null && GlobalConfiguration.playbackMode.equals(SongUtils.GAME_MODE_ABREPEAT));
        aSlider.setValue(newAPosition);
        aSlider.addListener(this);
        bSlider = new Slider(6f, songDuration, 1f, false, Assets.menuSkin);
        bSlider.setVisible(GlobalConfiguration.playbackMode != null && GlobalConfiguration.playbackMode.equals(SongUtils.GAME_MODE_ABREPEAT));
        bSlider.setValue(newBPosition);
        bSlider.addListener(this);

        aLabel = new Label("Start Time: " +String.format("%.2f", newAPosition) , Assets.menuSkin);
        aLabel.setVisible(GlobalConfiguration.playbackMode != null && GlobalConfiguration.playbackMode.equals(SongUtils.GAME_MODE_ABREPEAT));
        bLabel = new Label("End Time: " + String.format("%.2f", newBPosition), Assets.menuSkin);
        bLabel.setVisible(GlobalConfiguration.playbackMode != null && GlobalConfiguration.playbackMode.equals(SongUtils.GAME_MODE_ABREPEAT));

        aLabel.setFontScale(fontScale);
        bLabel.setFontScale(fontScale);

        table.add(abModeChooser).colspan(3).left().padBottom(stage.getHeight() * 0.01f).padTop(stage.getHeight() * 0.01f).row();
        table.add(aLabel).left().row();
        table.add(aSlider).width(stage.getWidth() * 0.8f).height(aLabel.getHeight() * fontScale).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).colspan(2).row();
        table.add(bLabel).left().row();
        table.add(bSlider).width(stage.getWidth() * 0.8f).height(aLabel.getHeight() * fontScale).padTop(stage.getHeight() * 0.01f).padBottom(stage.getHeight() * 0.01f).colspan(2).row();

        table.add().height(playbackRateValueLabel.getHeight() / 2f).row();

        table.add().expand().fill().row();
        table.add(backButton).size(stage.getWidth() * 0.75f / 2, stage.getHeight() * 0.12f);
        table.add(nextButton).size(stage.getWidth() * 0.75f / 2, stage.getHeight() * 0.12f);
        stage.addActor(table);

        InputMultiplexer impx = new InputMultiplexer();
        impx.addProcessor(this);
        impx.addProcessor(stage);

        Gdx.input.setInputProcessor(impx);
        Gdx.input.setCatchBackKey(true);
    }

    private float getDuration(List<SimpleSongInfo> song_info) {
        float timing = 0f;
        for (SimpleSongInfo simpleSongInfo : song_info)
        {
            for (SimpleNotesInfo note : simpleSongInfo.notes)
            {
                if (timing < note.timing_sec)
                {
                    timing = note.timing_sec.floatValue();
                }
            }
        }
        return timing;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        if (theSong != null && theSong.isPlaying() && theSong.getPosition() > newBPosition) {
            theSong.setPosition(newAPosition);
        }
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

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            if (theSong != null)
            {
                theSong.stop();
                theSong.dispose();
            }
            ((Game) Gdx.app.getApplicationListener()).setScreen(new SongSelectionScreen());
            // do nothing
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

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        if (actor == playbackRateSlider) {
            newPlaybackRate = ((Slider) actor).getValue();
            GlobalConfiguration.playbackRate = newPlaybackRate;
            playbackRateLabel.setText("Playback Rate" + (newPlaybackRate.compareTo(1.0f) == 0 ? "" : "(no music)"));
            playbackRateValueLabel.setText(String.format("%.2f", newPlaybackRate) + "x");
        }
        if (actor == aSlider) {
            newAPosition = ((Slider) actor).getValue();
            GlobalConfiguration.aTime = newAPosition;
            aLabel.setText("Start Time: " + String.format("%.2f", newAPosition));
            if (newAPosition + 5f > newBPosition) {
                newBPosition = newAPosition + 5f;
                bSlider.setValue(newBPosition);
                bLabel.setText("End Time: " + String.format("%.2f", newBPosition));
                GlobalConfiguration.bTime = newBPosition;
            }
            if (theSong != null) {
                theSong.pause();
                theSong.setPosition(newAPosition);
                theSong.setVolume(GlobalConfiguration.songVolume / 100f);
                theSong.play();
            }
        }
        if (actor == bSlider) {
            newBPosition = ((Slider) actor).getValue();
            GlobalConfiguration.bTime = newBPosition;
            bLabel.setText("End Time: " + String.format("%.2f", newBPosition));
            if (newBPosition - 5f < newAPosition) {
                newAPosition = newBPosition - 5f;
                aSlider.setValue(newAPosition);
                aLabel.setText("Start Time: " +  String.format("%.2f", newAPosition));
                GlobalConfiguration.aTime = newAPosition;
            }
        }
        if (actor == abModeChooser) {
            GlobalConfiguration.playbackMode = GlobalConfiguration.playbackMode == null ? 1 : (GlobalConfiguration.playbackMode + 1) % modes.length;
            abModeChooser.setText("Game Mode (Tap to change): " + modes[GlobalConfiguration.playbackMode]);

            aLabel.setVisible(GlobalConfiguration.playbackMode.equals(SongUtils.GAME_MODE_ABREPEAT));
            aSlider.setVisible(GlobalConfiguration.playbackMode.equals(SongUtils.GAME_MODE_ABREPEAT));
            bLabel.setVisible(GlobalConfiguration.playbackMode.equals(SongUtils.GAME_MODE_ABREPEAT));
            bSlider.setVisible(GlobalConfiguration.playbackMode.equals(SongUtils.GAME_MODE_ABREPEAT));
        }
    }
}
