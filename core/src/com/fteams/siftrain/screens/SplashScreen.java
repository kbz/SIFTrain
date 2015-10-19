package com.fteams.siftrain.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;

public class SplashScreen implements Screen {
    private Texture texture = new Texture(Gdx.files.internal("bigimages/splash.png"));
    private Image splashImage = new Image(texture);
    private Stage stage = new Stage();
    private Skin skin = new Skin(Gdx.files.internal("skins/splashSkin.json"), new TextureAtlas(Gdx.files.internal("textures/textures.pack.atlas")));
    private ProgressBar loadingProgress = new ProgressBar(0.0f, 100f, 0.1f, false, skin);

    public boolean animationDone = false;
    int phase = 0;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

        if (Assets.update()) { // check if all files are loaded

            if (animationDone) { // when the animation is finished, go to MainMenu()
                // load the assets to into the Assets class
                Assets.setMenuSkin();
                Assets.setHitsounds();
                Assets.setTextures();
                Assets.setFonts();
                if (phase == 1)
                {
                    Assets.setSongs();
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
                }
                if (phase == 0)
                {
                    phase++;
                    Assets.reloadBeatmaps();
                }
            }
        }
        loadingProgress.setValue(phase == 0 ? Assets.getProgress() * 100f : (Assets.getProgress() - 0.5f)*200f);
        loadingProgress.act(delta);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        float sourceHeight = texture.getHeight();
        float targetHeight = stage.getHeight();
        float scale = targetHeight / sourceHeight;

        splashImage.setScale(scale);
        splashImage.setX(stage.getWidth() / 2 - (scale * splashImage.getWidth()) / 2);

        loadingProgress.setSize(scale * splashImage.getWidth() * 0.7f, stage.getHeight() * 0.07f);
        loadingProgress.setX(splashImage.getX() + scale * splashImage.getWidth() * 0.15f);
        loadingProgress.setY(stage.getHeight()*0.2f);
        loadingProgress.setAnimateDuration(0.01f);

        stage.addActor(splashImage);
        stage.addActor(loadingProgress);

        //splashImage.setX(stage.getWidth() / 2 - width / 2);
        splashImage.addAction(Actions.sequence(Actions.alpha(0)
                , Actions.fadeIn(0.75f), Actions.delay(1.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                animationDone = true;
            }
        })));
        // Queue loading will load 2 sets of assets:
        // internal assets: images and hit sounds
        // external assets: beatmaps
        // while processing external assets, it may also install .osz beatmaps
        // or convert .osu files, which is why after the first pass is completed,
        // we update the set of loaded maps to include the recently extracted maps
        // if nothing was installed or changed, the second phase won't last long.
        Assets.queueLoading();
        GlobalConfiguration.loadConfiguration();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        GlobalConfiguration.storeConfiguration();
        texture.dispose();
        stage.dispose();
    }

}