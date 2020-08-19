package me.dogeon.unhash.core;

import java.io.File;
import java.util.Locale;

public class Variables {

	public static String mchome = get_mch();
	public static String mcversion = null;
	public static int fclimit = -1;
	public static boolean writeHashtable = false;
	public static boolean flagUnhashing = false;

	private static String get_mch() {

		String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		File home = new File(System.getProperty("user.home"));
		String mcp;

		if (OS.contains("mac") || OS.contains("darwin")) mcp = "Library/Application Support/minecraft";
		else if (OS.contains("win")) mcp = "AppData/Roaming/.minecraft";
		else if (OS.contains("nux")) mcp = ".minecraft";
		else return null;

		return new File(home, mcp).toString();
	}

}