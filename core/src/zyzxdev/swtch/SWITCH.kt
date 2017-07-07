package zyzxdev.swtch

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

import aurelienribon.tweenengine.Tween
import zyzxdev.swtch.screen.GameOverScreen
import zyzxdev.swtch.screen.MainMenuScreen
import zyzxdev.swtch.util.ColorAccessor
import zyzxdev.swtch.util.RectangleAccessor
import zyzxdev.swtch.util.Vector3Accessor

class SWITCH : Game() {

    var batch: SpriteBatch? = null
    var shape: ShapeRenderer? = null
    var mainMenuRobotoLight: BitmapFont? = null
    var gamemodeRobotoLight: BitmapFont? = null
    var smallRobotoLight: BitmapFont? = null
    var prefs: Preferences? = null

    override fun create() {
        WIDTH = Gdx.graphics.width / Gdx.graphics.density
        HEIGHT = Gdx.graphics.height / Gdx.graphics.density

        batch = SpriteBatch()
        shape = ShapeRenderer()
        shape?.setAutoShapeType(true)

        /* TEXTURES */
        zyzxdev.swtch.util.Assets.addTexture("switchLogoText", "switchlogotext.png")
        zyzxdev.swtch.util.Assets.addTexture("square", "square.png")
        zyzxdev.swtch.util.Assets.addTexture("squarePlay", "square-play.png")
        zyzxdev.swtch.util.Assets.addTexture("square3x3", "3x3.png")
        zyzxdev.swtch.util.Assets.addTexture("square4x4", "4x4.png")
        zyzxdev.swtch.util.Assets.addTexture("timed", "timed.png")
        zyzxdev.swtch.util.Assets.addTexture("casual", "casual.png")
        zyzxdev.swtch.util.Assets.addTexture("trophy", "trophy.png")
        zyzxdev.swtch.util.Assets.addTexture("trophyHighScore", "trophy-highscore.png")

        Tween.registerAccessor(Vector2::class.java, zyzxdev.swtch.util.Vector2Accessor())
        Tween.registerAccessor(Vector3::class.java, Vector3Accessor())
        Tween.registerAccessor(Rectangle::class.java, RectangleAccessor())
        Tween.registerAccessor(Color::class.java, ColorAccessor())

        val generator = FreeTypeFontGenerator(Gdx.files.internal("Roboto-Light.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()

        parameter.size = Gdx.graphics.width / 7
        parameter.color = Color.WHITE
        mainMenuRobotoLight = generator.generateFont(parameter)

        parameter.size = Gdx.graphics.width / 7
        gamemodeRobotoLight = generator.generateFont(parameter)

        parameter.size = Gdx.graphics.width / 10
        smallRobotoLight = generator.generateFont(parameter)

        prefs = Gdx.app.getPreferences("prefs")

        generator.dispose()

        Tween.setCombinedAttributesLimit(4)

        setScreen(MainMenuScreen(this))
    }

    companion object {
        var WIDTH: Float = 0.toFloat()
        var HEIGHT: Float = 0.toFloat()
    }
}
