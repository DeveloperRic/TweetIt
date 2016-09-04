package com.rictacius.tweetIt.user;

import java.util.UUID;

import org.bukkit.Bukkit;

import com.rictacius.tweetIt.utils.TweetItException;

import net.md_5.bungee.api.ChatColor;
import winterwell.jtwitter.OAuthSignpostClient;

/**
 * Class that represents an Instance of a twitter user
 * 
 * @author RictAcius
 *
 */
public class TwitterUser {
	private TwitterUserType type;
	private String id;
	private String username;
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
	 * 
	 * @param type
	 * @param id
	 */
	public TwitterUser(TwitterUserType type, String id) {
		this.setType(type);
		this.setId(id);
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
	 * @return the id of the user.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            - the id to set for this user
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the username of the TwitterUser
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * <strong>DO NOT EDIT THIS UNLESS YOU KNOW WHAT YOU ARE DOING!</strong>
	 * 
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
	public void message(String message) throws TweetItException {
		if (type == TwitterUserType.PLAYER) {
			try {
				Bukkit.getPlayer(UUID.fromString(id)).sendMessage(message);
			} catch (Exception e) {
				throw new TweetItException("You cannot send a message to an offline player!", e.getCause());
			}
		} else if (type == TwitterUserType.SYSTEM) {
			Bukkit.getConsoleSender().sendMessage(message);
		} else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "(" + id + ")" + ChatColor.RESET + message);
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
}
