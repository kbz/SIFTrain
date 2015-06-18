package com.fteams.siftrain.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fteams.siftrain.World;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.objects.CircleMark;
import com.fteams.siftrain.objects.TapZone;
import com.fteams.siftrain.util.SongUtils;

public class WorldRenderer {

    private static final float CAMERA_WIDTH = 600f;
    private static final float CAMERA_HEIGHT = 400f;

    private World world;
    private OrthographicCamera cam;

    // textures
    TextureRegion circle;
    TextureRegion circleSim;
    TextureRegion circleHoldStart;
    TextureRegion circleHoldStartSim;
    TextureRegion circleHoldEnd;
    TextureRegion circleHoldEndSim;
    TextureRegion circleSpecial;
    TextureRegion circleSpecialSim;
    TextureRegion circleToken;
    TextureRegion circleTokenSim;

    TextureRegion tapZoneIdle;
    TextureRegion tapZoneWarn;
    TextureRegion tapZonePressed;

    TextureRegion progressBarNoScore;
    TextureRegion progressBarCScore;
    TextureRegion progressBarBScore;
    TextureRegion progressBarAScore;
    TextureRegion progressBarSScore;

    TextureRegion accBadBackground;
    TextureRegion accGoodBackground;
    TextureRegion accGreatBackground;
    TextureRegion accPerfectBackground;

    TextureRegion holdBG;
    TextureRegion holdBGHolding;

    TextureRegion accHitMark;

    TextureRegion rinKnob;
    TextureRegion umiKnob;
    TextureRegion nicoKnob;
    TextureRegion nonTanKnob;
    TextureRegion makiKnob;

    BitmapFont font;

    GlyphLayout layout;

    short[] triangles = {0, 1, 2, 0, 2, 3};

    // extra stuff
    private PolygonSpriteBatch spriteBatch;
    //private PolygonSpriteBatch polygonSpriteBatch;

    private ShapeRenderer renderer;

    private int width;
    private int height;
    // pixels per unit on X
    public float ppuX;
    // pixels per unit on Y
    public float ppuY;

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
        ppuX = (float) width / CAMERA_WIDTH;
        ppuY = (float) height / CAMERA_HEIGHT;
    }

    public WorldRenderer(World world) {
        this.world = world;
        this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        this.cam.position.set(0f, 0f, 0f);
        this.cam.update();
        spriteBatch = new PolygonSpriteBatch();
        renderer = new ShapeRenderer();
        layout = new GlyphLayout();
        loadTextures();
    }

    private void loadTextures() {
        TextureAtlas atlas = Assets.atlas;
        circle = atlas.findRegion("circle");
        circleSim = atlas.findRegion("circle_sim");
        circleHoldStart = atlas.findRegion("circle_hold_press_no_black");
        circleHoldStartSim = atlas.findRegion("circle_hold_press_no_black_sim");
        circleHoldEnd = atlas.findRegion("circle_hold_release_no_black");
        circleHoldEndSim = atlas.findRegion("circle_hold_release_no_black_sim");
        circleSpecial = atlas.findRegion("circle_special");
        circleSpecialSim = atlas.findRegion("circle_special_sim");
        circleToken = atlas.findRegion("circle_token");
        circleTokenSim = atlas.findRegion("circle_token_sim");

        tapZoneIdle = atlas.findRegion("tap");
        tapZonePressed = atlas.findRegion("tap_pressed");
        tapZoneWarn = atlas.findRegion("tap_warn");

        progressBarNoScore = atlas.findRegion("progress_bar_no_score");
        progressBarCScore = atlas.findRegion("progress_bar_c_score");
        progressBarBScore = atlas.findRegion("progress_bar_b_score");
        progressBarAScore = atlas.findRegion("progress_bar_a_score");
        progressBarSScore = atlas.findRegion("progress_bar_s_score");

        accBadBackground = atlas.findRegion("acc_bad");
        accGoodBackground = atlas.findRegion("acc_good");
        accGreatBackground = atlas.findRegion("acc_great");
        accPerfectBackground = atlas.findRegion("acc_perfect");
        accHitMark = atlas.findRegion("acc_mark");

        holdBG = new TextureRegion(Assets.holdBG);
        holdBGHolding = new TextureRegion(Assets.holdBGHolding);

        rinKnob = atlas.findRegion("rin_star");
        umiKnob = atlas.findRegion("umi_star");
        nicoKnob = atlas.findRegion("nico_star");
        nonTanKnob = atlas.findRegion("nozomi_star");
        makiKnob = atlas.findRegion("maki_star");
        font = Assets.font;
    }

    public void render() {
        spriteBatch.begin();
        renderer.setProjectionMatrix(cam.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        if (!world.started) {
            drawTapToBeginMessage();
        }
        if (world.paused) {
            drawTapToContinue();
        }
        drawTapZones();
        drawCircles();
        drawScore();
        drawCombo();
        drawAccuracy();
        drawProgressBar();
        drawAccuracyBar();
        renderer.end();
        spriteBatch.end();
    }

    private void drawAccuracyBar() {
        // draw the background (bad level)
        float centerX = 200;
        float y = height - height * 0.1f;
        float zone = (float) (Assets.selectedSong.song_info[0].notes_speed / 2);
        float offset = GlobalConfiguration.offset / 1000f;

        spriteBatch.draw(accBadBackground, centerX - 200, y, 400f, height * 0.02f);
        // draw the background (good level)
        spriteBatch.draw(accGoodBackground, centerX - 120, y, 240f, height * 0.02f);
        // draw the background (great level)
        spriteBatch.draw(accGreatBackground, centerX - 60, y, 120f, height * 0.02f);
        // draw the background (perfect level)
        spriteBatch.draw(accPerfectBackground, centerX - 28, y, 56f, height * 0.02f);
        // draw each of the 'markers'
        for (Float accMarker : world.getAccuracyMarks()) {
            if (Math.abs(accMarker + offset) > zone)
                continue;

            spriteBatch.draw(accHitMark, centerX - (accMarker + offset) * 200 / zone - accHitMark.getRegionWidth(), y, 1f, height * 0.02f);
        }
    }

    private void drawTapToBeginMessage() {
        String tapToBegin = "Tap to begin!";
        float centerX = width / 2;
        float centerY = height / 2 + height * 0.15f;
        layout.setText(font, tapToBegin);
        font.draw(spriteBatch, tapToBegin, centerX - layout.width / 2, centerY - layout.height / 2);
    }

    private void drawTapToContinue() {
        String tapToBegin = "Tap to continue!";
        float centerX = width / 2;
        float centerY = height / 2 + height * 0.15f;
        layout.setText(font, tapToBegin);
        font.draw(spriteBatch, tapToBegin, centerX - layout.width / 2, centerY - layout.height / 2);
    }


    private void drawProgressBar() {
        float centerX = 0;
        float centerY = height - height * 0.07f;
        float progress = (1.0f * world.score) / (world.sScore * 1.0f);
        if (progress >= 1f)
            progress = 1f;

        spriteBatch.draw(selectTextureForProgressBar(), centerX, centerY + height * 0.035f / 2, progress * width, height * 0.035f);
        // A -> S -- Nico
        if (world.score < world.aScore) {
            progress = (1.0f * world.aScore) / (world.sScore * 1.0f);
        } else {
            progress = (1.0f * (world.score > world.sScore ? world.sScore : world.score)) / (world.sScore * 1.0f);
        }
        spriteBatch.draw(nicoKnob, progress * width - height * 0.07f / 2, centerY, height * 0.07f, height * 0.07f);
        // B -> A -- Umi
        if (world.score < world.bScore) {
            progress = (1.0f * world.bScore) / (world.sScore * 1.0f);
        } else {
            progress = (1.0f * (world.score > world.aScore ? world.aScore : world.score)) / (world.sScore * 1.0f);
        }
        spriteBatch.draw(umiKnob, progress * width - height * 0.07f / 2, centerY, height * 0.07f, height * 0.07f);
        // C - > B -- Rin
        if (world.score < world.cScore) {
            progress = (1.0f * world.cScore) / (world.sScore * 1.0f);
        } else {
            progress = (1.0f * (world.score > world.bScore ? world.bScore : world.score)) / (world.sScore * 1.0f);
        }
        spriteBatch.draw(rinKnob, progress * width - height * 0.07f / 2, centerY, height * 0.07f, height * 0.07f);
        // No -> C -- Nozomi
        if (world.score < world.cScore) {
            progress = (1.0f * world.score) / (world.sScore * 1.0f);
        } else {
            progress = (1.0f * world.cScore) / (world.sScore * 1.0f);
        }
        spriteBatch.draw(nonTanKnob, progress * width - height * 0.07f / 2, centerY, height * 0.07f, height * 0.07f);
    }

    private TextureRegion selectTextureForProgressBar() {
        if (world.score > world.sScore) {
            return progressBarSScore;
        }
        if (world.score > world.aScore) {
            return progressBarAScore;
        }
        if (world.score > world.bScore) {
            return progressBarBScore;
        }
        if (world.score > world.cScore) {
            return progressBarCScore;
        }
        return progressBarNoScore;
    }

    private void drawAccuracy() {
        float centerX = width / 2;
        float centerY = height / 2 + height * 0.15f;
        if (world.accuracy != CircleMark.Accuracy.NONE) {
            layout.setText(font, "" + world.accuracy);
            font.draw(spriteBatch, "" + world.accuracy, centerX - layout.width / 2, centerY - layout.height / 2);
        }
    }

    private void drawCombo() {
        float centerX = width / 2;
        float centerY = height / 2;
        if (world.combo != 0) {
            layout.setText(font, "" + world.combo);
            font.draw(spriteBatch, "" + world.combo, centerX - layout.width / 2, centerY - layout.height / 2);
        }
    }

    private void drawScore() {
        float centerX = width / 2;
        float centerY = height - height * 0.15f;
        String text = world.score + "";
        layout.setText(font, text);
        if (world.getLastBatch() != 0 && !world.processed) {
            text += " (+" + world.getLastBatch() + ")";
        }
        font.draw(spriteBatch, text, centerX - layout.width / 2, centerY - layout.height / 2);

    }


    private void drawTapZones() {
        float centerX = width / 2;
        float centerY = height - height * 0.25f;
        float size = height * 0.2f;
        for (TapZone tapZone : world.getZones()) {

            TextureRegion region = tapZoneIdle;

            if (tapZone.getState(TapZone.State.STATE_WARN)) {
                region = tapZoneWarn;
            }
            if (tapZone.getState(TapZone.State.STATE_PRESSED)) {
                region = tapZonePressed;
            }
            spriteBatch.draw(region, centerX + tapZone.getPosition().x * ppuX - size / 2, centerY + tapZone.getPosition().y * ppuY - size / 2, size, size);
        }
    }

    private void drawCircles() {
        float centerX = width / 2;
        float centerY = height - height * 0.25f;
        float size = height * 0.2f;
        for (CircleMark mark : world.getMarks()) {
            if (mark.isHold()) {
                if (mark.getState(CircleMark.State.WAITING)) {
                    // todo: draw the beam in holds
                    float[] points = {
                            (float) (mark.hookPoint2.x * ppuX - mark.getSize2() * size / 2 * Math.sin(mark.destination * Math.PI / 8)),
                            /*centerY +*/ (float) (mark.hookPoint2.y * ppuY - mark.getSize2() * size / 2 * Math.cos(mark.destination * Math.PI / 8)),
                            (float) (mark.hookPoint.x * ppuX - mark.getSize() * size / 2 * Math.sin(mark.destination * Math.PI / 8)),
                            /*centerY +*/ (float) (mark.hookPoint.y * ppuY - mark.getSize() * size / 2 * Math.cos(mark.destination * Math.PI / 8)),
                            (float) (mark.hookPoint.x * ppuX + mark.getSize() * size / 2 * Math.sin(mark.destination * Math.PI / 8)),
                            /*centerY +*/ (float) (mark.hookPoint.y * ppuY + mark.getSize() * size / 2 * Math.cos(mark.destination * Math.PI / 8)),
                            (float) (mark.hookPoint2.x * ppuX + mark.getSize2() * size / 2 * Math.sin(mark.destination * Math.PI / 8)),
                            /*centerY +*/  (float) (mark.hookPoint2.y * ppuY + mark.getSize2() * size / 2 * Math.cos(mark.destination * Math.PI / 8))
                    };
                    spriteBatch.draw(new PolygonRegion(mark.getState(CircleMark.State.HOLDING) ? holdBGHolding : holdBG, points, triangles), centerX, centerY);
                }
                if (mark.getState(CircleMark.State.END_VISIBLE)) {
                    TextureRegion region = circleHoldEnd;
                    spriteBatch.draw(region, centerX - size * mark.getSize2() / 2 + mark.getHoldReleasePosition().x * ppuX, centerY - size * mark.getSize2() / 2 + mark.getHoldReleasePosition().y * ppuY, size * mark.getSize2(), size * mark.getSize2());
                }
                // coordinates for the beam start and end
            }
            if (mark.getState(CircleMark.State.VISIBLE)) {
                int effectMask = mark.getEffectMask();
                spriteBatch.draw(selectTextureForCircle(effectMask), centerX - size * mark.getSize() / 2 + mark.getPosition().x * ppuX, centerY - size * mark.getSize() / 2 + mark.getPosition().y * ppuY, size * mark.getSize(), size * mark.getSize());
            }
        }

    }

    private TextureRegion selectTextureForCircle(int effectMask) {
        if ((effectMask & (SongUtils.NOTE_TYPE_NORMAL)) != 0) {
            if ((effectMask & (SongUtils.NOTE_TYPE_SIMULT_START | SongUtils.NOTE_TYPE_SIMULT_END)) != 0) {
                return circleSim;
            } else return circle;
        } else if ((effectMask & SongUtils.NOTE_TYPE_SPECIAL) != 0) {
            if ((effectMask & SongUtils.NOTE_TYPE_SIMULT_START) != 0) {
                return circleSpecialSim;
            } else return circleSpecial;
        } else if ((effectMask & SongUtils.NOTE_TYPE_HOLD) != 0) {
            if ((effectMask & SongUtils.NOTE_TYPE_SIMULT_START) != 0) {
                return circleHoldStartSim;
            } else return circleHoldStart;
        } else {
            if ((effectMask & (SongUtils.NOTE_TYPE_SIMULT_START | SongUtils.NOTE_TYPE_SIMULT_END)) != 0) {
                return circleTokenSim;
            } else {
                return circleToken;
            }
        }
    }
}
