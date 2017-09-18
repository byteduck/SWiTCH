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

class GamemodeScreen(private val game: SWITCH) : ScreenAdapter() {
    private val guiCam: OrthographicCamera
    private val textCam: OrthographicCamera
    private val manager: TweenManager
    private val touchPoint = Vector3()

    private var selectedSize = 0 //0 = 3x3, 1 = 4x4, 2 = 4x4 abnormal
    private var selectedMode = false //timed/not
    private var playHit = false
    //false - 3x3 or casual ||| true - 4x4 or timed

    /** LAYOUT VARS  */
    private val backButton: Rectangle
    private val backButtonTap: Rectangle
    private val txt: Rectangle
    private val fxf: Rectangle
    private val abn: Rectangle
    private val casual: Rectangle
    private val timed: Rectangle
    private val playButton: Rectangle
    private val mainText: Vector2

    private val callback = { type: Int, source: BaseTween<*> ->
        if (type == TweenCallback.COMPLETE) {
            if (playHit)
                game.screen = GameScreen(game, selectedSize != 0, selectedMode, false, selectedSize == 2)
            else
                game.screen = MainMenuScreen(game)
        }
    }

    init {

        guiCam = OrthographicCamera(SWITCH.WIDTH, SWITCH.HEIGHT)
        guiCam.position.set(-SWITCH.WIDTH / 2, SWITCH.HEIGHT / 2, 0f)

        textCam = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        textCam.position.set((-Gdx.graphics.width / 2).toFloat(), Gdx.graphics.height / 2f, 0f)

        /* BACK BUTTON LAYOUT */
        backButton = Rectangle(20 + SWITCH.WIDTH * 0.1f, SWITCH.HEIGHT - 20f - SWITCH.WIDTH * 0.1f, -SWITCH.WIDTH * 0.1f, SWITCH.WIDTH * 0.1f)
        backButtonTap = Rectangle(20f, SWITCH.HEIGHT - 20f - SWITCH.WIDTH * 0.1f, SWITCH.WIDTH * 0.1f, SWITCH.WIDTH * 0.1f)

        /* MAIN TEXT LAYOUT */
        val mainTextLayout = FontLayoutManager.getFontLayout("Choose Mode", game.gamemodeRobotoLight!!)
        mainText = Vector2(Gdx.graphics.width / 2 - mainTextLayout!!.width / 2, Gdx.graphics.height * 0.8f + mainTextLayout.height / 2)

        /* ICON SIZE */
        val iconSize = SWITCH.WIDTH * 0.2f

        /* 3x3 ICON LAYOUT */
        txt = Rectangle(SWITCH.WIDTH * 0.2f - iconSize / 2f, SWITCH.HEIGHT * 0.7f - iconSize / 2, iconSize, iconSize)

        /* 4x4 ICON LAYOUT */
        fxf = Rectangle(SWITCH.WIDTH * 0.5f - iconSize / 2f, SWITCH.HEIGHT * 0.7f - iconSize / 2, iconSize, iconSize)

        /* ABNORMAL ICON LAYOUT */
        abn = Rectangle(SWITCH.WIDTH * 0.8f - iconSize / 2f, SWITCH.HEIGHT * 0.7f - iconSize / 2, iconSize, iconSize)

        /* CASUAL ICON LAYOUT */
        casual = Rectangle(SWITCH.WIDTH * 0.3f - iconSize / 2f, SWITCH.HEIGHT * 0.45f - iconSize / 2, iconSize, iconSize)

        /* TIMED ICON LAYOUT */
        timed = Rectangle(SWITCH.WIDTH * 0.7f - iconSize / 2f, SWITCH.HEIGHT * 0.45f - iconSize / 2, iconSize, iconSize)

        /* PLAY BUTTON LAYOUT */
        playButton = Rectangle(SWITCH.WIDTH / 2 - iconSize / 2, SWITCH.HEIGHT * 0.2f - iconSize / 2, iconSize, iconSize)

        manager = TweenManager()
        Util.slideCamera(guiCam, Util.Direction.CENTER, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.OUT, 0f, null)
        Util.slideCamera(textCam, Util.Direction.CENTER, manager, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), Quart.OUT, 0f, null)
    }

    private fun update() {
        manager.update(Gdx.graphics.deltaTime)
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
            if (backButtonTap.contains(touchPoint.x, touchPoint.y)) {
                Util.slideCamera(guiCam, Util.Direction.RIGHT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0f, null)
                Util.slideCamera(textCam, Util.Direction.RIGHT, manager, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), Quart.IN, 0f, callback)
            }
            if (txt.contains(touchPoint.x, touchPoint.y))
                selectedSize = 0
            if (fxf.contains(touchPoint.x, touchPoint.y))
                selectedSize = 1
            if (abn.contains(touchPoint.x, touchPoint.y))
                selectedSize = 2
            if (casual.contains(touchPoint.x, touchPoint.y))
                selectedMode = false
            if (timed.contains(touchPoint.x, touchPoint.y))
                selectedMode = true
            if (playButton.contains(touchPoint.x, touchPoint.y)) {
                playHit = true
                Util.slideCamera(guiCam, Util.Direction.LEFT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0f, null)
                Util.slideCamera(textCam, Util.Direction.LEFT, manager, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), Quart.IN, 0f, callback)
            }
        }
    }

    private fun draw() {
        val gl = Gdx.gl
        gl.glClearColor(0.13333f, 0.13333f, 0.13333f, 1f)
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        textCam.update()
        guiCam.update()

        game.batch?.projectionMatrix = textCam.combined
        game.batch?.begin()

        //game.batch?.setColor(1,1,1,1);
        //game.gamemodeRobotoLight.draw(game.batch, "Choose Mode", mainText.x, mainText.y);

        /*game.batch?.setColor(1,1,1,0.5f);
		game.gamemodeRobotoLight.draw(game.batch, "3x3", Gdx.graphics.getWidth()/4-game.gamemodeFontLayout)*/

        game.batch?.end()

        game.batch?.projectionMatrix = guiCam.combined
        game.shape?.projectionMatrix = guiCam.combined

        game.batch?.begin()
        game.batch?.draw(Assets.getTexture("squarePlay"), backButton.x, backButton.y, backButton.width, backButton.height)

        color(selectedSize == 0)
        game.batch?.draw(Assets.getTexture("square3x3"), txt.x, txt.y, txt.width, txt.height)

        color(selectedSize == 1)
        game.batch?.draw(Assets.getTexture("square4x4"), fxf.x, fxf.y, fxf.width, fxf.height)

        color(selectedSize == 2)
        game.batch?.draw(Assets.getTexture("squareAbnormal"), abn.x, abn.y, abn.width, abn.height)

        color(!selectedMode)
        game.batch?.draw(Assets.getTexture("casual"), casual.x, casual.y, casual.width, casual.height)

        color(selectedMode)
        game.batch?.draw(Assets.getTexture("timed"), timed.x, timed.y, timed.width, timed.height)

        game.batch?.setColor(1f, 1f, 1f, 1f)
        game.batch?.draw(Assets.getTexture("squarePlay"), playButton.x, playButton.y, playButton.width, playButton.height)

        game.batch?.end()
    }

    /** Colors transparent for false, white for true  */
    private fun color(state: Boolean) {
        game.batch?.setColor(1f, 1f, 1f, if (state) 1f else 0.5f)
    }

    override fun render(delta: Float) {
        update()
        draw()
    }
}
