package com.fteams.siftrain;

import com.badlogic.gdx.Game;
import com.fteams.siftrain.screens.SplashScreen;

public class SifTrain extends Game {
    @Override
    public void create() {
        setScreen(new SplashScreen());
    }
}
