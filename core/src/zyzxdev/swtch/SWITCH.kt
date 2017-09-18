package zyzxdev.swtch

import aurelienribon.tweenengine.Tween
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import zyzxdev.swtch.nativeInterface.AdManager
import zyzxdev.swtch.screen.LoadingScreen
import zyzxdev.swtch.util.*

class SWITCH(val adManager: AdManager) : Game() {

    var batch: SpriteBatch? = null
    var shape: ShapeRenderer? = null
    var mainMenuRobotoLight: BitmapFont? = null
    var gamemodeRobotoLight: BitmapFont? = null
    var smallRobotoLight: BitmapFont? = null
    var prefs: Preferences? = null
    var clickOnSound: Sound? = null
    var clickOffSound: Sound? = null
    var completeSound: Sound? = null
    var errSound: Sound? = null

    override fun create() {
        WIDTH = Gdx.graphics.width / Gdx.graphics.density
        HEIGHT = Gdx.graphics.height / Gdx.graphics.density

        batch = SpriteBatch()
        shape = ShapeRenderer()
        shape?.setAutoShapeType(true)

        println("Inited variables")

        /* TEXTURES */
        Assets.addTexture("switchLogo", "texture/switchlogo.png")
        Assets.addTexture("square", "texture/square.png")
        Assets.addTexture("squarePlay", "texture/square-play.png")
        Assets.addTexture("square3x3", "texture/3x3.png")
        Assets.addTexture("square4x4", "texture/4x4.png")
        Assets.addTexture("timed", "texture/timed.png")
        Assets.addTexture("casual", "texture/casual.png")
        Assets.addTexture("trophy", "texture/trophy.png")
        Assets.addTexture("trophyHighScore", "texture/trophy-highscore.png")
        Assets.addTexture("squareAbnormal", "texture/abnormal.png")
        Assets.addTextureImmediately("qixl-text-med", "texture/qixl-text-med.png")

        println("Inited Textures")


        /* SOUND */
        Assets.assetManager.load("sound/clickon.wav", Sound::class.java)
        Assets.assetManager.load("sound/clickoff.wav", Sound::class.java)
        Assets.assetManager.load("sound/complete.mp3", Sound::class.java)
        Assets.assetManager.load("sound/wrong.mp3", Sound::class.java)

        println("Inited sound")

        Tween.registerAccessor(Vector2::class.java, Vector2Accessor())
        Tween.registerAccessor(Vector3::class.java, Vector3Accessor())
        Tween.registerAccessor(Rectangle::class.java, RectangleAccessor())
        Tween.registerAccessor(Color::class.java, ColorAccessor())

        println("Inited accessors")

        prefs = Gdx.app.getPreferences("prefs")

        Tween.setCombinedAttributesLimit(4)

        println("Inited!")

        setScreen(LoadingScreen(this))
    }

    fun assignAssetVariables(){
        clickOnSound = Assets.assetManager.get("sound/clickon.wav")
        clickOffSound = Assets.assetManager.get("sound/clickoff.wav")
        completeSound = Assets.assetManager.get("sound/complete.mp3")
        errSound = Assets.assetManager.get("sound/wrong.mp3")
    }

    companion object {
        var WIDTH: Float = 0.toFloat()
        var HEIGHT: Float = 0.toFloat()
    }

    fun loadFonts() {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("font/Roboto-Light.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()

        parameter.size = Gdx.graphics.width / 7
        parameter.color = Color.WHITE
        mainMenuRobotoLight = generator.generateFont(parameter)

        parameter.size = Gdx.graphics.width / 7
        gamemodeRobotoLight = generator.generateFont(parameter)

        parameter.size = Gdx.graphics.width / 10
        smallRobotoLight = generator.generateFont(parameter)

        generator.dispose()
        println("Inited fonts")
    }
}
