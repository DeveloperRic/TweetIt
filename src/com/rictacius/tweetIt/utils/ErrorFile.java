package com.rictacius.tweetIt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jpaste.pastebin.Pastebin;

import com.rictacius.tweetIt.Main;

import net.md_5.bungee.api.ChatColor;

/**
 * Class to create error files.
 * 
 * @author RictAcius
 *
 */
public class ErrorFile {
	private static Main plugin = Main.pl;
	public static String prefix = ChatColor.translateAlternateColorCodes('&', "&b&l[&aTweetIt&b&l] &r");
	public static ConsoleCommandSender console = Bukkit.getConsoleSender();

	/**
	 * Generates error info, writes them and creates a shareable link.
	 * 
	 * @param error
	 * @return a bastebin link to the error.
	 */
	public static ArrayList<String> writeError(String error) {
		ArrayList<String> returnl = new ArrayList<String>();
		try {
			Date now = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE-MMMM-d-yyyy-HH-mm");
			String date = formatter.format(now);
			File errorFile = new File(plugin.getDataFolder().getPath() + "/errors/" + date + ".yml");
			File defaultfile = new File(plugin.getDataFolder().getPath() + "/default-error.yml");
			if (!defaultfile.exists()) {
				errorFile.getParentFile().mkdirs();
				defaultfile.getParentFile().mkdirs();
				plugin.saveResource("default-error.yml", true);
				console.sendMessage(prefix + defaultfile.getPath());
			}
			FileConfiguration config = new YamlConfiguration();
			try {
				config.load(defaultfile);
			} catch (FileNotFoundException e) {
				console.sendMessage(prefix + "Could not find error file path, did you modify the jar contents?");
				e.printStackTrace();
			} catch (IOException e) {
				console.sendMessage(prefix + "Could not load error file, does the file exisit?");
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				console.sendMessage(prefix + "Could not load error file did you edit the files contents?");
				e.printStackTrace();
			}
			config.options().copyDefaults(true);
			config.set("error", error);
			config.set("date", date);
			String serverName = Bukkit.getServerName();
			String serverType = Bukkit.getServer().getName();
			String serverIP = Bukkit.getServer().getIp();
			if (serverIP == null) {
				serverIP = "Unknown";
			} else if (serverIP.equals("")) {
				serverIP = "Unknown";
			}
			int serverPort = Bukkit.getServer().getPort();
			String serverVersion = Bukkit.getServer().getVersion();
			String motdmVersion = plugin.getDescription().getVersion();
			String configFile = plugin.getConfig().saveToString();
			Plugin[] plugins = Bukkit.getServer().getPluginManager().getPlugins();
			for (Plugin plugin : plugins) {
				if (Bukkit.getServer().getPluginManager().getPlugin(plugin.getName()).isEnabled()) {
					config.set("plugins." + plugin.getName(), "enabled");
				} else {
					config.set("plugins." + plugin.getName(), "disabled");
				}
			}
			config.set("server-name", serverName);
			config.set("server-type", serverType);
			config.set("server-ip", serverIP + " : " + serverPort);
			config.set("server-version", serverVersion);
			config.set("motdm-version", motdmVersion);
			config.set("files.config", configFile);
			List<Plugin> dependants = new ArrayList<Plugin>();
			for (Plugin plugin : plugins) {
				List<String> depandancies = plugin.getDescription().getDepend();
				if (depandancies.contains(plugin.getDescription().getName())) {
					dependants.add(plugin);
				}
			}
			for (Plugin plugin : dependants) {
				if (Bukkit.getServer().getPluginManager().getPlugin(plugin.getName()).isEnabled()) {
					config.set("dependants." + plugin.getName(), "enabled");
				} else {
					config.set("dependants." + plugin.getName(), "disabled");
				}
			}
			List<String> timeline = new ArrayList<String>();
			for (String status : Main.logger.getTimeline()) {
				timeline.add(ChatColor.stripColor(status));
			}
			config.set("timeline", timeline);
			config.save(errorFile);
			defaultfile.delete();
			returnl.add(errorFile.getAbsolutePath());
			URL link = Pastebin.pastePaste("707d4468afc6923cb547cc3eb5a44297", config.saveToString(),
					"MOTDManager Dump File");
			returnl.add(link.toString());
			return returnl;
		} catch (Exception e) {
			console.sendMessage(prefix + "Could not write error dump file did you edit the configs correctly?");
			e.printStackTrace();
		}
		return null;
	}
}
