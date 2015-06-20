package com.fteams.siftrain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.controller.SongLoader;
import com.fteams.siftrain.entities.SimpleSong;

@SuppressWarnings("unchecked")
public class SongSelectionScreen implements Screen, InputProcessor {

    private Stage stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    private List<SimpleSong> songList = new List<>(Assets.menuSkin);
    private ScrollPane songListPane = new ScrollPane(null, Assets.menuSkin);
    private Table table = new Table();
    private TextButton nextButton = new TextButton("Next", Assets.menuSkin, "item1");
    private TextButton backButton = new TextButton("Back", Assets.menuSkin, "item1");
    private Image backgroundImage = new Image(Assets.mainMenuBackgroundTexture);

    @Override
    public void show() {
        float scaleFactor = stage.getHeight()/GlobalConfiguration.BASE_HEIGHT;
        //The elements are displayed in the order you add them.
        //The first appear on top, the last at the bottom.
        backgroundImage.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(backgroundImage);

        songList.setItems((Array) Assets.songList);
        songList.setSelectedIndex(0);

        nextButton.getLabel().setFontScale(scaleFactor);
        backButton.getLabel().setFontScale(scaleFactor);

        songList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                List<SimpleSong> thisList = (List<SimpleSong>) actor;
                if (Assets.music != null) {
                    if (Assets.music.isPlaying()) {
                        Assets.music.stop();
                    }
                    Assets.music.dispose();
                }
                Assets.music = SongLoader.loadSongFile(thisList.getSelected().getResourceName());
                if (Assets.music != null) {
                    Assets.music.setLooping(true);
                    Assets.music.setVolume(GlobalConfiguration.songVolume / 100f);
                    Assets.music.play();
                }
            }
        });
        if (Assets.selectedSong != null) {
            songList.setSelected(Assets.selectedSong);
        }
        songListPane.setWidget(songList);
        songListPane.setWidth(stage.getWidth());

        table.add(songListPane).colspan(2).size(stage.getWidth() * 0.87f, stage.getHeight() * 0.65f).row();
        table.setWidth(stage.getWidth());
        table.setHeight(stage.getHeight());

        backButton.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Assets.music != null) {
                    if (Assets.music.isPlaying()) {
                        Assets.music.stop();
                    }
                    Assets.music.dispose();
                }
                Assets.setSelectedSong(songList.getSelected());
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
            }
        }));
        nextButton.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Assets.music != null) {
                    if (Assets.music.isPlaying()) {
                        Assets.music.stop();
                    }
                    Assets.music.dispose();
                }
                if (songList.getSelected() == null)
                {
                    return;
                }
                Assets.setSelectedSong(songList.getSelected());
                ((Game) Gdx.app.getApplicationListener()).setScreen(new SongScreen());
            }
        }));
        table.add(backButton).size(stage.getWidth() * 0.87f / 2, stage.getHeight() * 0.2f);
        table.add(nextButton).size(stage.getWidth() * 0.87f / 2, stage.getHeight() * 0.2f).row();

        stage.addActor(table);

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

        songListPane.act(delta);
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
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK) {
            if (Assets.music != null) {
                if (Assets.music.isPlaying()) {
                    Assets.music.stop();
                }
                Assets.music.dispose();
            }
            Assets.setSelectedSong(songList.getSelected());
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
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
}
