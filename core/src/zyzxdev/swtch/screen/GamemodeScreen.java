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
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import zyzxdev.swtch.util.Assets;
import zyzxdev.swtch.util.FontLayoutManager;
import zyzxdev.swtch.SWITCH;
import zyzxdev.swtch.util.Util;

public class GamemodeScreen extends ScreenAdapter implements TweenCallback {
	private OrthographicCamera guiCam, textCam;
	private SWITCH game;
	private TweenManager manager;
	private Vector3 touchPoint = new Vector3();

	private boolean selectedSize = false, selectedMode = false, playHit = false;
	//false - 3x3 or casual ||| true - 4x4 or timed

	/** LAYOUT VARS **/
	private Rectangle backButton, backButtonTap, txt, fxf, casual, timed, playButton;
	private Vector2 mainText;

	public GamemodeScreen (SWITCH game) {
		this.game = game;

		guiCam = new OrthographicCamera(SWITCH.WIDTH, SWITCH.HEIGHT);
		guiCam.position.set(-SWITCH.WIDTH/2, SWITCH.HEIGHT / 2, 0);

		textCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		textCam.position.set(-Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2f, 0);

		/* BACK BUTTON LAYOUT */
		backButton = new Rectangle(20+SWITCH.WIDTH*0.1f, SWITCH.HEIGHT-20-SWITCH.WIDTH*0.1f, -SWITCH.WIDTH*0.1f, SWITCH.WIDTH*0.1f);
		backButtonTap = new Rectangle(20, SWITCH.HEIGHT-20-SWITCH.WIDTH*0.1f, SWITCH.WIDTH*0.1f, SWITCH.WIDTH*0.1f);

		/* MAIN TEXT LAYOUT */
		GlyphLayout mainTextLayout = FontLayoutManager.getFontLayout("Choose Mode", game.gamemodeRobotoLight);
		mainText = new Vector2(Gdx.graphics.getWidth()/2-mainTextLayout.width/2, Gdx.graphics.getHeight()*0.8f+mainTextLayout.height/2);

		/* ICON SIZE */
		float iconSize = SWITCH.WIDTH*0.2f;

		/* 3x3 ICON LAYOUT */
		txt = new Rectangle(SWITCH.WIDTH*0.3f-iconSize/2f, SWITCH.HEIGHT*0.7f-iconSize/2, iconSize, iconSize);

		/* 4x4 ICON LAYOUT */
		fxf = new Rectangle(SWITCH.WIDTH*0.7f-iconSize/2f, SWITCH.HEIGHT*0.7f-iconSize/2, iconSize, iconSize);

		/* CASUAL ICON LAYOUT */
		casual = new Rectangle(SWITCH.WIDTH*0.3f-iconSize/2f, SWITCH.HEIGHT*0.45f-iconSize/2, iconSize, iconSize);

		/* TIMED ICON LAYOUT */
		timed = new Rectangle(SWITCH.WIDTH*0.7f-iconSize/2f, SWITCH.HEIGHT*0.45f-iconSize/2, iconSize, iconSize);

		/* PLAY BUTTON LAYOUT */
		playButton = new Rectangle(SWITCH.WIDTH/2-iconSize/2, SWITCH.HEIGHT*0.2f-iconSize/2, iconSize, iconSize);

		manager = new TweenManager();
		Util.slideCamera(guiCam, Util.Direction.CENTER, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.OUT, 0f, null);
		Util.slideCamera(textCam, Util.Direction.CENTER, manager, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Quart.OUT, 0f, null);
	}

	@Override
	public void onEvent(int type, BaseTween<?> source) {
		if(type == COMPLETE){
			if(playHit)
				game.setScreen(new GameScreen(game, selectedSize, selectedMode, false));
			else
				game.setScreen(new MainMenuScreen(game));
		}
	}

	private void update(){
		manager.update(Gdx.graphics.getDeltaTime());
		if(Gdx.input.justTouched()){
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			if(backButtonTap.contains(touchPoint.x, touchPoint.y)){
				Util.slideCamera(guiCam, Util.Direction.RIGHT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0, null);
				Util.slideCamera(textCam, Util.Direction.RIGHT, manager, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Quart.IN, 0, this);
			}
			if(txt.contains(touchPoint.x, touchPoint.y))
				selectedSize = false;
			if(fxf.contains(touchPoint.x, touchPoint.y))
				selectedSize = true;
			if(casual.contains(touchPoint.x, touchPoint.y))
				selectedMode = false;
			if(timed.contains(touchPoint.x, touchPoint.y))
				selectedMode = true;
			if(playButton.contains(touchPoint.x, touchPoint.y)){
				playHit = true;
				Util.slideCamera(guiCam, Util.Direction.LEFT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0f, null);
				Util.slideCamera(textCam, Util.Direction.LEFT, manager, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Quart.IN, 0f, this);
			}
		}
	}

	private void draw(){
		GL20 gl = Gdx.gl;
		gl.glClearColor(0.13333f, 0.13333f, 0.13333f, 1f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		textCam.update();
		guiCam.update();

		game.batch.setProjectionMatrix(textCam.combined);
		game.batch.begin();

		//game.batch.setColor(1,1,1,1);
		//game.gamemodeRobotoLight.draw(game.batch, "Choose Mode", mainText.x, mainText.y);

		/*game.batch.setColor(1,1,1,0.5f);
		game.gamemodeRobotoLight.draw(game.batch, "3x3", Gdx.graphics.getWidth()/4-game.gamemodeFontLayout)*/

		game.batch.end();

		game.batch.setProjectionMatrix(guiCam.combined);
		game.shape.setProjectionMatrix(guiCam.combined);

		game.batch.begin();
		game.batch.draw(Assets.getTexture("squarePlay"), backButton.x, backButton.y, backButton.width, backButton.height);

		color(!selectedSize);
		game.batch.draw(Assets.getTexture("square3x3"), txt.x, txt.y, txt.width, txt.height);

		color(selectedSize);
		game.batch.draw(Assets.getTexture("square4x4"), fxf.x, fxf.y, fxf.width, fxf.height);

		color(!selectedMode);
		game.batch.draw(Assets.getTexture("casual"), casual.x, casual.y, casual.width, casual.height);

		color(selectedMode);
		game.batch.draw(Assets.getTexture("timed"), timed.x, timed.y, timed.width, timed.height);

		game.batch.setColor(1,1,1,1);
		game.batch.draw(Assets.getTexture("squarePlay"), playButton.x, playButton.y, playButton.width, playButton.height);

		game.batch.end();
	}

	/** Colors transparent for false, white for true **/
	private void color(boolean state){
		game.batch.setColor(1,1,1, state ? 1 : 0.5f);
	}

	@Override
	public void render(float delta){
		update();
		draw();
	}
}
