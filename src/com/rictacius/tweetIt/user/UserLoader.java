package com.rictacius.tweetIt.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.rictacius.tweetIt.Main;
import com.rictacius.tweetIt.utils.TweetItException;
import com.rictacius.tweetIt.utils.config.ConfigType;
import com.rictacius.tweetIt.utils.config.ConfigUpdater;

import winterwell.jtwitter.OAuthSignpostClient;

/**
 * Class that handles storage and retrieval of user info while offline.
 * 
 * @author RictAcius
 *
 */
public class UserLoader {
	private static HashMap<String, TwitterUser> users = new HashMap<String, TwitterUser>();
	private static Main plugin = Main.pl;

	public UserLoader() {
		loadUsers();
	}

	public static FileConfiguration loadConfig(FileConfiguration config, File file) {
		try {
			config.load(file);
		} catch (FileNotFoundException e) {
			Main.logger.log("[TweetIt] Could not load User file " + file.getName()
					+ " make sure you do not edit user files manually." + " Reset the user config and report the bug.",
					3);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Main.logger.log("[TweetIt] Could not load User file " + file.getName()
					+ " make sure you do not edit user files manually." + " Reset the user config and report the bug.",
					3);
			e.printStackTrace();
			return null;
		} catch (InvalidConfigurationException e) {
			Main.logger.log("[TweetIt] Could not load User file " + file.getName()
					+ " make sure you do not edit user files manually." + " Reset the user config and report the bug.",
					3);
			e.printStackTrace();
			return null;
		}
		return config;
	}

	public static boolean saveConfig(FileConfiguration config, File file) {
		try {
			config.save(file);
		} catch (IOException e) {
			Main.logger.log("[TweetIt] Could not save User file! Please report this!", 3);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void loadUsers() {
		users.clear();
		File defaultFile = new File(plugin.getDataFolder(), "/default-user.yml");
		if (!defaultFile.exists()) {
			defaultFile.getParentFile().mkdirs();
			plugin.saveResource("default-user.yml", false);
		}
		FileConfiguration dconfig = new YamlConfiguration();
		dconfig = loadConfig(dconfig, defaultFile);
		if (dconfig == null) {
			return;
		}
		File folder = new File(plugin.getDataFolder(), "/users");
		folder.mkdirs();
		for (File userFile : folder.listFiles()) {
			if (userFile.isFile() && !userFile.isDirectory()) {
				FileConfiguration config = new YamlConfiguration();
				config = loadConfig(config, userFile);
				if (config == null) {
					return;
				}
				config = ConfigUpdater.updateOtherConfig(userFile.getName().replaceAll(".yml", ""), config,
						ConfigType.USER);
				if (!saveConfig(config, userFile)) {
					return;
				}
				String id = userFile.getName().replaceAll(".yml", "");
				String username = config.getString("username");
				String pin = config.getString("pin");
				boolean authenticated = Boolean.parseBoolean(config.getString("authenticated"));
				TwitterUserType type = null;
				if (config.getString("type").equalsIgnoreCase("player")) {
					type = TwitterUserType.PLAYER;
				} else if (config.getString("type").equalsIgnoreCase("system")) {
					type = TwitterUserType.SYSTEM;
				} else if (config.getString("type").equalsIgnoreCase("other")) {
					type = TwitterUserType.OTHER;
				} else {
					Main.logger.log("[TweetIt] Could not load User file " + userFile.getName()
							+ " (TwitterUserType is invalid) make sure you do not edit user files manually."
							+ " Reset the user config and report the bug.", 3);
				}
				OAuthSignpostClient client = null;
				try {
					client = new OAuthSignpostClient(Main.consumerKey, Main.consumerSecret,
							Main.auth.getAccessToken(id), Main.auth.getAccessTokenSecret(id));
				} catch (GeneralSecurityException e) {
					Main.logger.log("[TweetIt] Could not load User file " + userFile.getName()
							+ " (Could not decrypt user keys) make sure you do not edit user files manually."
							+ " Reset the user config and report the bug.", 3);
					e.printStackTrace();
				} catch (IOException e) {
					Main.logger.log("[TweetIt] Could not load User file " + userFile.getName()
							+ " (Could not access user keys) make sure you do not edit user files manually."
							+ " Reset the user config and report the bug.", 3);
					e.printStackTrace();
				} catch (TweetItException e) {
					Main.logger.log("[TweetIt] Could not load User file " + userFile.getName()
							+ " (User has not authnticated TweetIt for thier twitter account!) make sure you do not edit user files manually."
							+ " Reset the user config and report the bug.", 3);
					userFile.delete();
				}
				TwitterUser user = new TwitterUser(type, id, username);
				user.setAuthenticated(authenticated);
				user.setClient(client);
				user.setPin(pin);
				users.put(id, user);
			}
		}
		defaultFile.delete();
	}

	/**
	 * @return the users
	 */
	public static HashMap<String, TwitterUser> getUsers() {
		return users;
	}

	public static void storeUser(TwitterUser user) {
		File defaultFile = new File(plugin.getDataFolder(), "/default-user.yml");
		File file = new File(plugin.getDataFolder(), "/users/" + user.getId() + ".yml");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			plugin.saveResource("default-user.yml", false);
			FileConfiguration dconfig = new YamlConfiguration();
			dconfig = loadConfig(dconfig, defaultFile);
			if (dconfig == null) {
				return;
			}
			if (!saveConfig(dconfig, file)) {
				return;
			}
			defaultFile.delete();
		}
		FileConfiguration config = new YamlConfiguration();
		try {
			config.load(file);
		} catch (FileNotFoundException e) {
			Main.logger.log("[TweetIt] Could not create User file! Please report this!", 3);
			e.printStackTrace();
		} catch (IOException e) {
			Main.logger.log("[TweetIt] Could not load User file! Please report this!"
					+ " Please reset your default config by deleting it!", 3);
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			Main.logger.log("[TweetIt] Could not load User file! Please report this!"
					+ " Please reset your default config by deleting it!", 3);
			e.printStackTrace();
		}
		config.set("username", user.getUsername());
		config.set("authenticated", user.isAuthenticated());
		config.set("type", user.getType().name());
		config.set("pin", user.getPin());
		try {
			config.save(file);
		} catch (IOException e) {
			Main.logger.log("[TweetIt] Could not save User file! Please report this!", 3);
			e.printStackTrace();
		}
		users.put(user.getId(), user);
	}
}
