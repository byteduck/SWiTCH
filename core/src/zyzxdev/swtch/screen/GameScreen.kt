package zyzxdev.swtch.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenCallback
import aurelienribon.tweenengine.TweenManager
import aurelienribon.tweenengine.equations.*
import sun.rmi.runtime.Log
import zyzxdev.swtch.SWITCH
import zyzxdev.swtch.Square
import zyzxdev.swtch.util.Assets
import zyzxdev.swtch.util.Util
import zyzxdev.swtch.util.Vector2Accessor
import zyzxdev.swtch.util.Vector3Accessor

/**
 * Created by aaron on 5/30/17.
 */

class GameScreen
/**
 * @param size false(3x3) true(4x4)
 * *
 * @param mode false(casual) true(timed)
 * *
 * @param tutorial Is it a tutorial?
 */
(private val game: SWITCH, private val size: Boolean, private val mode: Boolean, private var tutorial: Boolean) : ScreenAdapter() {

    private val guiCam: OrthographicCamera = OrthographicCamera(SWITCH.WIDTH, SWITCH.HEIGHT)
    private val textCam: OrthographicCamera
    private val manager: TweenManager
    private var squareSize: Float = 0.toFloat()
    private val boardSize: Float
    private val squareSpacing: Float
    private val touchPoint = Vector3()
    private val clock: Vector3
    private var selectedSquare: Vector2? = null
    private val grid: Array<Array<Square?>>
    private val backButtonRender: Rectangle
    private val backButtonTouch: Rectangle
    private val timer = Vector2() //y not used, using vector2 for tweening
    private var text = Vector3() //x,y=center pos z=opacity
    private var doUpdate = false
    private var score = -1
    private val maxTime = 20f
    private var textLayout: GlyphLayout? = null
    private val currentTutorialTap: Vector2 = Vector2()
    private var tutorialTapSize: Vector2 = Vector2(0f, 0f) //y not used, using vector2 for tweening
    private var currentTutorialStep = 0

    init {
        guiCam.position.set(-SWITCH.WIDTH / 2, SWITCH.HEIGHT / 2, 0f)

        textCam = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        textCam.position.set((-Gdx.graphics.width / 2).toFloat(), Gdx.graphics.height / 2f, 0f)

        timer.x = maxTime


        grid = Array(if (size) 4 else 3) { arrayOfNulls<Square?>(if (size) 4 else 3) }


        boardSize = SWITCH.WIDTH * 0.8f
        squareSize = boardSize / grid.size
        squareSpacing = squareSize * 0.1f
        squareSize -= squareSpacing
        tutorialTapSize.x = squareSize*0.4f

        for (x in grid.indices)
            for (y in grid.indices) { //Square pos is at the center of the square for scaling
                grid[x][y] = Square()
                grid[x][y]?.renderRectangle = Rectangle(SWITCH.WIDTH * 0.5f - boardSize / 2 + (x + 0.5f) * squareSize + (x + 0.5f) * squareSpacing,
                        SWITCH.HEIGHT * 0.5f - boardSize / 2 + (y + 0.5f) * squareSize + (y + 0.5f) * squareSpacing, squareSize, squareSize)
                grid[x][y]?.touchRectangle = Rectangle(SWITCH.WIDTH * 0.5f - boardSize / 2 + x * squareSize + (x + 0.5f) * squareSpacing,
                        SWITCH.HEIGHT * 0.5f - boardSize / 2 + y * squareSize + (y + 0.5f) * squareSpacing, squareSize, squareSize)
            }

        if(!tutorial)
            populateGrid()
        else
            populateTutorial()

        backButtonRender = Rectangle(20 + SWITCH.WIDTH * 0.1f, SWITCH.HEIGHT - 20f - SWITCH.WIDTH * 0.1f, -SWITCH.WIDTH * 0.1f, SWITCH.WIDTH * 0.1f)
        backButtonTouch = Rectangle(20f, SWITCH.HEIGHT - 20f - SWITCH.WIDTH * 0.1f, SWITCH.WIDTH * 0.1f, SWITCH.WIDTH * 0.1f)

        val clockRadius = SWITCH.WIDTH * 0.1f
        clock = Vector3(SWITCH.WIDTH / 2, SWITCH.HEIGHT * 0.15f, clockRadius)

        text = Vector3((Gdx.graphics.width / 2).toFloat(), if (mode) Gdx.graphics.height * 0.83f else Gdx.graphics.height * 0.17f, 1f)
        updateScore(false)

        manager = TweenManager()
        Util.slideCamera(guiCam, Util.Direction.CENTER, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.OUT, 0f, null)
        Util.slideCamera(textCam, Util.Direction.CENTER, manager, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), Quart.OUT, 0f) { _, _ ->
            doUpdate = true
        }

        if(tutorial){
            val touchTipTween = Tween.to(tutorialTapSize, Vector2Accessor.TYPE_XY, 1.0f)
                    .ease(Quad.INOUT)
                    .repeatYoyo(Tween.INFINITY, 0f)
            touchTipTween.target(squareSize*0.25f, 0f)
            touchTipTween.start(manager)
        }
    }

    override fun render(delta: Float) {
        update()
        draw()
    }

    private fun populateGrid() {
        val color = Util.rand.nextInt(3)
        for (x in grid.indices)
            for (y in 0..grid[x].size - 1)
                grid[x][y]?.setColor(color)

        val mixUp = 20 + Util.rand.nextInt(20)
        for (i in 0..mixUp - 1) {
            val x = Util.rand.nextInt(this.grid.size)
            val y = Util.rand.nextInt(this.grid.size)
            val change = if (Math.random() <= 0.5) "x" else "y"
            var x2 = if (change === "x") -1 else x
            var y2 = if (change === "y") -1 else y
            if (change === "x")
                while (x2 < 0 || x2 >= this.grid.size)
                    x2 = x + if (Util.rand.nextBoolean()) -1 else 1
            else
                while (y2 < 0 || y2 >= this.grid.size)
                    y2 = y + if (Util.rand.nextBoolean()) -1 else 1
            if (this.grid[x][y]?.colorID == this.grid[x2][y2]?.colorID) {
                this.grid[x][y]?.setColor(Util.colorChanges[this.grid[x][y]!!.colorID][0])
                this.grid[x2][y2]?.setColor(Util.colorChanges[this.grid[x2][y2]!!.colorID][1])
            }
        }
    }

    private fun populateTutorial() {
        for (x in grid.indices)
            for (y in 0..grid[x].size - 1)
                grid[x][y]?.setColor(1)
        grid[0][2]?.setColor(0)
        grid[1][2]?.setColor(2)
        grid[2][0]?.setColor(2)
        grid[2][1]?.setColor(0)
        currentTutorialTap.set(0f,2f)
    }

    private fun update() {
        manager.update(Gdx.graphics.deltaTime)
        if (!doUpdate) return
        timer.x -= Gdx.graphics.deltaTime
        if (mode && timer.x <= 0)
            lose(true)
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (backButtonTouch.contains(touchPoint.x, touchPoint.y)) {
                doUpdate = false
                Util.slideCamera(guiCam, Util.Direction.RIGHT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0f, null)
                Util.slideCamera(textCam, Util.Direction.RIGHT, manager, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), Quart.IN, 0f) { _, _ ->
                    game.screen = MainMenuScreen(game)
                }
            }

            for (x in grid.indices)
                for(y in grid.indices){
                    if(!grid[x][y]?.touchRectangle!!.contains(touchPoint.x, touchPoint.y)) continue
                    if(tutorial && (x != currentTutorialTap.x.toInt() || y != currentTutorialTap.y.toInt())) continue
                    else{
                        nextTouchPoint()
                    }
                    if (selectedSquare == null) {
                        selectedSquare = Vector2(x.toFloat(), y.toFloat())
                        grid[x][y]?.setSizeWithTween(squareSize * 0.85f, 0.15f)?.start(manager)
                    } else {
                        val square2 = grid[selectedSquare!!.x.toInt()][selectedSquare!!.y.toInt()]
                        square2?.setSizeWithTween(squareSize, 0.15f)?.start(manager)
                        //Check if square is adjacent to the selected square
                        if (square2?.colorID != grid[x][y]?.colorID && Math.sqrt(Math.pow((x - selectedSquare!!.x).toDouble(), 2.0) + Math.pow((y - selectedSquare!!.y).toDouble(), 2.0)) == 1.0) {
                            val color = Util.reverseColorChanges[grid[x][y]?.colorID.toString() + "," + square2?.colorID]
                            grid[x][y]?.setColorWithTween(color!!)?.start(manager)
                            square2?.setColorWithTween(color!!)?.start(manager)

                            if (checkGrid()) {
                                selectedSquare = null
                                win()
                                return
                            }
                        }
                        selectedSquare = null
                    }
                }
        }
    }

    fun nextTouchPoint(){
        currentTutorialStep++
        when(currentTutorialStep){
            1 -> currentTutorialTap.set(1f, 2f)
            2 -> currentTutorialTap.set(2f, 0f)
            3 -> currentTutorialTap.set(2f, 1f)
            4 -> {
                tutorial = false
            }
        }
    }

    private fun lose(showGameOver: Boolean) {
        doUpdate = false
        animateSquares(false, TweenCallback { type, source ->
            Util.slideCamera(guiCam, Util.Direction.RIGHT, manager, SWITCH.WIDTH, SWITCH.HEIGHT, Quart.IN, 0f, null)
            Util.slideCamera(textCam, Util.Direction.RIGHT, manager, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), Quart.IN, 0f) { type, source ->
                if (!showGameOver)
                    game.screen = MainMenuScreen(game)
                else
                    game.screen = GameOverScreen(game, score, size, mode)
            }
        })
    }

    private fun win() {
        doUpdate = false
        updateScore(true)
        Tween.to(timer, Vector2Accessor.TYPE_XY, 0.6f + 0.05f * grid.size.toFloat() * 2f)
                .target(maxTime, 0f)
                .ease(Bounce.OUT)
                .start(manager)
        animateSquares(false, TweenCallback { _, _ ->
            populateGrid()
            animateSquares(true, TweenCallback { _, _ -> doUpdate = true })
        })
    }

    private fun animateSquares(`in`: Boolean, callback: TweenCallback?) {
        val gridSize = if (size) 4 else 3
        var t: Tween
        for (x in grid.indices)
            for (y in grid.indices) {
                if (`in`)
                    t = grid[x][y]?.setSizeWithTween(squareSize, 0.3f)?.ease(Back.OUT)?.delay((x + -y + gridSize + 1) * 0.05f)?.start(manager)!!
                else
                    t = grid[x][y]?.setSizeWithTween(0f, 0.3f)?.ease(Back.IN)?.delay((x + -y + gridSize + 1) * 0.05f)?.start(manager)!!
                if (callback != null && x == grid.size - 1 && y == 0)
                    t.setCallback(callback)
            }
    }

    private fun checkGrid(): Boolean {
        val col = grid[0][0]?.colorID
        for (x in grid.indices)
            for (y in grid.indices)
                if (grid[x][y]?.colorID != col)
                    return false
        return true
    }

    private fun draw() {
        val gl = Gdx.gl
        gl.glClearColor(0.13333f, 0.13333f, 0.13333f, 1f)
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        guiCam.update()
        textCam.update()

        game.batch?.projectionMatrix = guiCam.combined
        game.shape?.projectionMatrix = guiCam.combined

        game.batch?.begin()
        for (x in grid.indices)
            for (y in grid.indices) {
                game.batch?.color = grid[x][y]?.color
                val square = grid[x][y]?.renderRectangle
                game.batch?.draw(Assets.getTexture("square"), square!!.x - square.width / 2f, square.y - square.height / 2f, square.width, square.height)
            }
        game.batch?.setColor(1f, 1f, 1f, 1f)
        game.batch?.draw(Assets.getTexture("squarePlay"), backButtonRender.x, backButtonRender.y, backButtonRender.width, backButtonRender.height)
        game.batch?.end()

        if (mode) {
            game.shape?.begin(ShapeRenderer.ShapeType.Filled)
            game.shape?.setColor(1f, 1f, 1f, 1f)
            game.shape?.arc(clock.x, clock.y, clock.z, 90f, 361f, 100)
            game.shape?.setColor(0.13333f, 0.13333f, 0.13333f, 1f)
            game.shape?.arc(clock.x, clock.y, clock.z * 0.85f, 90f, 361f, 100)
            game.shape?.setColor(1f, 1f, 1f, 1f)
            val angle = Math.toRadians(timer.x / maxTime.toDouble() * 360.0 + 90)
            val cos = Math.cos(angle).toFloat()
            val sin = Math.sin(angle).toFloat()
            game.shape?.rectLine(clock.x, clock.y, clock.x + cos * clock.z * 0.6f, clock.y + sin * clock.z * 0.6f, clock.z * 0.15f)
            game.shape?.rectLine(clock.x, clock.y, clock.x, clock.y + clock.z * 0.6f, clock.z * 0.15f)
            game.shape?.arc(clock.x, clock.y, clock.z * 0.075f, 90f, 361f, 100)
            game.shape?.end()
        }

        game.batch?.projectionMatrix = textCam.combined
        game.batch?.begin()
        val col = 0.13333f + 0.86667f * Math.max(Math.min(text.z, 1f), 0f)
        game.gamemodeRobotoLight!!.setColor(col, col, col, 1f)
        game.gamemodeRobotoLight!!.draw(game.batch, "" + score, text.x - textLayout!!.width / 2, text.y + textLayout!!.height / 2)
        game.batch?.end()

        if(tutorial){
            Gdx.gl.glEnable(GL20.GL_BLEND)
            game.shape?.begin(ShapeRenderer.ShapeType.Filled)
            game.shape?.setColor(1f, 1f, 1f, 0.5f)
            val renderRectangle = grid[currentTutorialTap.x.toInt()][currentTutorialTap.y.toInt()]!!.renderRectangle!!
            game.shape?.circle(renderRectangle.x, renderRectangle.y, tutorialTapSize.x, 50)
            game.shape?.end()
            Gdx.gl.glDisable(GL20.GL_BLEND)
        }
    }

    fun updateScore(animate: Boolean) {
        if (animate) {
            Tween.to(text, Vector3Accessor.TYPE_XYZ, 0.3f + 0.05f * grid.size)
                    .target(text.x, text.y - textLayout!!.height * 0.3f, -1f)
                    .setCallback { type, source ->
                        text.y += textLayout!!.height * 0.6f
                        score++
                        textLayout = GlyphLayout(game.gamemodeRobotoLight, "" + score)
                        Tween.to(text, Vector3Accessor.TYPE_XYZ, 0.3f + 0.05f * grid.size)
                                .target(text.x, text.y - textLayout!!.height * 0.3f, 1f)
                                .ease(Quad.OUT)
                                .start(manager)
                    }
                    .ease(Quad.IN)
                    .start(manager)
        } else {
            score++
            this.textLayout = GlyphLayout(game.gamemodeRobotoLight, "" + score)
        }
    }
}
