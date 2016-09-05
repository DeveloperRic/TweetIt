package com.rictacius.tweetIt.utils.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.rictacius.tweetIt.Main;

public class ConfigUpdater {
	private static Main plugin = Main.pl;
	private static int latestVersion = 2;

	public static FileConfiguration updateOtherConfig(String name, FileConfiguration config, ConfigType type) {
		int currentVersion = Integer.parseInt(plugin.getConfig().getString("config-version"));
		if (currentVersion < latestVersion) {
			if (type == ConfigType.USER) {
				if (currentVersion < 2) {
					List<String> attachments = new ArrayList<String>();
					attachments.add(name);
					config.set("attachments", attachments);
				}
			}
		}
		return config;
	}
}
