package me.dogeon.unhash.util;

import java.io.File;
import java.util.Locale;

public class MCHome {

	public static String get() {

		String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		File home = new File(System.getProperty("user.home"));

		String mcp;

		if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
			mcp = "Library/Application Support/minecraft";
		} else if (OS.indexOf("win") >= 0) {
			mcp = "AppData/Roaming/.minecraft";
		} else if (OS.indexOf("nux") >= 0) {
			mcp = ".minecraft";
		} else {
			return null;
		}
		return new File(home, mcp).toString();
	}
}