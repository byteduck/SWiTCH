package qixl.swtch.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

import aurelienribon.tweenengine.BaseTween
import aurelienribon.tweenengine.TweenCallback
import aurelienribon.tweenengine.TweenManager
import aurelienribon.tweenengine.equations.Quart
import qixl.swtch.util.Assets
import qixl.swtch.util.FontLayoutManager
import qixl.swtch.SWITCH
import qixl.swtch.util.Util

class MainMenuScreen(private val game: SWITCH, private val initial:Boolean = false) : ScreenAdapter() {
    private val guiCam: OrthographicCamera = OrthographicCamera(SWITCH.WIDTH, SWITCH.HEIGHT)
    private val textCam: OrthographicCamera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    private val touchPoint = Vector3()
    private val manager: TweenManager
    private val switchLogo: Rectangle

    /** LAYOUT VARS  */
    private val playButton: Rectangle
    private val title: Vector2

    init {
        if(initial) {
            textCam.position.set(-Gdx.graphics.width / 2f, Gdx.graphics.height / 2f, 0f)
            guiCam.position.set(-SWITCH.WIDTH / 2f, SWITCH.HEIGHT / 2, 0f)
        }else {
            textCam.position.set(Gdx.graphics.width * 1.5f, Gdx.graphics.height / 2f, 0f)
            guiCam.position.set(SWITCH.WIDTH * 1.5f, SWITCH.HEIGHT / 2, 0f)
        }

        /* PLAY BUTTON LAYOUT */
        val squareSize = SWITCH.WIDTH * 0.2f
        playButton = Rectangle(SWITCH.WIDTH / 2 - squareSize / 2, SWITCH.HEIGHT * 0.2f - squareSize / 2, squareSize, squareSize)

        /* TITLE LAYOUT */
        val titleLayout = FontLayoutManager.getFontLayout("S  W  i  T  C  H", game.mainMenuRobotoLight!!)
        title = Vector2(Gdx.graphics.width / 2 - titleLayout!!.width / 2, Gdx.graphics.height * 0.8f + titleLayout.height / 2)

        /* LOGO LAYOUT */
        val logoSize = SWITCH.WIDTH*0.35f
        switchLogo = Rectangle(SWITCH.WIDTH * 0.5f - logoSize * 0.5f, SWITCH.HEIGHT * 0.5f - logoSize * 0.5f, logoSize, logoSize)

        manager = TweenManager()
        Util.slideCamera(guiCam, Util.Direction.CENTER, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.OUT, 0f, null)
        Util.slideCamera(textCam, Util.Direction.CENTER, manager, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), Quart.OUT, 0f, null)
    }

    private fun update() {
        manager.update(Gdx.graphics.deltaTime)

        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
            if (playButton.contains(touchPoint.x, touchPoint.y)) {
				game.clickOnSound?.play()
                Util.slideCamera(guiCam, Util.Direction.LEFT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0f, null)
                Util.slideCamera(textCam, Util.Direction.LEFT, manager, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), Quart.IN, 0f) { type, _ ->
                    if (type == TweenCallback.COMPLETE) {
                        val playedTutorial = game.prefs!!.getBoolean("playedTutorial", false)
                        game.prefs!!.putBoolean("playedTutorial", true)
                        game.prefs!!.flush()
                        if(playedTutorial)
                            game.screen = GamemodeScreen(game)
                        else
                            game.screen = GameScreen(game, false, false, true, false)
                    }
                }
            }
        }
    }

    private fun draw() {
        val gl = Gdx.gl
        gl.glClearColor(0.13333f, 0.13333f, 0.13333f, 1f)
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        guiCam.update()
        textCam.update()

        game.batch?.projectionMatrix = textCam.combined

        game.batch?.begin()
        game.mainMenuRobotoLight!!.draw(game.batch, "S  W  i  T  C  H", title.x, title.y)
        game.batch?.end()

        game.batch?.projectionMatrix = guiCam.combined
        game.shape?.projectionMatrix = guiCam.combined

        game.batch?.begin()
        game.batch?.draw(Assets.getTexture("squarePlay"), playButton.x, playButton.y, playButton.width, playButton.height)
        game.batch?.draw(Assets.getTexture("switchLogo"), switchLogo.x, switchLogo.y, switchLogo.width, switchLogo.height)
        game.batch?.end()
    }

    override fun render(delta: Float) {
        update()
        draw()
    }

    override fun pause() {

    }
}
