package com.rictacius.tweetIt.user;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.rictacius.tweetIt.Main;
import com.rictacius.tweetIt.events.TweetItEvent;
import com.rictacius.tweetIt.utils.TweetItException;
import com.rictacius.tweetIt.utils.TweetItException.EEvent;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.User;

/**
 * Class that represents an Instance of a twitter user
 * 
 * @author RictAcius
 *
 */
public class TwitterUser {
	private TwitterUserType type;
	private UUID owner;
	private String iD;
	private String pin;
	private boolean authenticated;
	private OAuthSignpostClient client;

	/**
	 * <p>
	 * Creates a new twitter user
	 * </p>
	 * <p>
	 * <p>
	 * <b><u>Note:</u></b> The username of the user may never be known, and it
	 * does not need to be. If you would need the username of the user, you
	 * would need to request it from the user.
	 * </p>
	 * <p>
	 * <strong>You may set the username to <i>null</i> if you have to.</strong>
	 * </p>
	 * 
	 * @param type
	 *            the type of user {@link TwitterUserType}
	 * @param username
	 *            the username of the twitter user
	 */
	public TwitterUser(TwitterUserType type, String username) {
		this.setType(type);
		this.setId(username);
	}

	/**
	 * <p>
	 * Creates a new twitter user
	 * </p>
	 * <p>
	 * <p>
	 * <b><u>Note:</u></b> The username of the user may never be known, and it
	 * does not need to be. If you would need the username of the user, you
	 * would need to request it from the user.
	 * </p>
	 * <p>
	 * <strong>You may set the username to <i>null</i> if you have to.</strong>
	 * </p>
	 * 
	 * @param type
	 *            the type of user {@link TwitterUserType}
	 * @param username
	 *            the username of the twitter user The UUID of the player to
	 *            recieve messages.
	 */
	public TwitterUser(TwitterUserType type, String username, UUID owner) {
		this.setType(type);
		this.setId(username);
		this.owner = owner;
	}

	public TwitterUser(TweetItEvent.EventType eventType, TwitterUserType type, String username) {
		if (!type.equals(TwitterUserType.EVENT)) {
			try {
				throw new TweetItException.EEvent(eventType,
						"You cannot use this contructor if you are not making a TweetIt Event!");
			} catch (EEvent e) {
				e.printStackTrace();
			}
		}
		this.setType(type);
		this.setId(username);
	}

	/**
	 * @return the type of user
	 */
	public TwitterUserType getType() {
		return type;
	}

	/**
	 * @param type
	 *            - the usertype to set
	 */
	public void setType(TwitterUserType type) {
		this.type = type;
	}

	/**
	 * @return the username of the user.
	 */
	public String getId() {
		return iD;
	}

	/**
	 * A duplicated function for {@link #getId()}
	 * 
	 * @return the username of the twitter user
	 */
	public String getUsername() {
		return iD;
	}

	/**
	 * @param id
	 *            - the id to set for this user
	 */
	public void setId(String id) {
		this.iD = id;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	/**
	 * <p>
	 * <b><u>Sends a message to the TwitterUser</u></b>
	 * </p>
	 * <p>
	 * Will send a player message if the TwitterUser is of TwitterUserType
	 * PLAYER
	 * <p>
	 * <p>
	 * Will send a console message if the TwitterUser is of TwitterUserType
	 * SYSTEM
	 * <p>
	 * <p>
	 * for all other TwitterUserTypes a console message will be send including
	 * the id of the TwitterUser
	 * <p>
	 * 
	 * @param message
	 * @throws TweetItException
	 *             if the TwitterUser is of TwitterUserType PLAYER but the
	 *             player is not online
	 */
	@SuppressWarnings("deprecation")
	public void message(String message) {
		if (type == TwitterUserType.PLAYER || type == TwitterUserType.DISPOSABLE_PLAYER
				|| type == TwitterUserType.OTHER) {
			if (owner == null) {
				try {
					Bukkit.getPlayer(UUID.fromString(iD)).sendMessage(message);
				} catch (Exception e) {
				}
			} else {
				try {
					Bukkit.getPlayer(owner).sendMessage(message);
				} catch (Exception e) {
				}
			}
		} else if (type == TwitterUserType.SYSTEM) {
			Bukkit.getConsoleSender().sendMessage(message);
		}
	}

	/**
	 * @return authenticated - lets dependant plugins know this user is ready.
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * @param authenticated
	 *            changes the suthentication status of this user
	 */
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	/**
	 * @return the oauthclient
	 */
	public OAuthSignpostClient getClient() {
		return client;
	}

	/**
	 * <strong>DO NOT EDIT THIS UNLESS YOU KNOW WHAT YOU ARE DOING!</strong>
	 * 
	 * @param client
	 *            - the oauthclient to set
	 */
	public void setClient(OAuthSignpostClient client) {
		this.client = client;
	}

	/**
	 * @return the pin of the twitter user
	 */
	public String getPin() {
		return pin;
	}

	/**
	 * @param pin
	 *            the userpin to set
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}

	/**
	 * Gets a list of twitter users that are linked to the current user.
	 * 
	 * @return a list of TwitterUsers or null if the process was unsucessful
	 */
	@SuppressWarnings("deprecation")
	public List<TwitterUser> getAttachments() {
		List<TwitterUser> attachments = new ArrayList<TwitterUser>();
		if (type == TwitterUserType.TEMPORARY || type == TwitterUserType.DISPOSABLE_PLAYER) {
			return attachments;
		}
		Main plugin = Main.pl;
		File defaultFile = new File(plugin.getDataFolder(), "/default-user.yml");
		File file = new File(plugin.getDataFolder(), "/users/" + iD + ".yml");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			plugin.saveResource("default-user.yml", false);
			FileConfiguration dconfig = new YamlConfiguration();
			dconfig = UserLoader.loadConfig(dconfig, defaultFile);
			if (dconfig == null) {
				return null;
			}
			if (!UserLoader.saveConfig(dconfig, file)) {
				return null;
			}
			defaultFile.delete();
		}
		FileConfiguration config = new YamlConfiguration();
		config = UserLoader.loadConfig(config, file);
		if (config == null) {
			return null;
		}
		if (config.getStringList("attachments") == null) {
			return null;
		}
		for (String attachmentID : config.getStringList("attachments")) {
			TwitterUser attachment = UserLoader.getUsers().get(attachmentID);
			attachments.add(attachment);
		}
		return attachments;
	}

	/**
	 * 
	 * @param attachments
	 *            the list of users to attach to
	 * @return a boolean representing whether or not this operation was
	 *         successful
	 */
	public boolean setAttachments(List<TwitterUser> attachments) {
		Main plugin = Main.pl;
		File defaultFile = new File(plugin.getDataFolder(), "/default-user.yml");
		File file = new File(plugin.getDataFolder(), "/users/" + iD + ".yml");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			plugin.saveResource("default-user.yml", false);
			FileConfiguration dconfig = new YamlConfiguration();
			dconfig = UserLoader.loadConfig(dconfig, defaultFile);
			if (dconfig == null) {
				return false;
			}
			if (!UserLoader.saveConfig(dconfig, file)) {
				return false;
			}
			defaultFile.delete();
		}
		FileConfiguration config = new YamlConfiguration();
		config = UserLoader.loadConfig(config, file);
		if (config == null) {
			return false;
		}
		config.set("attachments", attachments);
		return UserLoader.saveConfig(config, file);
	}

	/**
	 * Attempts to generate a User instance for this TwitterUser
	 * 
	 * @return a User instance equal() to the actual TwitterUser or null if a
	 *         username is not set
	 */
	public User generateUser() {
		try {
			Twitter twitter = new Twitter(iD, client);
			return twitter.getSelf();
		} catch (Exception e) {
			return null;
		}
	}
}
