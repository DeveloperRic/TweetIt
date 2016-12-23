package com.rictacius.tweetIt;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.rictacius.tweetIt.Updater;
import com.rictacius.tweetIt.auth.TokenRequester;
import com.rictacius.tweetIt.auth.TokenRequesterChild;
import com.rictacius.tweetIt.auth.UserAuthorizer;
import com.rictacius.tweetIt.commands.TweetItCommand;
import com.rictacius.tweetIt.user.TwitterUser;
import com.rictacius.tweetIt.user.UserLoader;
import com.rictacius.tweetIt.utils.Log;
import com.rictacius.tweetIt.utils.PinParser;

public class Main extends JavaPlugin implements Listener {
	PluginDescriptionFile pdfFile = getDescription();

	public static Main pl;

	public void onEnable() {
		pl = this;
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Registering Config...."), ChatColor.YELLOW);
		createFiles();
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Registering Utils...."), ChatColor.YELLOW);
		registerUtils();
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Registering Commands...."), ChatColor.YELLOW);
		registerCommands();
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Registering Events...."), ChatColor.YELLOW);
		registerEvents();
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Checking for updates...."), ChatColor.YELLOW);
		boolean update = Updater.check();
		if (update) {
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Found update (v" + Updater.newVersion + ")."), ChatColor.GREEN);
			if (config.getString("auto-update") == null) {
				config.set("auto-update", true);
				saveConfig();
			}
			if (Boolean.parseBoolean(config.getString("auto-update"))) {
				Methods.sendColoredMessage(this, ChatColor.AQUA, ("Auto-updating AuctionRoom..."), ChatColor.YELLOW);
				Updater.download();
				Methods.sendColoredMessage(this, ChatColor.AQUA,
						("Downloaded update (v" + Updater.newVersion + ") Please restart your server to install it!"),
						ChatColor.GREEN);
			}
		} else {
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("TweetIt is up to date."), ChatColor.GREEN);
		}
		Methods.sendColoredMessage(this, ChatColor.AQUA,
				(pdfFile.getName() + " has been enabled! (V." + pdfFile.getVersion() + ")"), ChatColor.GREEN);
	}

	public static String consumerKey = "";
	public static String consumerSecret = "";
	public static UserAuthorizer auth = null;
	public static Log logger = null;

	public void registerUtils() {
		try {
			PinParser.load();
			consumerKey = getConfig().getString("system.consumerKey");
			consumerSecret = getConfig().getString("system.consumerSecret");
			auth = new UserAuthorizer(Integer.parseInt(getConfig().getString("auth.pinLength")),
					Integer.parseInt(getConfig().getString("auth.pinTimeout")));
			logger = new Log();
			UserLoader.loadUsers();
		} catch (Exception e) {
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Error while registering utils!"), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Trace:"), ChatColor.RED);
			e.printStackTrace();
		}
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Utils successfuly registered!"), ChatColor.LIGHT_PURPLE);
	}

	public void onDisable() {
		for (TwitterUser user : UserLoader.getUsers().values()) {
			UserLoader.storeUser(user);
		}
		Methods.sendColoredMessage(this, ChatColor.AQUA,
				(pdfFile.getName() + " has been disabled! (V." + pdfFile.getVersion() + ")"), ChatColor.YELLOW);
	}

	public void registerCommands() {
		try {
			getCommand("tweetit").setExecutor(new TweetItCommand());
		} catch (Exception e) {
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Error while registering commands!"), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Trace:"), ChatColor.RED);
			e.printStackTrace();
		}
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Commands successfuly registered!"), ChatColor.LIGHT_PURPLE);
	}

	public void registerEvents() {
		try {
		} catch (Exception e) {
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Error while registering events!"), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Trace:"), ChatColor.RED);
			e.printStackTrace();
		}
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Events successfuly registered!"), ChatColor.LIGHT_PURPLE);
	}

	public void registerTokenEvents(TokenRequester requester) {
		try {
			PluginManager pm = getServer().getPluginManager();
			pm.registerEvents(requester, this);
		} catch (Exception e) {
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Error while registering events!"), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Trace:"), ChatColor.RED);
			e.printStackTrace();
		}
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Events successfuly registered!"), ChatColor.LIGHT_PURPLE);
	}

	public void registerTokenEvents(TokenRequesterChild tokenRequesterChild) {
		try {
			PluginManager pm = getServer().getPluginManager();
			pm.registerEvents(tokenRequesterChild, this);
		} catch (Exception e) {
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Error while registering events!"), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Trace:"), ChatColor.RED);
			e.printStackTrace();
		}
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Events successfuly registered!"), ChatColor.LIGHT_PURPLE);
	}

	public void registerConfig() {
		try {
			getConfig().options().copyDefaults(true);
			saveConfig();

		} catch (Exception e) {
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Error while registering config!"), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Trace:"), ChatColor.RED);
			e.printStackTrace();
		}
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Config successfuly registered!"), ChatColor.LIGHT_PURPLE);
	}

	public static Plugin getPlugin() {
		return Bukkit.getServer().getPluginManager().getPlugin("Exteria_Utilities");
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private File configf;
	private FileConfiguration config;

	public int reloadPlugin() {
		createFiles();
		int errors = 0;
		ArrayList<String> errorFiles = new ArrayList<String>();
		String file = "";
		ArrayList<StackTraceElement[]> traces = new ArrayList<StackTraceElement[]>();
		StackTraceElement[] trace = null;
		try {
			this.reloadConfig();
		} catch (Exception e) {
			errors++;
			trace = e.getStackTrace();
			traces.add(trace);
			file = "Main Config File";
			errorFiles.add(file);
		}
		try {
			registerUtils();
		} catch (Exception e) {
			errors++;
			trace = e.getStackTrace();
			traces.add(trace);
			file = "TweetIt Utilities";
			errorFiles.add(file);
		}
		if (errors > 0) {
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Could not reload all config files!"), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("The following files generated erros:"), ChatColor.RED);
			for (String fileName : errorFiles) {
				Methods.sendColoredMessage(this, ChatColor.GOLD, (ChatColor.GRAY + " - " + ChatColor.RED + fileName),
						ChatColor.RED);
			}
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Trace(s):"), ChatColor.RED);
			for (StackTraceElement[] currentTrace : traces) {
				int i = 0;
				Methods.sendColoredMessage(this, ChatColor.GOLD,
						(ChatColor.GRAY + "* " + ChatColor.RED + errorFiles.get(i)), ChatColor.RED);
				for (StackTraceElement printTrace : currentTrace) {
					Methods.sendColoredMessage(this, ChatColor.GOLD, (printTrace.toString()), ChatColor.RED);
				}
				i++;
			}
		}
		return errors;
	}

	public void saveAllConfigFiles() {
		saveConfig();
		try {
		} catch (Exception ex) {
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Could not save config to "), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Trace:"), ChatColor.RED);
			ex.printStackTrace();
		}
	}

	private void createFiles() {
		try {
			configf = new File(getDataFolder(), "config.yml");

			if (!configf.exists()) {
				configf.getParentFile().mkdirs();
				saveResource("config.yml", false);
			}

			config = new YamlConfiguration();
			try {
				config.load(configf);
			} catch (Exception e) {
				Methods.sendColoredMessage(this, ChatColor.LIGHT_PURPLE, ("Error while registering config!"),
						ChatColor.RED);
				e.printStackTrace();
			}
			getConfig().options().copyDefaults(false);
		} catch (Exception e) {
			Methods.sendColoredMessage(this, ChatColor.LIGHT_PURPLE, ("Error while registering config!"),
					ChatColor.RED);
			e.printStackTrace();
		}
	}

}
