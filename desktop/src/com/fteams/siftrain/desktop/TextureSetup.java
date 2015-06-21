package com.fteams.siftrain.desktop;


import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TextureSetup {

    public static void main(String[] args) {
        TexturePacker.Settings config = new TexturePacker.Settings();
        config.maxWidth = 512;
        config.maxHeight = 512;
        TexturePacker.process(config, "images/", "textures/", "textures.pack");
    }
}

