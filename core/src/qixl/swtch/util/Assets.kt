package qixl.swtch.util

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture

import java.util.HashMap
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.assets.loaders.FileHandleResolver



object Assets {
    private val textures = HashMap<String, String>()
    private val immediateTextures = HashMap<String, Texture>()
    var assetManager = AssetManager()

    init {
        val resolver = InternalFileHandleResolver()
        assetManager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
    }

    fun addTextureImmediately(name: String, texture: String) {
        immediateTextures.put(name, Texture(texture))
    }

    fun addTexture(name: String, texture: String){
        textures.put(name, texture)
        assetManager.load(texture, Texture::class.java)
    }

    fun getTexture(name: String): Texture? {
        if(textures.containsKey(name))
            return assetManager.get(textures[name])
        else
            return immediateTextures[name]
    }
}
