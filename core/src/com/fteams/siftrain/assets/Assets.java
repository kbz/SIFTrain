package com.fteams.siftrain.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.entities.SimpleSong;

public class Assets {

    public static AssetManager internalManager = new AssetManager(new InternalFileHandleResolver());
    public static AssetManager externalManager = new AssetManager(new ExternalFileHandleResolver());

    static {
        externalManager.setLoader(SimpleSong.class, new SimpleSongLoader(new ExternalFileHandleResolver()));
    }

    public static SimpleSong selectedSong;
    public static TextureAtlas atlas;

    public static Skin menuSkin;
    public static Sound badSound;
    public static Sound goodSound;
    public static Sound greatSound;
    public static Sound perfectSound;

    public static BitmapFont font;

    public static Texture mainMenuBackgroundTexture;
    public static Texture holdBG;
    public static Texture holdBGHolding;

    public static Array<SimpleSong> songList;

    // In here we'll put everything that needs to be loaded in this format:
    // manager.load("file location in assets", fileType.class);
    //
    // libGDX AssetManager currently supports: Pixmap, Texture, BitmapFont,
    //     TextureAtlas, TiledAtlas, TiledMapRenderer, Music and Sound.
    public static void queueLoading() {
        internalManager.load("textures/textures.pack.atlas", TextureAtlas.class);
        internalManager.load("hitsounds/bad.mp3", Sound.class);
        internalManager.load("hitsounds/good.mp3", Sound.class);
        internalManager.load("hitsounds/great.mp3", Sound.class);
        internalManager.load("hitsounds/perfect.mp3", Sound.class);
        internalManager.load("bigimages/main_menu_background.jpg", Texture.class);
        internalManager.load("images/hold_background.png", Texture.class);
        internalManager.load("images/hold_background_holding.png", Texture.class);
        internalManager.load("fonts/song-font.fnt", BitmapFont.class);
        if (Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "beatmaps/datafiles").exists()) {
            for (String fileName : Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "beatmaps/datafiles").file().list()) {
                externalManager.load("beatmaps/datafiles/" + fileName, SimpleSong.class);
            }
        } else {
            (Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "beatmaps")).mkdirs();
            (Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "beatmaps/datafiles")).mkdirs();
            (Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "beatmaps/soundfiles")).mkdirs();
        }
    }

    //In here we'll create our skin, so we only have to create it once.
    public static void setMenuSkin() {
        if (menuSkin == null)
            menuSkin = new Skin(Gdx.files.internal("skins/menuSkin.json"), internalManager.get("textures/textures.pack.atlas", TextureAtlas.class));
    }

    public static void setTextures() {
        if (atlas == null)
            atlas = internalManager.get("textures/textures.pack.atlas");

        if (mainMenuBackgroundTexture == null)
            mainMenuBackgroundTexture = internalManager.get("bigimages/main_menu_background.jpg");

        if (holdBG == null)
            holdBG = internalManager.get("images/hold_background.png");

        if (holdBGHolding == null)
            holdBGHolding = internalManager.get("images/hold_background_holding.png");
    }

    public static void setFonts() {
        if (font == null)
            font = internalManager.get("fonts/song-font.fnt");

    }

    public static void setHitsounds() {
        if (badSound == null)
            badSound = internalManager.get("hitsounds/bad.mp3");
        if (goodSound == null)
            goodSound = internalManager.get("hitsounds/good.mp3");
        if (greatSound == null)
            greatSound = internalManager.get("hitsounds/great.mp3");
        if (perfectSound == null)
            perfectSound = internalManager.get("hitsounds/perfect.mp3");
    }

    public static void setSongs() {
        if (songList == null) {
            songList = new Array<>();
            Array<String> assets = externalManager.getAssetNames();
            for (String string : assets) {
                songList.add(externalManager.get(string, SimpleSong.class));
            }

            songList.sort();
        }
    }

    public static boolean update() {
        return internalManager.update() && externalManager.update();
    }

    public static float getProgress() {
        return (internalManager.getProgress() + externalManager.getProgress()) / 2;
    }

    public static void setSelectedSong(SimpleSong song) {
        selectedSong = song;
    }
}
