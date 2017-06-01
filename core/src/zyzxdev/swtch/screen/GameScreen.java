package zyzxdev.swtch.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import zyzxdev.swtch.SWITCH;
import zyzxdev.swtch.Square;
import zyzxdev.swtch.util.Assets;
import zyzxdev.swtch.util.ColorAccessor;
import zyzxdev.swtch.util.RectangleAccessor;
import zyzxdev.swtch.util.Util;
import zyzxdev.swtch.util.Vector2Accessor;
import zyzxdev.swtch.util.Vector3Accessor;

/**
 * Created by aaron on 5/30/17.
 */

public class GameScreen extends ScreenAdapter{
	private OrthographicCamera guiCam, textCam;
	private TweenManager manager;
	private SWITCH game;
	private boolean size, mode, tutorial;
	private float squareSize, boardSize, squareSpacing;
	private Vector3 touchPoint = new Vector3(), clock;
	private Vector2 selectedSquare = null;
	private Square[][] grid;
	private Rectangle backButtonRender, backButtonTouch;
	private Vector2 timer = new Vector2(); //y not used, using vector2 for tweening
	private Vector3 text = new Vector3(); //x,y=center pos z=opacity
	private boolean doUpdate = false;
	private int score = -1;
	private float maxTime = 20;
	private GlyphLayout textLayout;

	/**
	 * @param size false(3x3) true(4x4)
	 * @param mode false(casual) true(timed)
	 * @param tutorial Is it a tutorial?
	**/
	public GameScreen(SWITCH game, boolean size, boolean mode, boolean tutorial){
		guiCam = new OrthographicCamera(SWITCH.WIDTH, SWITCH.HEIGHT);
		guiCam.position.set(-SWITCH.WIDTH/2, SWITCH.HEIGHT / 2, 0);

		textCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		textCam.position.set(-Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2f, 0);

		this.mode = mode;
		this.size = size;
		this.tutorial = tutorial;
		this.game = game;

		timer.x = maxTime;

		grid = new Square[size ? 4 : 3][size ? 4 : 3];

		boardSize = SWITCH.WIDTH*0.8f;
		squareSize = boardSize/grid.length;
		squareSpacing = squareSize*0.1f;
		squareSize -= squareSpacing;

		for(int x = 0; x < grid.length; x++)
			for(int y = 0; y < grid.length; y++) { //Square pos is at the center of the square for scaling
				grid[x][y] = new Square();
				grid[x][y].renderRectangle = new Rectangle(SWITCH.WIDTH * 0.5f - boardSize / 2 + (x + 0.5f) * squareSize + (x + 0.5f) * squareSpacing,
						SWITCH.HEIGHT * 0.5f - boardSize / 2 + (y + 0.5f) * squareSize + (y + 0.5f) * squareSpacing, squareSize, squareSize);
				grid[x][y].touchRectangle = new Rectangle(SWITCH.WIDTH * 0.5f - boardSize / 2 + x * squareSize + (x + 0.5f) * squareSpacing,
						SWITCH.HEIGHT * 0.5f - boardSize / 2 + y * squareSize + (y + 0.5f) * squareSpacing, squareSize, squareSize);
			}

		populateGrid();

		backButtonRender = new Rectangle(20+SWITCH.WIDTH*0.1f, SWITCH.HEIGHT-20-SWITCH.WIDTH*0.1f, -SWITCH.WIDTH*0.1f, SWITCH.WIDTH*0.1f);
		backButtonTouch = new Rectangle(20, SWITCH.HEIGHT-20-SWITCH.WIDTH*0.1f, SWITCH.WIDTH*0.1f, SWITCH.WIDTH*0.1f);

		float clockRadius = SWITCH.WIDTH*0.1f;
		clock = new Vector3(SWITCH.WIDTH/2, SWITCH.HEIGHT*0.15f, clockRadius);

		text = new Vector3(Gdx.graphics.getWidth()/2, mode ? Gdx.graphics.getHeight()*0.83f : Gdx.graphics.getHeight()*0.17f, 1);
		updateScore(false);

		manager = new TweenManager();
		Util.slideCamera(guiCam, Util.Direction.CENTER, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.OUT, 0f, null);
		Util.slideCamera(textCam, Util.Direction.CENTER, manager, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Quart.OUT, 0f, new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				doUpdate = true;
			}
		});
	}

	@Override
	public void render(float delta){
		update();
		draw();
	}

	private void populateGrid(){
		int color = Util.rand.nextInt(3);
		for(int x = 0; x < grid.length; x++)
			for(int y = 0; y < grid[x].length; y++)
				grid[x][y].setColor(color);

		int mixUp = 20+Util.rand.nextInt(20);
		for(int i = 0; i < mixUp; i++){
			int x = Util.rand.nextInt(this.grid.length);
			int y = Util.rand.nextInt(this.grid.length);
			String change = Math.random() <= 0.5 ? "x" : "y";
			int x2 = change == "x" ? -1 : x;
			int y2 = change == "y" ? -1 : y;
			if(change == "x")
				while(x2 < 0 || x2 >= this.grid.length)
					x2 = x+(Util.rand.nextBoolean() ? -1 : 1);
			else
				while(y2 < 0 || y2 >= this.grid.length)
					y2 = y+(Util.rand.nextBoolean() ? -1 : 1);
			if(this.grid[x][y].colorID == this.grid[x2][y2].colorID){
				this.grid[x][y].setColor(Util.colorChanges[this.grid[x][y].colorID][0]);
				this.grid[x2][y2].setColor(Util.colorChanges[this.grid[x2][y2].colorID][1]);
			}
		}
	}

	private void update(){
		manager.update(Gdx.graphics.getDeltaTime());
		if(!doUpdate) return;
		timer.x -= Gdx.graphics.getDeltaTime();
		if(mode && timer.x <= 0)
			lose(true);
		if(Gdx.input.justTouched()){
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if(backButtonTouch.contains(touchPoint.x, touchPoint.y)){
				doUpdate = false;
				Util.slideCamera(guiCam, Util.Direction.RIGHT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0, null);
				Util.slideCamera(textCam, Util.Direction.RIGHT, manager, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Quart.IN, 0, new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						game.setScreen(new MainMenuScreen(game));
					}
				});
			}

			for(int x = 0; x < grid.length; x++)
				for(int y = 0; y < grid.length; y++)
					if(grid[x][y].touchRectangle.contains(touchPoint.x, touchPoint.y)){
						if(selectedSquare == null) {
							selectedSquare = new Vector2(x, y);
							grid[x][y].setSizeWithTween(squareSize*0.85f, 0.15f).start(manager);
						}else{
							Square square2 = grid[(int) selectedSquare.x][(int) selectedSquare.y];
							square2.setSizeWithTween(squareSize, 0.15f).start(manager);
							if(square2.colorID != grid[x][y].colorID && Math.sqrt(Math.pow(x-selectedSquare.x,2) + Math.pow(y-selectedSquare.y,2)) == 1){
								int color = Util.reverseColorChanges.get(grid[x][y].colorID+","+square2.colorID);
								grid[x][y].setColorWithTween(color).start(manager);
								square2.setColorWithTween(color).start(manager);

								if(checkGrid()){
									selectedSquare = null;
									win();
									return;
								}
							}
							selectedSquare = null;
						}
					}
		}
	}

	private void lose(final boolean showGameOver) {
		doUpdate = false;
		animateSquares(false, new TweenCallback(){
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				Util.slideCamera(guiCam, Util.Direction.RIGHT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0, null);
				Util.slideCamera(textCam, Util.Direction.RIGHT, manager, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Quart.IN, 0, new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						if(!showGameOver)
							game.setScreen(new MainMenuScreen(game));
						else
							game.setScreen(new GameOverScreen(game, score, size, mode));
					}
				});
			}
		});
	}

	private void win(){
		doUpdate = false;
		updateScore(true);
		Tween.to(timer, Vector2Accessor.TYPE_XY, 0.6f + 0.05f*grid.length*2)
				.target(maxTime, 0)
				.ease(Bounce.OUT)
				.start(manager);
		animateSquares(false, new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				populateGrid();
				animateSquares(true, new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						doUpdate = true;
					}
				});
			}
		});
	}

	private void animateSquares(boolean in, TweenCallback callback){
		int gridSize = size ? 4 : 3;
		Tween t;
		for(int x = 0; x < grid.length; x++)
			for(int y = 0; y < grid.length; y++) {
				if (in)
					t = grid[x][y].setSizeWithTween(squareSize, 0.3f).ease(Back.OUT).delay((x + -y + gridSize + 1) * 0.05f).start(manager);
				else
					t = grid[x][y].setSizeWithTween(0, 0.3f).ease(Back.IN).delay((x + -y + gridSize + 1) * 0.05f).start(manager);
				if(callback != null && x == grid.length-1 && y == 0)
					t.setCallback(callback);
			}
	}

	private boolean checkGrid(){
		int col = grid[0][0].colorID;
		for(int x = 0; x < grid.length; x++)
			for(int y = 0; y < grid.length; y++)
				if(grid[x][y].colorID != col)
					return false;
		return true;
	}

	private void draw(){
		GL20 gl = Gdx.gl;
		gl.glClearColor(0.13333f, 0.13333f, 0.13333f, 1f);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		guiCam.update();
		textCam.update();

		game.batch.setProjectionMatrix(guiCam.combined);
		game.shape.setProjectionMatrix(guiCam.combined);

		game.batch.begin();
		for(int x = 0; x < grid.length; x++)
			for(int y = 0; y < grid.length; y++) {
				game.batch.setColor(grid[x][y].color);
				Rectangle square = grid[x][y].renderRectangle;
				game.batch.draw(Assets.getTexture("square"), square.x-square.width/2f, square.y-square.height/2f, square.width, square.height);
			}
		game.batch.setColor(1,1,1,1);
		game.batch.draw(Assets.getTexture("squarePlay"), backButtonRender.x, backButtonRender.y, backButtonRender.width, backButtonRender.height);
		game.batch.end();

		if(mode){
			game.shape.begin(ShapeRenderer.ShapeType.Filled);
			game.shape.setColor(1, 1, 1, 1);
			game.shape.arc(clock.x, clock.y, clock.z, 90, 361, 100);
			game.shape.setColor(0.13333f, 0.13333f, 0.13333f, 1f);
			game.shape.arc(clock.x, clock.y, clock.z * 0.85f, 90, 361, 100);
			game.shape.setColor(1, 1, 1, 1);
			double angle = Math.toRadians((timer.x / (double)maxTime) * 360d + 90);
			float cos = (float) Math.cos(angle);
			float sin = (float) Math.sin(angle);
			game.shape.rectLine(clock.x, clock.y, clock.x + cos * clock.z * 0.6f, clock.y + sin * clock.z * 0.6f, clock.z * 0.15f);
			game.shape.rectLine(clock.x, clock.y, clock.x, clock.y + clock.z * 0.6f, clock.z * 0.15f);
			game.shape.arc(clock.x, clock.y, clock.z * 0.075f, 90, 361, 100);
			game.shape.end();
		}

		game.batch.setProjectionMatrix(textCam.combined);
		game.batch.begin();
		float col = 0.13333f+0.86667f*Math.max(Math.min(text.z, 1), 0);
		game.gamemodeRobotoLight.setColor(col,col,col,1);
		game.gamemodeRobotoLight.draw(game.batch, ""+score, text.x-textLayout.width/2, text.y+textLayout.height/2);
		game.batch.end();
	}

	public void updateScore(boolean animate) {
		if(animate){
			Tween.to(text, Vector3Accessor.TYPE_XYZ, 0.3f + 0.05f*grid.length)
					.target(text.x, text.y-textLayout.height*0.3f, -1f)
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int type, BaseTween<?> source) {
							text.y += textLayout.height*0.6f;
							score++;
							textLayout = new GlyphLayout(game.gamemodeRobotoLight, "" + score);
							Tween.to(text, Vector3Accessor.TYPE_XYZ, 0.3f + 0.05f*grid.length)
									.target(text.x, text.y-textLayout.height*0.3f, 1f)
									.ease(Quad.OUT)
									.start(manager);
						}
					})
					.ease(Quad.IN)
					.start(manager);
		}else {
			score++;
			this.textLayout = new GlyphLayout(game.gamemodeRobotoLight, "" + score);
		}
	}
}
