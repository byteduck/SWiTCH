package zyzxdev.swtch.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3

import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenManager
import aurelienribon.tweenengine.equations.Quart
import zyzxdev.swtch.SWITCH
import zyzxdev.swtch.util.Assets
import zyzxdev.swtch.util.RectangleAccessor
import zyzxdev.swtch.util.Util
import zyzxdev.swtch.util.Vector3Accessor

/**
 * Created by aaron on 5/31/17.
 */

class GameOverScreen internal constructor(private val game: SWITCH, private val score: Int, size: Boolean, mode: Boolean) : ScreenAdapter() {
    private val guiCam: OrthographicCamera
    private val textCam: OrthographicCamera
    private val touchPoint = Vector3()
    private val manager: TweenManager
    private val highScore: Int
    private var newHighScore = false
    private var trophy: Rectangle? = null
    private var textOne: Vector3? = null
    private var textTwo: Vector3? = null //z is opacity
    private val textOneStr: String
    private val textTwoStr: String
    private var iconSize: Float = 0.toFloat()
    private var canTap = false

    init {
        guiCam = OrthographicCamera(SWITCH.WIDTH, SWITCH.HEIGHT)
        guiCam.position.set(SWITCH.WIDTH * 1.5f, SWITCH.HEIGHT / 2, 0f)

        textCam = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        textCam.position.set(Gdx.graphics.width * 1.5f, Gdx.graphics.height / 2f, 0f)

        val settingsKey = "hs" + (if (size) "4x4" else "3x3") + if (mode) "comp" else "casual"
        this.highScore = game.prefs!!.getInteger(settingsKey, 0)
        iconSize = SWITCH.WIDTH * 0.1f
        textOneStr = score.toString() + ""
        textTwoStr = highScore.toString() + ""
        val layout = GlyphLayout(game.mainMenuRobotoLight, textOneStr)
        val layout2 = GlyphLayout(game.smallRobotoLight, textTwoStr)

        if (score > highScore) {
            newHighScore = true
            game.prefs!!.putInteger(settingsKey, score)
            game.prefs!!.flush()
            textOne = Vector3(Gdx.graphics.width / 2 - layout.width / 2, Gdx.graphics.height / 2 + layout.height / 2, 0f)
            iconSize = SWITCH.WIDTH * 0.4f
            trophy = Rectangle(SWITCH.WIDTH / 2 - iconSize / 2, SWITCH.HEIGHT / 2 - iconSize / 2, iconSize, iconSize)
        } else {
            textOne = Vector3(Gdx.graphics.width / 2 - layout.width / 2, Gdx.graphics.height / 2 + layout.height / 2, 0f)
            textTwo = Vector3(Gdx.graphics.width * 0.05f + iconSize * Gdx.graphics.density + layout2.width / 2, Gdx.graphics.width * 0.05f + layout.height * 0.79f, 1f)
            trophy = Rectangle(SWITCH.WIDTH * 0.05f, SWITCH.WIDTH * 0.05f, iconSize, iconSize)
        }

        manager = TweenManager()
        Util.slideCamera(guiCam, Util.Direction.CENTER, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.OUT, 0f, null)
        Util.slideCamera(textCam, Util.Direction.CENTER, manager, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), Quart.OUT, 0f) { type, source ->
            if (newHighScore) {
                Tween.to(trophy, RectangleAccessor.TYPE_XYWH, 1f)
                        .target(SWITCH.WIDTH / 2 - iconSize * 0.4f, trophy!!.y - iconSize * 0.4f, iconSize * 0.8f, iconSize * 0.8f)
                        .ease(Quart.OUT)
                        .delay(1f)
                        .start(manager)
                Tween.to(textOne, Vector3Accessor.TYPE_XYZ, 1f)
                        .target(textOne!!.x, textOne!!.y + iconSize * 0.4f * Gdx.graphics.density, 1f)
                        .ease(Quart.OUT)
                        .delay(1f)
                        .setCallback { type, source -> canTap = true }
                        .start(manager)
            } else {
                Tween.to(textOne, Vector3Accessor.TYPE_XYZ, 1f)
                        .target(textOne!!.x, textOne!!.y, 1f)
                        .ease(Quart.OUT)
                        .delay(0.5f)
                        .setCallback { type, source -> canTap = true }
                        .start(manager)
            }
        }

    }

    override fun render(delta: Float) {
        update()
        draw()
    }

    private fun update() {
        manager.update(Gdx.graphics.deltaTime)

        if (Gdx.input.justTouched() && canTap) {
            Util.slideCamera(guiCam, Util.Direction.RIGHT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0f, null)
            Util.slideCamera(textCam, Util.Direction.RIGHT, manager, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), Quart.IN, 0f) { type, source -> game.screen = MainMenuScreen(game) }
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
        if (textOne != null) {
            val col = 0.13333f + 0.86667f * Math.max(Math.min(textOne!!.z, 1f), 0f)
            game.mainMenuRobotoLight!!.setColor(col, col, col, 1f)
            game.mainMenuRobotoLight!!.draw(game.batch, textOneStr, textOne!!.x, textOne!!.y)
        }
        if (textTwo != null) {
            val col = 0.13333f + 0.86667f * Math.max(Math.min(textTwo!!.z, 1f), 0f)
            game.smallRobotoLight!!.setColor(col, col, col, 1f)
            game.smallRobotoLight!!.draw(game.batch, textTwoStr, textTwo!!.x, textTwo!!.y)
        }
        game.mainMenuRobotoLight!!.setColor(1f, 1f, 1f, 1f)
        game.batch?.end()

        game.batch?.projectionMatrix = guiCam.combined
        game.batch?.begin()
        game.batch?.draw(Assets.getTexture(if (newHighScore) "trophyHighScore" else "trophy"), trophy!!.x, trophy!!.y, trophy!!.width, trophy!!.height)
        game.batch?.end()
    }
}
