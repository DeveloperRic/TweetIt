package com.rictacius.tweetIt.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.rictacius.tweetIt.Main;
import com.rictacius.tweetIt.utils.Log;
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
	@Deprecated
	private static HashMap<String, TwitterUser> tempUsers = new HashMap<String, TwitterUser>();
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
					Log.Level.FATAL);
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Main.logger.log("[TweetIt] Could not load User file " + file.getName()
					+ " make sure you do not edit user files manually." + " Reset the user config and report the bug.",
					Log.Level.FATAL);
			e.printStackTrace();
			return null;
		} catch (InvalidConfigurationException e) {
			Main.logger.log("[TweetIt] Could not load User file " + file.getName()
					+ " make sure you do not edit user files manually." + " Reset the user config and report the bug.",
					Log.Level.FATAL);
			e.printStackTrace();
			return null;
		}
		return config;
	}

	public static boolean saveConfig(FileConfiguration config, File file) {
		try {
			config.save(file);
		} catch (IOException e) {
			Main.logger.log("[TweetIt] Could not save User file! Please report this!", Log.Level.FATAL);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void loadUsers() {
		users.clear();
		File folder = new File(plugin.getDataFolder(), "/users");
		folder.mkdirs();
		for (File userFile : folder.listFiles()) {
			if (userFile.isFile() && !userFile.isDirectory()) {
				try {
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
					String pin = config.getString("pin");
					boolean authenticated = Boolean.parseBoolean(config.getString("authenticated"));
					TwitterUserType type = TwitterUserType.valueOf(config.getString("type"));
					OAuthSignpostClient client = null;
					try {
						client = new OAuthSignpostClient(Main.consumerKey, Main.consumerSecret,
								Main.auth.getAccessToken(id), Main.auth.getAccessTokenSecret(id));
					} catch (GeneralSecurityException e) {
						Main.logger.log("[TweetIt] Could not load User file " + userFile.getName()
								+ " (Could not decrypt user keys) make sure you do not edit user files manually."
								+ " Reset the user config and report the bug.", Log.Level.FATAL);
						e.printStackTrace();
					} catch (IOException e) {
						Main.logger.log("[TweetIt] Could not load User file " + userFile.getName()
								+ " (Could not access user keys) make sure you do not edit user files manually."
								+ " Reset the user config and report the bug.", Log.Level.FATAL);
						e.printStackTrace();
					} catch (TweetItException e) {
						Main.logger.log("[TweetIt] Could not load User file " + userFile.getName()
								+ " (User has not authnticated TweetIt for thier twitter account!) make sure you do not edit user files manually."
								+ " Reset the user config and report the bug.", Log.Level.FATAL);
						userFile.delete();
					}
					UUID owner = null;
					try {
						if (config.getString("owner") != null) {
							owner = UUID.fromString(config.getString("owner"));
						}
					} catch (Exception e) {
					}
					TwitterUser user = new TwitterUser(type, id, owner);
					user.setAuthenticated(authenticated);
					user.setClient(client);
					user.setPin(pin);
					users.put(id, user);
				} catch (Exception e) {
					Main.logger.log("[TweetIt] Could not load User " + userFile.getName()
							+ " (detected outdated or corrupted user file) Resetting user.", Log.Level.FATAL);
					userFile.delete();
				}
			}
		}
	}

	/**
	 * @return the users
	 */
	public static HashMap<String, TwitterUser> getUsers() {
		return users;
	}

	@Deprecated
	public static void storeTempUser(TwitterUser user) {
		tempUsers.put(user.getId(), user);
	}

	public static TwitterUser getUser(String ID) {
		return users.get(ID);
	}

	@Deprecated
	public static TwitterUser getTempUser(String ID) {
		if (tempUsers.containsKey(ID)) {
			TwitterUser user = tempUsers.get(ID);
			tempUsers.remove(ID);
			try {
				Main.auth.clearAccessToken(ID);
			} catch (IOException e) {
				Main.logger.log("Could not clear tokens for temporary user=" + ID, Log.Level.FATAL, e);
				e.printStackTrace();
			}
			return user;
		}
		return null;
	}

	public static void storeUser(TwitterUser user) {
		File file = new File(plugin.getDataFolder(), "/users/" + user.getId() + ".yml");
		FileConfiguration config = new YamlConfiguration();
		config.set("username", null);
		config.set("authenticated", user.isAuthenticated());
		config.set("type", user.getType().toString());
		config.set("pin", user.getPin());
		try {
			config.set("owner", user.getOwner().toString());
		} catch (Exception e) {
		}
		file.getParentFile().mkdirs();
		try {
			config.save(file);
		} catch (IOException e) {
			Main.logger.log("[TweetIt] Could not save User file! Please report this!", Log.Level.FATAL);
			e.printStackTrace();
		}
		users.put(user.getId(), user);
		Main.logger.log("Saved user details for user=" + user.getId(), Log.Level.INFO);
	}
}
