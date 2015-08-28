package com.fteams.siftrain.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.fteams.siftrain.World;
import com.fteams.siftrain.assets.Assets;
import com.fteams.siftrain.assets.GlobalConfiguration;
import com.fteams.siftrain.objects.AccuracyMarker;
import com.fteams.siftrain.objects.AccuracyPopup;
import com.fteams.siftrain.objects.CircleMark;
import com.fteams.siftrain.objects.ScoreDiffMarker;
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

    TextureRegion scoreMarker;
    TextureRegion cRankKnob;
    TextureRegion bRankKnob;
    TextureRegion aRankKnob;
    TextureRegion sRankKnob;

    TextureRegion missMark;
    TextureRegion badLateMark;
    TextureRegion badSoonMark;
    TextureRegion goodLateMark;
    TextureRegion goodSoonMark;
    TextureRegion greatLateMark;
    TextureRegion greatSoonMark;
    TextureRegion perfectMark;

    BitmapFont font;

    GlyphLayout layout;

    short[] triangles = {0, 1, 2, 0, 2, 3};

    // extra stuff
    private PolygonSpriteBatch spriteBatch;
    //private PolygonSpriteBatch polygonSpriteBatch;

    private ShapeRenderer renderer;

    private int width;
    private int height;
    private int positionOffsetX;
    private int positionOffsetY;
    // pixels per unit on X
    public float ppuX;
    // pixels per unit on Y
    public float ppuY;

    private float time;

    public void setSize(int w, int h, int offsetX, int offsetY) {
        this.width = w;
        this.height = h;
        this.positionOffsetX = offsetX;
        this.positionOffsetY = offsetY;
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

        scoreMarker = atlas.findRegion("score_marker");
        cRankKnob = atlas.findRegion("c_marker");
        bRankKnob = atlas.findRegion("b_marker");
        aRankKnob = atlas.findRegion("a_marker");
        sRankKnob = atlas.findRegion("s_marker");

        missMark = atlas.findRegion("miss");
        badLateMark = atlas.findRegion("bad_late");
        badSoonMark = atlas.findRegion("bad_soon");
        goodLateMark = atlas.findRegion("good_late");
        goodSoonMark = atlas.findRegion("good_soon");
        greatLateMark = atlas.findRegion("great_late");
        greatSoonMark = atlas.findRegion("great_soon");
        perfectMark = atlas.findRegion("perfect");

        font = Assets.font;
    }

    public void render() {
        spriteBatch.begin();
        renderer.setProjectionMatrix(cam.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        drawTapZones();
        drawCircles();
        drawScore();
        drawCombo();
        drawProgressBar();
        drawAccuracyBar();
        if (!world.started) {
            drawTapToBeginMessage();
        }
        if (!world.paused) {
            drawAccuracy();
        }
        if (world.paused) {
            drawTapToContinue();
        }
        renderer.end();
        spriteBatch.end();
        time += Gdx.graphics.getDeltaTime();
    }

    private void drawAccuracyBar() {
        float centerX = this.positionOffsetX + width / 6;
        float y = this.positionOffsetY + height - height * 0.1f;
        float zone = (float) (Assets.selectedSong.song_info.get(0).notes_speed / 2);

        // draw the background (bad level)
        spriteBatch.draw(accBadBackground, centerX - width / 6f, y, width / 3f, height * 0.01f);
        // draw the background (good level)
        spriteBatch.draw(accGoodBackground, centerX - 0.4f * width / 6f, y, 0.4f * width / 3f, height * 0.01f);
        // draw the background (great level)
        spriteBatch.draw(accGreatBackground, centerX - 0.3f * width / 6f, y, 0.3f * width / 3f, height * 0.01f);
        // draw the background (perfect level)
        spriteBatch.draw(accPerfectBackground, centerX - 0.1f * width / 6f, y, 0.1f * width / 3f, height * 0.01f);
        // draw each of the 'markers'
        for (AccuracyMarker accMarker : world.getAccuracyMarkers()) {
            if (accMarker.display){

                spriteBatch.setColor(1, 1, 1, accMarker.getAlpha());
                spriteBatch.draw(accHitMark, centerX + (accMarker.getTime()) * (width / 6) / zone - accHitMark.getRegionWidth(), y - height * 0.01f, 3f, height * 0.03f);
            }
        }
        spriteBatch.setColor(1, 1, 1, 1);
    }

    private void drawTapToBeginMessage() {
        String tapToBegin = "Tap to begin!";
        float centerX = this.positionOffsetX + width / 2;
        float centerY = this.positionOffsetY + height / 2 + height * 0.15f;
        layout.setText(font, tapToBegin);
        font.draw(spriteBatch, tapToBegin, centerX - layout.width / 2, centerY - layout.height / 2);
    }

    private void drawTapToContinue() {
        String tapToBegin = "Tap to continue!";
        float centerX = this.positionOffsetX + width / 2;
        float centerY = this.positionOffsetY + height / 2 + height * 0.15f;
        layout.setText(font, tapToBegin);
        font.draw(spriteBatch, tapToBegin, centerX - layout.width / 2, centerY - layout.height / 2);

        String backToExit = "Or press back again to skip to the Results screen.";
        centerX = this.positionOffsetX + width / 2;
        centerY = this.positionOffsetY + height / 2 + height * 0.1f;
        layout.setText(font, backToExit);
        font.draw(spriteBatch, backToExit, centerX - layout.width / 2, centerY - layout.height / 2);
    }


    private void drawProgressBar() {
        float centerX = this.positionOffsetX;
        float centerY = this.positionOffsetY + height - height * 0.07f;
        float progress = (1.0f * world.score) / (world.sScore * 1.0f);
        if (progress >= 1f)
            progress = 1f;

        spriteBatch.draw(selectTextureForProgressBar(), centerX, centerY + height * 0.035f, progress * width, height * 0.025f);
        // S rank marker
        spriteBatch.draw(sRankKnob, this.positionOffsetX + width - height * 0.035f / 2, centerY, height * 0.035f, height * 0.035f);
        // A rank marker
        spriteBatch.draw(aRankKnob, this.positionOffsetX + (1.0f * world.aScore) / (world.sScore * 1.0f) * width - height * 0.035f / 2, centerY, height * 0.035f, height * 0.035f);
        // B rank marker
        spriteBatch.draw(bRankKnob, this.positionOffsetX + (1.0f * world.bScore) / (world.sScore * 1.0f) * width - height * 0.035f / 2, centerY, height * 0.035f, height * 0.035f);
        // C rank marker
        spriteBatch.draw(cRankKnob, this.positionOffsetX + (1.0f * world.cScore) / (world.sScore * 1.0f) * width - height * 0.035f / 2, centerY, height * 0.035f, height * 0.035f);
        // No rank marker -> C -- Nozomi
        if (world.score < world.sScore) {
            progress = (1.0f * world.score) / (world.sScore * 1.0f);
            spriteBatch.draw(scoreMarker, this.positionOffsetX + progress * width - height * 0.035f / 2, centerY, height * 0.035f, height * 0.035f);
        }

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
        float scale = height / GlobalConfiguration.BASE_HEIGHT;
        float centerX = this.positionOffsetX + width / 2;
        float centerY = this.positionOffsetY + height / 2 + height * 0.15f;
        for (AccuracyPopup popup : world.getAccuracyPopups()) {
            if (popup.show) {
                TextureRegion region = perfectMark;
                if (popup.accuracy == CircleMark.Accuracy.MISS) {
                    region = missMark;
                }
                if (popup.accuracy == CircleMark.Accuracy.BAD) {
                    region = popup.soon ? badSoonMark : badLateMark;
                }
                if (popup.accuracy == CircleMark.Accuracy.GOOD) {
                    region = popup.soon ? goodSoonMark : goodLateMark;
                }
                if (popup.accuracy == CircleMark.Accuracy.GREAT) {
                    region = popup.soon ? greatSoonMark : greatLateMark;
                }
                spriteBatch.setColor(1, 1, 1, popup.getAlpha());
                spriteBatch.draw(region, centerX - scale * region.getRegionWidth() * popup.getSize() / 2, centerY - scale * region.getRegionHeight() * popup.getSize() / 2, scale * region.getRegionWidth() * popup.getSize(), scale * region.getRegionHeight() * popup.getSize());
            }
        }
        spriteBatch.setColor(1, 1, 1, 1);
    }

    private void drawCombo() {
        float centerX = this.positionOffsetX + width / 2;
        float centerY = height / 2;
        if (world.combo != 0) {
            layout.setText(font, "" + world.combo);
            font.draw(spriteBatch, "" + world.combo, centerX - layout.width / 2, centerY - layout.height / 2);
        }
    }

    private void drawScore() {
        float centerX = this.positionOffsetX + width / 2;
        float centerY = height - height * 0.15f;
        String text = world.score + "";
        layout.setText(font, text);
        float width = layout.width;
        float height = layout.height;
        font.draw(spriteBatch, text, centerX - width / 2, centerY - height / 2);
        for (ScoreDiffMarker marker : world.getScoreMarkers()) {
            if (!marker.display)
                continue;

            String markerValue = "(+" + marker.value + ")";
            layout.setText(font, markerValue);

            font.draw(spriteBatch, markerValue, centerX + (marker.left ? -width / 2 - layout.width - layout.width / 2 : width / 2 + layout.width / 2), centerY - height / 2);
        }

    }

    private void drawTapZones() {
        float centerX = this.positionOffsetX + width / 2;
        float centerY = this.positionOffsetY + height - height * 0.25f;
        float size = height * 0.2f;
        for (TapZone tapZone : world.getZones()) {

            TextureRegion region = tapZoneIdle;

            if (tapZone.getState(TapZone.State.STATE_WARN)) {
                region = tapZoneWarn;
            }

            if (tapZone.getState(TapZone.State.STATE_PRESSED)) {
                tapZone.touchTime = time;
            }

            final float x = centerX + tapZone.getPosition().x * ppuX - size / 2;
            final float y = centerY + tapZone.getPosition().y * ppuY - size / 2;
            spriteBatch.draw(region, x, y, size, size);

            float alpha = 1f - MathUtils.clamp((time - tapZone.touchTime) * 5f, 0f, 1f);
            if(alpha > 0) {
                Color c = spriteBatch.getColor();
                spriteBatch.setColor(c.r, c.g, c.b, Interpolation.pow2In.apply(alpha));
                spriteBatch.draw(tapZonePressed, x, y, size, size);
                spriteBatch.setColor(c);
            }
        }
    }

    private void drawCircles() {
        float centerX = this.positionOffsetX + width / 2;
        float centerY = this.positionOffsetY + height - height * 0.25f;
        float size = height * 0.2f;
        Color c = null;

        for (CircleMark mark : world.getMarks()) {
            if(!mark.visible && !mark.hold)
                continue;

            float alpha = mark.alpha;
            if(alpha < 1f) {
                c = spriteBatch.getColor();
                spriteBatch.setColor(c.r, c.g, c.b, alpha);
            }

            if (mark.hold) {
                if (mark.waiting) {
                    float[] points = {
                            (float) (mark.getHoldReleasePosition().x * ppuX - mark.getSize2() * size / 2 * Math.sin(mark.destination * Math.PI / 8)),
                            /*centerY +*/ (float) (mark.getHoldReleasePosition().y * ppuY - mark.getSize2() * size / 2 * Math.cos(mark.destination * Math.PI / 8)),
                            (float) (mark.getPosition().x * ppuX - mark.getSize() * size / 2 * Math.sin(mark.destination * Math.PI / 8)),
                            /*centerY +*/ (float) (mark.getPosition().y * ppuY - mark.getSize() * size / 2 * Math.cos(mark.destination * Math.PI / 8)),
                            (float) (mark.getPosition().x * ppuX + mark.getSize() * size / 2 * Math.sin(mark.destination * Math.PI / 8)),
                            /*centerY +*/ (float) (mark.getPosition().y * ppuY + mark.getSize() * size / 2 * Math.cos(mark.destination * Math.PI / 8)),
                            (float) (mark.getHoldReleasePosition().x * ppuX + mark.getSize2() * size / 2 * Math.sin(mark.destination * Math.PI / 8)),
                            /*centerY +*/  (float) (mark.getHoldReleasePosition().y * ppuY + mark.getSize2() * size / 2 * Math.cos(mark.destination * Math.PI / 8))
                    };
                    spriteBatch.draw(new PolygonRegion(mark.holding ? holdBGHolding : holdBG, points, triangles), centerX, centerY);
                }
                if (mark.endVisible) {
                    TextureRegion region = circleHoldEnd;
                    spriteBatch.draw(region, centerX - size * mark.getSize2() / 2 + mark.getHoldReleasePosition().x * ppuX, centerY - size * mark.getSize2() / 2 + mark.getHoldReleasePosition().y * ppuY, size * mark.getSize2(), size * mark.getSize2());
                }
                // coordinates for the beam start and end
            }

            if (mark.visible) {
                int effectMask = mark.getEffectMask();

                spriteBatch.draw(selectTextureForCircle(effectMask), centerX - size * mark.getSize() / 2 + mark.getPosition().x * ppuX, centerY - size * mark.getSize() / 2 + mark.getPosition().y * ppuY, size * mark.getSize(), size * mark.getSize());

            }

            if(alpha < 1f) {
                spriteBatch.setColor(c);
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
