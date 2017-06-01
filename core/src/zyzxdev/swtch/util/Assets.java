package zyzxdev.swtch.util;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class Assets{
	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();

	public static void addTexture(String name, String texture){
		textures.put(name, new Texture(texture));
	}

	public static Texture getTexture(String name){
		return textures.get(name);
	}
}
