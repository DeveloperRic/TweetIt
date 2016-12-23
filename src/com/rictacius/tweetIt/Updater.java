package com.rictacius.tweetIt;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;

public class Updater {
	public static String newVersion;

	public Updater() {
	}

	public static boolean check() {
		newVersion = null;
		try {
			URL url = new URL("http://rictacius.bplaced.net/pages/plugins/download/");

			URLConnection con = url.openConnection();
			InputStream is = con.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String line = null;

			while ((line = br.readLine()) != null) {
				if (line.startsWith("TweetIt")) {
					String version = line.split(":")[1];
					if (!version.equals(Main.pl.getDescription().getVersion())) {
						newVersion = version;
						return true;
					}
				}
			}
		} catch (Exception e) {
			Methods.sendColoredMessage(Main.pl, ChatColor.AQUA, ("Could not check for updates!"), ChatColor.RED);
			Methods.sendColoredMessage(Main.pl, ChatColor.AQUA, ("Trace:"), ChatColor.RED);
			e.printStackTrace();
		}
		return false;
	}

	public static boolean download() {
		try {
			URL website = new URL("http://rictacius.bplaced.net/pages/plugins/download/TweetIt/" + newVersion
					+ "/TweetIt.jar");
			File toSave = new File("plugins/TweetIt.jar");
			FileUtils.copyURLToFile(website, toSave, 30000, 30000);
		} catch (Exception e) {
			Methods.sendColoredMessage(Main.pl, ChatColor.AQUA, ("Could not download update (v" + newVersion + ")"), ChatColor.RED);
			Methods.sendColoredMessage(Main.pl, ChatColor.AQUA, ("Trace:"), ChatColor.RED);
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
