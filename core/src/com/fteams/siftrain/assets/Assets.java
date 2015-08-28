package com.fteams.siftrain.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.entities.SimpleSong;
import com.fteams.siftrain.entities.SimpleSongGroup;
import com.fteams.siftrain.entities.SongFileInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assets {

    public static AssetManager internalManager = new AssetManager(new InternalFileHandleResolver());
    public static AssetManager externalManager = new AssetManager(new ExternalFileHandleResolver());

    static {
        externalManager.setLoader(List.class, new SimplifiedBeatmapLoader(new ExternalFileHandleResolver()));
    }

    public static SimpleSong selectedSong;
    public static SongFileInfo selectedBeatmap;
    public static SimpleSongGroup selectedGroup;

    public static TextureAtlas atlas;

    public static Skin menuSkin;
    public static Sound badSound;
    public static Sound goodSound;
    public static Sound greatSound;
    public static Sound perfectSound;

    public static BitmapFont font;

    public static Texture mainMenuBackgroundTexture;
    public static Texture holdBG;

    public static Array<SimpleSongGroup> songGroup;

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
        internalManager.load("fonts/song-font.fnt", BitmapFont.class);
        reloadBeatmaps();
    }

    // thanks to libgdx, the manager will not actually load maps which were already loaded,
    // so if the same file comes again, it will be skipped
    public static void reloadBeatmaps() {
        if (Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "beatmaps/datafiles").exists()) {
            for (String fileName : Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "beatmaps/datafiles").file().list()) {
                String fullPath = Gdx.files.getExternalStoragePath() + "beatmaps/datafiles/" + fileName;
                // if for any reason the user placed .osu/.osz files in the datafiles, we process them
                if (Gdx.files.absolute(fullPath).isDirectory() || (!fileName.endsWith(".rs") && !fileName.endsWith(".osz") && !fileName.endsWith(".osu")))
                    continue;

                externalManager.load("beatmaps/datafiles/" + fileName, List.class);
            }
            // process osu files from the beatmaps folder
            for (String fileName : Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "beatmaps/").file().list()) {
                String fullPath = Gdx.files.getExternalStoragePath() + "beatmaps/" + fileName;
                if (Gdx.files.absolute(fullPath).isDirectory() || (!fileName.endsWith(".osz") && !fileName.endsWith(".osu")))
                    continue;
                externalManager.load("beatmaps/" + fileName, List.class);
            }
        } else {
            (Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "beatmaps")).mkdirs();
            (Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "beatmaps/datafiles")).mkdirs();
            (Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "beatmaps/soundfiles")).mkdirs();
        }
    }

    // unlike the simple reload, in the hard reload we unload everything from the external manager
    // and force a reload of the beatmaps - this will cause .osz files which weren't extracted
    // to be processed, .osu files to be converted and music files within the .osz packages
    // to be copied over to the /beatmaps/soundfiles/ folder.
    public static void hardReloadBeatmaps() {
        selectedSong = null;
        selectedBeatmap = null;
        selectedGroup = null;
        externalManager.clear();
        reloadBeatmaps();
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

    @SuppressWarnings("unchecked")
    public static void setSongs() {
        if (songGroup == null) {
            songGroup = new Array<>();
        }
        else{
            songGroup.clear();
        }

        Array<String> assets = externalManager.getAssetNames();
        Map<String, SimpleSongGroup> groupMap = new HashMap<>();

        for (String string : assets) {
            List<SongFileInfo> beatmaps = externalManager.get(string, List.class);
            if (!beatmaps.isEmpty())
            {
                String resourceName = beatmaps.get(0).getResourceName();
                String musicFile = beatmaps.get(0).music_file;
                String songName = beatmaps.get(0).song_name;
                if (groupMap.get(resourceName) == null)
                {
                    SimpleSongGroup group = new SimpleSongGroup();
                    group.resource_name = resourceName;
                    group.music_file = musicFile;
                    group.song_name = songName;
                    group.songs = new Array<>();
                    groupMap.put(resourceName, group);
                }
                SimpleSongGroup group = groupMap.get(resourceName);
                for (SongFileInfo beatmap : beatmaps) {
                    group.songs.add(beatmap);
                }
                group.songs.sort();
            }
        }
        for (String key: groupMap.keySet()) {
            songGroup.add(groupMap.get(key));
        }
        songGroup.sort();
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
