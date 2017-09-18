package zyzxdev.swtch

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle

import aurelienribon.tweenengine.Tween
import zyzxdev.swtch.util.ColorAccessor
import zyzxdev.swtch.util.RectangleAccessor
import zyzxdev.swtch.util.Util

/**
 * Created by aaron on 5/30/17.
 */

class Square {
    var color: Color = Color.WHITE
    var touchRectangle: Rectangle? = null
    var renderRectangle: Rectangle? = null
    var colorID: Int = 0
    var tweenA: Tween? = null
    var tweenB: Tween? = null
    var visible = true

    fun setColor(color: Int) {
        this.colorID = color
        this.color = Color(Util.colors[colorID])
    }

    fun setColorWithTween(colorID: Int): Tween {
        this.colorID = colorID
        return Tween
                .to(color, ColorAccessor.TYPE_RGB, 0.2f)
                .target(Util.colors[colorID].r, Util.colors[colorID].g, Util.colors[colorID].b)
    }

    fun setSizeWithTween(size: Float, time: Float): Tween {
        return Tween
                .to(renderRectangle, RectangleAccessor.TYPE_SIZE, time)
                .target(size)
    }
}
