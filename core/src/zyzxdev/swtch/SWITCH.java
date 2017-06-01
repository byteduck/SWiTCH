package zyzxdev.swtch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import aurelienribon.tweenengine.Tween;
import zyzxdev.swtch.screen.GameOverScreen;
import zyzxdev.swtch.screen.MainMenuScreen;
import zyzxdev.swtch.util.ColorAccessor;
import zyzxdev.swtch.util.RectangleAccessor;
import zyzxdev.swtch.util.Vector3Accessor;

public class SWITCH extends Game {
	public static float WIDTH, HEIGHT;

	public SpriteBatch batch;
	public ShapeRenderer shape;
	public BitmapFont mainMenuRobotoLight, gamemodeRobotoLight, smallRobotoLight;
	public Preferences prefs;

	@Override
	public void create () {
		WIDTH = Gdx.graphics.getWidth()/Gdx.graphics.getDensity();
		HEIGHT = Gdx.graphics.getHeight()/Gdx.graphics.getDensity();

		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		shape.setAutoShapeType(true);

		/* TEXTURES */
		zyzxdev.swtch.util.Assets.addTexture("switchLogoText", "switchlogotext.png");
		zyzxdev.swtch.util.Assets.addTexture("square", "square.png");
		zyzxdev.swtch.util.Assets.addTexture("squarePlay", "square-play.png");
		zyzxdev.swtch.util.Assets.addTexture("square3x3", "3x3.png");
		zyzxdev.swtch.util.Assets.addTexture("square4x4", "4x4.png");
		zyzxdev.swtch.util.Assets.addTexture("timed", "timed.png");
		zyzxdev.swtch.util.Assets.addTexture("casual", "casual.png");
		zyzxdev.swtch.util.Assets.addTexture("trophy", "trophy.png");
		zyzxdev.swtch.util.Assets.addTexture("trophyHighScore", "trophy-highscore.png");

		Tween.registerAccessor(Vector2.class, new zyzxdev.swtch.util.Vector2Accessor());
		Tween.registerAccessor(Vector3.class, new Vector3Accessor());
		Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
		Tween.registerAccessor(Color.class, new ColorAccessor());

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Light.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

		parameter.size = Gdx.graphics.getWidth()/7;
		parameter.color = Color.WHITE;
		mainMenuRobotoLight = generator.generateFont(parameter);

		parameter.size = Gdx.graphics.getWidth()/7;
		gamemodeRobotoLight = generator.generateFont(parameter);

		parameter.size = Gdx.graphics.getWidth()/10;
		smallRobotoLight = generator.generateFont(parameter);

		prefs = Gdx.app.getPreferences("prefs");

		generator.dispose();

		Tween.setCombinedAttributesLimit(4);

		setScreen(new MainMenuScreen(this));
	}
}
