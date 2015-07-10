package com.fteams.siftrain.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.fteams.siftrain.entities.SimpleNotesInfo;
import com.fteams.siftrain.entities.SimpleRankInfo;
import com.fteams.siftrain.entities.SimpleSong;
import com.fteams.siftrain.entities.SimpleSongInfo;
import com.fteams.siftrain.entities.SongFileInfo;
import com.fteams.siftrain.util.SongUtils;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SimplifiedBeatmapLoader extends AsynchronousAssetLoader<List, SimplifiedBeatmapLoader.BeatmapParameter> {
    private List<SongFileInfo> beatmaps;

    public SimplifiedBeatmapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, BeatmapParameter parameter) {
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, BeatmapParameter parameter) {
        beatmaps = new ArrayList<>();
        if (fileName.endsWith(".rs")) {
            loadAsyncStandard(manager, fileName, file, parameter);
        } else if (fileName.endsWith(".osz")) {
            installOsuFiles(manager, fileName, file, parameter);
        } else if (fileName.endsWith(".osu")) {
            convertOsuBeatmap(manager, fileName, file, parameter);
        }
    }

    private void loadAsyncStandard(AssetManager manager, String fileName, FileHandle file, BeatmapParameter parameter) {

        FileHandle handle = resolve(fileName);
        String jsonDefinition = handle.readString("UTF-8");
        SongFileInfo info = new SongFileInfo();
        try {
            info = new Gson().fromJson(jsonDefinition, SimpleSong.class);
        } catch (Exception e) {
            info = new SimpleSong();
            info.song_name = "Error: Invalid JSON format " + handle.file().getName();
            info.difficulty = 1;
        } finally {
            info.setFileName(fileName);
            info.setResourceName(handle.nameWithoutExtension().replaceAll("_(easy|normal|hard|expert)$", ""));
            beatmaps.add(info);
        }
    }

    private void installOsuFiles(AssetManager manager, String fileName, FileHandle file, BeatmapParameter parameter) {
        try {
            Gdx.app.log("OSZ_LOADER", "installing map: " + fileName);
            FileHandle handle = resolve(fileName);
            ZipFile osuZipFile = new ZipFile(handle.file());
            Enumeration<? extends ZipEntry> entries = osuZipFile.entries();
            List<SimpleSong> songs = new ArrayList<>();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // get only osu files
                if (entry.getName().endsWith(".osu")) {
                    try {
                        // check if the beatmap already exists - was extracted before
                        // if the osz file was found in the root directory, we go down to datafiles
                        FileHandle[] files;
                        if (handle.parent().name().equals("beatmaps")) {
                            files = handle.parent().child("datafiles").list(".rs");
                        }
                        // if the osz file was in the datafiles directory
                        else {
                            files = handle.parent().list(".rs");
                        }

                        boolean found = false;
                        // check if the entries already exist
                        for (FileHandle fh : files) {
                            if (fh.name().equals(entry.getName().replace(".osu", ".rs"))) {
                                found = true;
                                break;
                            }
                        }

                        if (found) {
                            Gdx.app.log("OSZ_LOADER", "Entry: [" + entry.getName().replace(".osu", ".rs") + "] exists, skipping.");
                            continue;
                        }

                        SimpleSong beatmap = processOsuStandardFile(osuZipFile.getInputStream(entry));
                        songs.add(beatmap);
                        storeBeatmap(beatmap, "beatmaps/datafiles/" + entry.getName().replace(".osu", ".rs"));
                    } catch (Exception e) {
                        // attempted to load a non-mania map, ignore it
                        Gdx.app.log("OSZ_LOADER", "Attempted to load a non-mania map: " + entry.getName());
                    }
                }
            }

            for (SimpleSong song : songs) {
                String songFile = song.music_file;
                storeMusicFile(osuZipFile.getInputStream(osuZipFile.getEntry(songFile)), songFile);
            }
            // don't load files from within an osu archive, just install them
            songs.clear();
        } catch (IOException e) {
            // something happened while loading the file
            // encrypted zip, or corrupted file
            // just exit
        }
    }

    private void convertOsuBeatmap(AssetManager manager, String fileName, FileHandle file, BeatmapParameter parameter) {
        try {
            Gdx.app.log("OSU_LOADER", "converting map: " + fileName);
            FileHandle handle = resolve(fileName);

            FileHandle[] files;
            // if the osu file was in the root 'beatmaps' directory, go down to the datafiles directory
            if (handle.parent().name().equals("beatmaps")) {
                files = handle.parent().child("datafiles").list(".rs");
            }
            // if the osz file was in the datafiles directory
            else {
                files = handle.parent().list(".rs");
            }
            boolean found = false;
            for (FileHandle fh : files) {
                if (fh.name().equals(handle.name().replace(".osu", ".rs"))) {
                    found = true;
                    break;
                }
            }
            // don't store files which were already converted.
            // if someone wants a hard reload, they can remove the .rs file
            // and have the game re-generate them
            if (found) {
                Gdx.app.log("OSU_LOADER", "Map [" + handle.nameWithoutExtension() + ".rs] already exists, skipping.");
                return;
            }

            InputStream is = new FileInputStream(handle.file());
            SimpleSong beatmap = processOsuStandardFile(is);
            storeBeatmap(beatmap, "beatmaps/datafiles/" + fileName.split("\\\\|/", 2)[1].replace(".osu", ".rs"));
        } catch (Exception e) {
            // something happened while loading the file
            // just exit
            Gdx.app.log("OSZ_LOADER", "Attempted to load a non-mania map: " + fileName);
        }
    }

    private void storeMusicFile(InputStream inputStream, String fileName) {
        File output = new File(Gdx.files.getExternalStoragePath() + "/beatmaps/soundfiles/" + fileName);
        if (output.exists()) {
            Gdx.app.log("OSZ_MUSIC_LOADER", "File [" + fileName + "] already exists, skipping.");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(output);
            // tried bigger blocks, didn't really get better performance.
            int BLOCK_SIZE = 8192;
            byte[] buffer = new byte[BLOCK_SIZE];
            int len = inputStream.read(buffer, 0, BLOCK_SIZE);
            while (len != -1) {
                fos.write(buffer, 0, len);
                len = inputStream.read(buffer, 0, BLOCK_SIZE);
            }
            inputStream.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeBeatmap(SimpleSong beatmap, String fileName) {
        File output = new File(Gdx.files.getExternalStoragePath() + "/" + fileName);
        String json = new Gson().toJson(beatmap);
        try {
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(json.getBytes("UTF-8"));
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SimpleSong processOsuStandardFile(InputStream entry) throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(entry);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String contents = reader.readLine();
        SimpleSong song = new SimpleSong();
        song.difficulty = 4;
        Integer mode = 0;
        SimpleSongInfo songInfo = new SimpleSongInfo();
        song.song_info = new ArrayList<>();
        song.song_info.add(songInfo);
        songInfo.notes = new ArrayList<>();
        while (contents != null) {
            if (contents.startsWith("//")) {
                contents = reader.readLine();
                continue;
            }
            if (contents.startsWith("AudioFilename:")) {
                song.music_file = contents.split(":", 2)[1].trim();
            }
            if (contents.startsWith("AudioLeadIn:")) {
                song.lead_in = Integer.parseInt(contents.split(":", 2)[1].trim()) / 1000f;
            }
            if (contents.startsWith("Title:")) {
                song.song_name = contents.split(":", 2)[1].trim();
            }
            // mania = mode 3
            if (contents.startsWith("Mode:")) {
                if (Integer.parseInt(contents.split(":", 2)[1].trim()) != 3) {
                    throw new RuntimeException("Invalid beatmap mode.");
                }
            }
            if (contents.startsWith("Version:")) {
                song.difficulty_name = contents.split(":", 2)[1].trim();
            }
            if (contents.startsWith("CircleSize:")) {
                mode = Integer.parseInt(contents.split(":", 2)[1].trim());
            }
            if (contents.startsWith("ApproachRate:")) {
                songInfo.notes_speed = SongUtils.getDefaultNoteSpeedForApproachRate(Integer.parseInt(contents.split(":", 2)[1].trim()));
            }

            if (contents.equals("[HitObjects]")) {
                // convert notes into CircleMarks
                contents = reader.readLine();
                // format:
                while (contents != null && !contents.startsWith("[")) {
                    if (contents.startsWith("//")) {
                        contents = reader.readLine();
                        continue;
                    }
                    // x,y,timing,flag,0,release:a:b:c:d
                    String[] theLine = contents.split(",", 6);
                    SimpleNotesInfo notesInfo = new SimpleNotesInfo();
                    Integer band = Integer.parseInt(theLine[0].trim()) / (512 / mode);
                    notesInfo.position = SongUtils.getPositionForMode(mode, band);
                    notesInfo.timing_sec = Integer.parseInt(theLine[2].trim()) / 1000.0;
                    notesInfo.effect = SongUtils.NOTE_TYPE_NORMAL;
                    if ((Integer.parseInt(theLine[3].trim()) & 128) != 0) {
                        notesInfo.effect_value = Integer.parseInt(theLine[5].trim().split(":", 2)[0].trim()) / 1000f - notesInfo.timing_sec;
                        notesInfo.effect = SongUtils.NOTE_TYPE_HOLD;
                    } else {
                        notesInfo.effect_value = 2.0;
                    }
                    songInfo.notes.add(notesInfo);

                    contents = reader.readLine();
                }
            }
            contents = reader.readLine();
        }
        processEffects(song);
        setRankInfo(song);
        return song;
    }

    // if the game converts the maps, we set the rank tiers to prevent warning pop-ups for the user.
    private void setRankInfo(SimpleSong song) {
        song.rank_info = new ArrayList<>();
        song.rank_info.add(new SimpleRankInfo(SongUtils.getCScoreForSong(song.song_info.get(0).notes.size(), song.difficulty)));
        song.rank_info.add(new SimpleRankInfo(SongUtils.getBScoreForSong(song.song_info.get(0).notes.size(), song.difficulty)));
        song.rank_info.add(new SimpleRankInfo(SongUtils.getAScoreForSong(song.song_info.get(0).notes.size(), song.difficulty)));
        song.rank_info.add(new SimpleRankInfo(SongUtils.getSScoreForSong(song.song_info.get(0).notes.size(), song.difficulty)));
        song.rank_info.add(new SimpleRankInfo(0));
    }

    // since we're creating the maps, we need to make sure that notes which land at the same time are tagged as simultaneous
    private void processEffects(SimpleSong song) {
        SimpleSongInfo info = song.song_info.get(0);
        for (SimpleNotesInfo note : info.notes) {
            for (SimpleNotesInfo note2 : info.notes) {
                if (note2.timing_sec.equals(note.timing_sec) && !note.equals(note2)) {
                    note.effect = note.effect | SongUtils.NOTE_TYPE_SIMULT_START;
                    // todo: if the note is a hold, check if the release time matches the timing or release timing of another note as well.
                    break;
                }
            }
        }
    }

    @Override
    public List<SongFileInfo> loadSync(AssetManager manager, String fileName, FileHandle file, BeatmapParameter parameter) {
        return beatmaps;
    }

    public class BeatmapParameter extends AssetLoaderParameters<List> {
    }
}
