package qixl.swtch.screen

import aurelienribon.tweenengine.TweenCallback
import aurelienribon.tweenengine.TweenManager
import aurelienribon.tweenengine.equations.Quart
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import qixl.swtch.SWITCH
import qixl.swtch.util.Assets
import qixl.swtch.util.Util

/**
 * Created by aaron on 9/17/2017.
 */
class LoadingScreen(val game: SWITCH) : Screen {
    private var loops = 0
    private var exiting = false
    val guiCam: OrthographicCamera = OrthographicCamera(SWITCH.WIDTH, SWITCH.HEIGHT)
    val qixlLogo: Rectangle
    val qixlTexture: Texture = Assets.getTexture("qixl-text-med")!!
    val manager = TweenManager()

    init{
        guiCam.position.set(SWITCH.WIDTH/2, SWITCH.HEIGHT/2, 0f)
        val zyzxWidth = SWITCH.WIDTH*0.6f
        val zyzxHeight = (qixlTexture.height.toFloat()/ qixlTexture.width.toFloat())*zyzxWidth
        qixlLogo = Rectangle(SWITCH.WIDTH / 2 - zyzxWidth / 2, SWITCH.HEIGHT / 2 - zyzxHeight / 2, zyzxWidth, zyzxHeight)
    }

    override fun hide() {
    }

    override fun show() {
    }

    override fun render(delta: Float) {
        manager.update(delta)

        val gl = Gdx.gl
        gl.glClearColor(0.13333f, 0.13333f, 0.13333f, 1f)
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        guiCam.update()
        game.batch?.projectionMatrix = guiCam.combined

        game.batch?.begin()
        game.batch?.draw(qixlTexture, qixlLogo.x, qixlLogo.y, qixlLogo.width, qixlLogo.height)
        game.batch?.end()

        if(loops == 10){
            game.loadFonts()
        }

        if(!exiting && Assets.assetManager.update() && loops >= 20){
            game.assignAssetVariables()
            Util.slideCamera(guiCam, Util.Direction.LEFT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0f, { type, _ ->
                if(type == TweenCallback.COMPLETE)
                    game.screen = MainMenuScreen(game, initial = true)
            })
            exiting = true
        }

        loops++
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun dispose() {
    }

}