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

public class MainMenuScreen extends ScreenAdapter implements TweenCallback{
	private SWITCH game;
	private OrthographicCamera guiCam, textCam;
	private Vector3 touchPoint = new Vector3();
	private TweenManager manager;

	/** LAYOUT VARS **/
	private Rectangle playButton;
	private Vector2 title;

	public MainMenuScreen (SWITCH game) {
		this.game = game;

		guiCam = new OrthographicCamera(SWITCH.WIDTH, SWITCH.HEIGHT);
		guiCam.position.set(SWITCH.WIDTH*1.5f, SWITCH.HEIGHT / 2, 0);

		textCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		textCam.position.set(Gdx.graphics.getWidth()*1.5f, Gdx.graphics.getHeight()/2f, 0);

		/* PLAY BUTTON LAYOUT */
		float squareSize = SWITCH.WIDTH*0.2f;
		playButton = new Rectangle(SWITCH.WIDTH/2-squareSize/2, SWITCH.HEIGHT*0.2f-squareSize/2, squareSize, squareSize);

		/* TITLE LAYOUT */
		GlyphLayout titleLayout = FontLayoutManager.getFontLayout("S  W  i  T  C  H", game.mainMenuRobotoLight);
		title = new Vector2(Gdx.graphics.getWidth()/2-titleLayout.width/2, Gdx.graphics.getHeight()*0.8f+titleLayout.height/2);

		manager = new TweenManager();
		Util.slideCamera(guiCam, Util.Direction.CENTER, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.OUT, 0f, null);
		Util.slideCamera(textCam, Util.Direction.CENTER, manager, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Quart.OUT, 0f, null);
	}

	private void update () {
		manager.update(Gdx.graphics.getDeltaTime());

		if(Gdx.input.justTouched()){
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			if(playButton.contains(touchPoint.x, touchPoint.y)){
				Util.slideCamera(guiCam, Util.Direction.LEFT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0, null);
				Util.slideCamera(textCam, Util.Direction.LEFT, manager, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Quart.IN, 0, this);
			}
		}
	}

	private void draw () {
		GL20 gl = Gdx.gl;
		gl.glClearColor(0.13333f, 0.13333f, 0.13333f, 1f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		guiCam.update();
		textCam.update();

		game.batch.setProjectionMatrix(textCam.combined);

		game.batch.begin();
		game.mainMenuRobotoLight.draw(game.batch, "S  W  i  T  C  H", title.x, title.y);
		game.batch.end();

		game.batch.setProjectionMatrix(guiCam.combined);
		game.shape.setProjectionMatrix(guiCam.combined);

		game.batch.begin();
		game.batch.draw(Assets.getTexture("squarePlay"), playButton.x, playButton.y, playButton.width, playButton.height);
		game.batch.end();
	}

	@Override
	public void render (float delta) {
		update();
		draw();
	}

	@Override
	public void pause () {

	}

	@Override
	public void onEvent(int type, BaseTween<?> source) {
		if(type == COMPLETE){
			game.setScreen(new GamemodeScreen(game));
		}
	}
}
