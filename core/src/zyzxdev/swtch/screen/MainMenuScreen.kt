package zyzxdev.swtch.screen

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
import zyzxdev.swtch.util.Assets
import zyzxdev.swtch.util.FontLayoutManager
import zyzxdev.swtch.SWITCH
import zyzxdev.swtch.util.Util

class MainMenuScreen(private val game: SWITCH) : ScreenAdapter() {
    private val guiCam: OrthographicCamera
    private val textCam: OrthographicCamera
    private val touchPoint = Vector3()
    private val manager: TweenManager

    /** LAYOUT VARS  */
    private val playButton: Rectangle
    private val title: Vector2

    init {

        guiCam = OrthographicCamera(SWITCH.WIDTH, SWITCH.HEIGHT)
        guiCam.position.set(SWITCH.WIDTH * 1.5f, SWITCH.HEIGHT / 2, 0f)

        textCam = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        textCam.position.set(Gdx.graphics.width * 1.5f, Gdx.graphics.height / 2f, 0f)

        /* PLAY BUTTON LAYOUT */
        val squareSize = SWITCH.WIDTH * 0.2f
        playButton = Rectangle(SWITCH.WIDTH / 2 - squareSize / 2, SWITCH.HEIGHT * 0.2f - squareSize / 2, squareSize, squareSize)

        /* TITLE LAYOUT */
        val titleLayout = FontLayoutManager.getFontLayout("S  W  i  T  C  H", game.mainMenuRobotoLight!!)
        title = Vector2(Gdx.graphics.width / 2 - titleLayout!!.width / 2, Gdx.graphics.height * 0.8f + titleLayout.height / 2)

        manager = TweenManager()
        Util.slideCamera(guiCam, Util.Direction.CENTER, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.OUT, 0f, null)
        Util.slideCamera(textCam, Util.Direction.CENTER, manager, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), Quart.OUT, 0f, null)
    }

    private fun update() {
        manager.update(Gdx.graphics.deltaTime)

        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
            if (playButton.contains(touchPoint.x, touchPoint.y)) {
                Util.slideCamera(guiCam, Util.Direction.LEFT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0f, null)
                Util.slideCamera(textCam, Util.Direction.LEFT, manager, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), Quart.IN, 0f) {type, _ ->
                    if (type == TweenCallback.COMPLETE) {
                        val playedTutorial = game.prefs!!.getBoolean("playedTutorial", false)
                        game.prefs!!.putBoolean("playedTutorial", true)
                        game.prefs!!.flush()
                        if(playedTutorial)
                            game.screen = GamemodeScreen(game)
                        else
                            game.screen = GameScreen(game, false, false, true)
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
        game.batch?.end()
    }

    override fun render(delta: Float) {
        update()
        draw()
    }

    override fun pause() {

    }
}
