package com.fteams.siftrain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.assets.Results;

public class ResultsScreen implements Screen, InputProcessor {

    Stage stage = new Stage();
    private Texture texture = Assets.mainMenuBackgroundTexture;
    private Image splashImage = new Image(texture);
    private Table table = new Table();
    private Label scoreLabel = new Label("Score:", Assets.menuSkin, "song_style_result_values");
    private Label scoreResultLabel;
    private Label accuracyLabel = new Label("Avg. Accuracy:", Assets.menuSkin, "song_style_result_values");
    private Label accuracyResultLabel;
    private Label normalizedAccuracyLabel = new Label("% Accuracy:", Assets.menuSkin, "song_style_result_values");
    private Label normalizedAccuracyResultLabel;
    private Label accuracyRangeLabel = new Label("Accuracy Range:", Assets.menuSkin, "song_style_result_values");
    private Label accuracyRangeResultLabel;
    private Label unstableRatingLabel = new Label("Unstable Rating:", Assets.menuSkin, "song_style_result_values");
    private Label unstableRatingValueLabel;
    private Label rankLabel = new Label("Score Rank:", Assets.menuSkin, "song_style_result_values");
    private Label rankResultLabel;
    private Label badLabel = new Label("Bad:", Assets.menuSkin, "song_style_result_values");
    private Label badResultLabel;
    private Label goodLabel = new Label("Good:", Assets.menuSkin, "song_style_result_values");
    private Label goodResultLabel;
    private Label greatLabel = new Label("Great", Assets.menuSkin, "song_style_result_values");
    private Label greatResultLabel;
    private Label perfectLabel = new Label("Perfect:", Assets.menuSkin, "song_style_result_values");
    private Label perfectResultLabel;
    private Label missLabel = new Label("Miss:", Assets.menuSkin, "song_style_result_values");
    private Label missResultLabel;
    private Label comboLabel = new Label("Largest Combo:", Assets.menuSkin, "song_style_result_values");
    private Label comboResultLabel;
    private Label titleLabel = new Label("Results/結果発表", Assets.menuSkin, "default");

    private float timeBeforeClose = 5f;
    private boolean canClose = false;

    @Override
    public void show() {
        // title font scale = 1 for a 720 height
        float fontScale = stage.getHeight()/ GlobalConfiguration.BASE_HEIGHT;

        splashImage.setHeight(stage.getHeight());
        splashImage.setWidth(stage.getWidth());
        stage.addActor(splashImage);
        titleLabel.setX(stage.getWidth() / 2 - titleLabel.getWidth() / 2);
        titleLabel.setY(stage.getHeight() - stage.getHeight() * 0.3f);
        titleLabel.setFontScale(fontScale);

        table.setFillParent(true);
        scoreResultLabel = new Label(Integer.toString(Results.score), Assets.menuSkin, "song_style_result_values");
        accuracyResultLabel = new Label(String.format("%.2f", Results.accuracy * 1000) + " ms.", Assets.menuSkin, "song_style_result_values");
        accuracyRangeResultLabel = new Label(String.format("%.2f", Results.minAccuracy * 1000) + " ms. to " + String.format("%.2f", Results.maxAccuracy * 1000) + " ms.", Assets.menuSkin, "song_style_result_values");
        normalizedAccuracyResultLabel = new Label(String.format("%.2f", Results.normalizedAccuracy * 100f) + "%", Assets.menuSkin, "song_style_result_values");
        unstableRatingValueLabel = new Label(String.format("%.2f", Results.unstableRating * 1000), Assets.menuSkin, "song_style_result_values");
        rankResultLabel = new Label(Results.getRankString(), Assets.menuSkin, "song_style_result_values");
        missResultLabel = new Label(Integer.toString(Results.miss), Assets.menuSkin, "song_style_result_values");
        badResultLabel = new Label(Integer.toString(Results.bads), Assets.menuSkin, "song_style_result_values");
        goodResultLabel = new Label(Integer.toString(Results.goods), Assets.menuSkin, "song_style_result_values");
        greatResultLabel = new Label(Integer.toString(Results.greats), Assets.menuSkin, "song_style_result_values");
        perfectResultLabel = new Label(Integer.toString(Results.perfects), Assets.menuSkin, "song_style_result_values");
        comboResultLabel = new Label(Integer.toString(Results.combo) + (Results.combo == Assets.selectedSong.song_info[0].notes.length ? (Results.bads > 0 || Results.goods > 0 ? "(fake FC)" : " (FC)") : ""), Assets.menuSkin, "song_style_result_values");

        scoreResultLabel.setFontScale(fontScale);
        accuracyResultLabel.setFontScale(fontScale);
        accuracyRangeResultLabel.setFontScale(fontScale);
        normalizedAccuracyResultLabel.setFontScale(fontScale);
        unstableRatingValueLabel.setFontScale(fontScale);
        rankResultLabel.setFontScale(fontScale);
        missResultLabel.setFontScale(fontScale);
        badResultLabel.setFontScale(fontScale);
        goodResultLabel.setFontScale(fontScale);
        greatResultLabel.setFontScale(fontScale);
        perfectResultLabel.setFontScale(fontScale);
        comboResultLabel.setFontScale(fontScale);

        scoreLabel.setFontScale(fontScale);
        rankLabel.setFontScale(fontScale);
        comboLabel.setFontScale(fontScale);
        normalizedAccuracyLabel.setFontScale(fontScale);
        accuracyLabel.setFontScale(fontScale);
        accuracyRangeLabel.setFontScale(fontScale);
        unstableRatingLabel.setFontScale(fontScale);

        perfectLabel.setFontScale(fontScale);
        greatLabel.setFontScale(fontScale);
        goodLabel.setFontScale(fontScale);
        badLabel.setFontScale(fontScale);
        missLabel.setFontScale(fontScale);

        Label songResultTitle = new Label(Assets.selectedSong.toString(), Assets.menuSkin, "song_result_song_title");
        songResultTitle.setFontScale(fontScale);


        table.add(songResultTitle).colspan(3).row();
        table.add(titleLabel).colspan(3).padBottom(stage.getHeight() * 0.1f).row();
        table.add(scoreLabel).fillX();
        table.add().width(stage.getWidth() * 0.2f);
        table.add(scoreResultLabel).fillX().row();
        table.add(rankLabel).fillX();
        table.add().width(stage.getWidth() * 0.2f);
        table.add(rankResultLabel).fillX().row();
        table.add(comboLabel).fillX().padBottom(20);
        table.add().width(stage.getWidth() * 0.2f).padBottom(20);
        table.add(comboResultLabel).fillX().padBottom(20).row();

        table.add(normalizedAccuracyLabel).fillX();
        table.add().width(stage.getWidth() * 0.2f);
        table.add(normalizedAccuracyResultLabel).fillX().row();
        table.add(accuracyLabel).fillX();
        table.add().width(stage.getWidth() * 0.2f);
        table.add(accuracyResultLabel).fillX().row();
        table.add(accuracyRangeLabel).fillX();
        table.add().width(stage.getWidth() * 0.2f);
        table.add(accuracyRangeResultLabel).fillX().row();
        table.add(unstableRatingLabel).padBottom(20).fillX();
        table.add().width(stage.getWidth() * 0.2f).padBottom(20);
        table.add(unstableRatingValueLabel).padBottom(20).fillX().row();

        table.add(perfectLabel).fillX();
        table.add().width(stage.getWidth() * 0.2f);
        table.add(perfectResultLabel).fillX().row();
        table.add(greatLabel).fillX();
        table.add().width(stage.getWidth() * 0.2f);
        table.add(greatResultLabel).fillX().row();
        table.add(goodLabel).fillX();
        table.add().width(stage.getWidth() * 0.2f);
        table.add(goodResultLabel).fillX().row();
        table.add(badLabel).fillX();
        table.add().width(stage.getWidth() * 0.2f);
        table.add(badResultLabel).fillX().row();
        table.add(missLabel).fillX();
        table.add().width(stage.getWidth() * 0.2f);
        table.add(missResultLabel).fillX().row();

        stage.addActor(table);

        Gdx.input.setInputProcessor(this);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

        timeBeforeClose -= delta;
        if (timeBeforeClose <= 0) {
            canClose = true;
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
        if (canClose) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new SongSelectionScreen());
        }
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
