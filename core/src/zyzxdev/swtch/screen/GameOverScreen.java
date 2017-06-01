package zyzxdev.swtch.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quart;
import zyzxdev.swtch.SWITCH;
import zyzxdev.swtch.util.Assets;
import zyzxdev.swtch.util.FontLayoutManager;
import zyzxdev.swtch.util.RectangleAccessor;
import zyzxdev.swtch.util.Util;
import zyzxdev.swtch.util.Vector3Accessor;

/**
 * Created by aaron on 5/31/17.
 */

public class GameOverScreen extends ScreenAdapter {
	private SWITCH game;
	private OrthographicCamera guiCam, textCam;
	private Vector3 touchPoint = new Vector3();
	private TweenManager manager;
	private int highScore, score;
	private boolean newHighScore = false;
	private Rectangle trophy;
	private Vector3 textOne, textTwo; //z is opacity
	private String textOneStr, textTwoStr;
	private float iconSize;
	private boolean canTap = false;

	GameOverScreen(SWITCH game, int score, boolean size, boolean mode){
		guiCam = new OrthographicCamera(SWITCH.WIDTH, SWITCH.HEIGHT);
		guiCam.position.set(SWITCH.WIDTH*1.5f, SWITCH.HEIGHT / 2, 0);

		textCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		textCam.position.set(Gdx.graphics.getWidth()*1.5f, Gdx.graphics.getHeight()/2f, 0);

		String settingsKey = "hs"+(size ? "4x4" : "3x3")+(mode ? "comp" : "casual");

		this.game = game;
		this.highScore = game.prefs.getInteger(settingsKey, 0);
		this.score = score;
		iconSize = SWITCH.WIDTH*0.1f;
		textOneStr = score+"";
		textTwoStr = highScore+"";
		GlyphLayout layout = new GlyphLayout(game.mainMenuRobotoLight, textOneStr);
		GlyphLayout layout2 = new GlyphLayout(game.smallRobotoLight, textTwoStr);

		if(score > highScore) {
			newHighScore = true;
			game.prefs.putInteger(settingsKey, score);
			game.prefs.flush();
			textOne = new Vector3(Gdx.graphics.getWidth()/2-layout.width/2, Gdx.graphics.getHeight()/2+layout.height/2, 0);
			iconSize = SWITCH.WIDTH*0.4f;
			trophy = new Rectangle(SWITCH.WIDTH/2-iconSize/2, SWITCH.HEIGHT/2-iconSize/2, iconSize, iconSize);
		}else{
			textOne = new Vector3(Gdx.graphics.getWidth()/2-layout.width/2, Gdx.graphics.getHeight()/2+layout.height/2, 0);
			textTwo = new Vector3(Gdx.graphics.getWidth()*0.05f+iconSize*Gdx.graphics.getDensity()+layout2.width/2, Gdx.graphics.getWidth()*0.05f+layout.height*0.79f, 1);
			trophy = new Rectangle(SWITCH.WIDTH*0.05f, SWITCH.WIDTH*0.05f, iconSize, iconSize);
		}

		manager = new TweenManager();
		Util.slideCamera(guiCam, Util.Direction.CENTER, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.OUT, 0f, null);
		Util.slideCamera(textCam, Util.Direction.CENTER, manager, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Quart.OUT, 0f, new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if(newHighScore){
					Tween.to(trophy, RectangleAccessor.TYPE_XYWH, 1f)
							.target(SWITCH.WIDTH/2-iconSize*0.4f, trophy.y-iconSize*0.4f, iconSize*0.8f, iconSize*0.8f)
							.ease(Quart.OUT)
							.delay(1f)
							.start(manager);
					Tween.to(textOne, Vector3Accessor.TYPE_XYZ, 1f)
							.target(textOne.x, textOne.y+iconSize*0.4f*Gdx.graphics.getDensity(), 1)
							.ease(Quart.OUT)
							.delay(1f)
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									canTap = true;
								}
							})
							.start(manager);
				}else{
					Tween.to(textOne, Vector3Accessor.TYPE_XYZ, 1f)
							.target(textOne.x, textOne.y, 1)
							.ease(Quart.OUT)
							.delay(0.5f)
							.setCallback(new TweenCallback() {
								@Override
								public void onEvent(int type, BaseTween<?> source) {
									canTap = true;
								}
							})
							.start(manager);
				}
			}
		});

	}

	@Override
	public void render(float delta){
		update();
		draw();
	}

	private void update(){
		manager.update(Gdx.graphics.getDeltaTime());

		if(Gdx.input.justTouched() && canTap){
			Util.slideCamera(guiCam, Util.Direction.RIGHT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0f, null);
			Util.slideCamera(textCam, Util.Direction.RIGHT, manager, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Quart.IN, 0f, new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					game.setScreen(new MainMenuScreen(game));
				}
			});
		}
	}

	private void draw(){
		GL20 gl = Gdx.gl;
		gl.glClearColor(0.13333f, 0.13333f, 0.13333f, 1f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		guiCam.update();
		textCam.update();

		game.batch.setProjectionMatrix(textCam.combined);
		game.batch.begin();
		if(textOne != null) {
			float col = 0.13333f+0.86667f*Math.max(Math.min(textOne.z, 1), 0);
			game.mainMenuRobotoLight.setColor(col, col, col, 1);
			game.mainMenuRobotoLight.draw(game.batch, textOneStr, textOne.x, textOne.y);
		}if(textTwo != null) {
			float col = 0.13333f+0.86667f*Math.max(Math.min(textTwo.z, 1), 0);
			game.smallRobotoLight.setColor(col, col, col, 1);
			game.smallRobotoLight.draw(game.batch, textTwoStr, textTwo.x, textTwo.y);
		}
		game.mainMenuRobotoLight.setColor(1, 1, 1, 1);
		game.batch.end();

		game.batch.setProjectionMatrix(guiCam.combined);
		game.batch.begin();
		game.batch.draw(Assets.getTexture(newHighScore ? "trophyHighScore" : "trophy"), trophy.x, trophy.y, trophy.width, trophy.height);
		game.batch.end();
	}
}
