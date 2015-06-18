package com.fteams.siftrain.desktop;


import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TextureSetup {

    public static void main(String[] args) {
        TexturePacker.process("images/", "textures/", "textures.pack");
    }
}

