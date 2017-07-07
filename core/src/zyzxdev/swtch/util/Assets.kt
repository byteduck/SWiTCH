package zyzxdev.swtch.util

import com.badlogic.gdx.graphics.Texture

import java.util.HashMap

object Assets {
    private val textures = HashMap<String, Texture>()

    fun addTexture(name: String, texture: String) {
        textures.put(name, Texture(texture))
    }

    fun getTexture(name: String): Texture? {
        return textures[name]
    }
}
