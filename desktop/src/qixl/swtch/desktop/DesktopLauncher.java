package qixl.swtch.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import qixl.swtch.SWITCH;
import qixl.swtch.nativeInterface.AdManager;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new SWITCH(new DesktopAdManager()), config);
	}

	static class DesktopAdManager implements AdManager{
		@Override
		public void showInterstitial() {
			System.out.println("Showing interstitial");
		}
	}
}
