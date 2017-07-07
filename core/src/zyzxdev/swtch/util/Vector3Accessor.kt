package zyzxdev.swtch.util

import com.badlogic.gdx.math.Vector3

import aurelienribon.tweenengine.TweenAccessor

/**
 * Created by aaron on 5/29/17.
 */

class Vector3Accessor : TweenAccessor<Vector3> {

    override fun getValues(target: Vector3, tweenType: Int, returnValues: FloatArray): Int {
        when (tweenType) {
            TYPE_XYZ -> {
                returnValues[0] = target.x
                returnValues[1] = target.y
                returnValues[2] = target.z
                return 3
            }

            else -> {
                assert(false)
                return -1
            }
        }
    }

    override fun setValues(target: Vector3, tweenType: Int, newValues: FloatArray) {
        when (tweenType) {
            TYPE_XYZ -> {
                target.x = newValues[0]
                target.y = newValues[1]
                target.z = newValues[2]
            }
            else -> assert(false)
        }
    }

    companion object {
        val TYPE_XYZ = 1
    }
}