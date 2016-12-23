package com.rictacius.tweetIt;

import java.util.UUID;

import com.rictacius.tweetIt.auth.UserAuthorizer;
import com.rictacius.tweetIt.user.TwitterUser;
import com.rictacius.tweetIt.user.TwitterUserType;
import com.rictacius.tweetIt.user.UserLoader;
import com.rictacius.tweetIt.utils.Log;

public class TweetItMethods {
	public static final String getConsumerKey() {
		return Main.consumerKey;
	}

	public static final String getConsumerSecret() {
		return Main.consumerSecret;
	}

	public static final UserAuthorizer getUserAuthorizer() {
		return Main.auth;
	}

	public static final Log getLogger() {
		return Main.logger;
	}

	/**
	 * <p>
	 * Returns the {@link TwitterUser} or requests tokens for a new user.
	 * </p>
	 * <p>
	 * If a new user needs to be created the {@link TwitterUserType} will be
	 * defaulted to PLAYER
	 * </p>
	 * 
	 * @param username
	 *            the username of the user
	 * @return a {@link TwitterUser} if found or null if a new one is being
	 *         made.
	 * @see #getUser(String, TwitterUserType, UUID)
	 */
	public static final TwitterUser getUser(String username) {
		return getUser(username, TwitterUserType.PLAYER, null);
	}

	/**
	 * Returns the {@link TwitterUser} or requests tokens for a new user.
	 * 
	 * @param username
	 *            the username of the user
	 * @param type
	 *            the type of the user (if a new one needs to be created)
	 * @return a {@link TwitterUser} if found or null if a new one is being
	 *         made.
	 * @see #getUser(String, TwitterUserType, UUID)
	 */
	public static final TwitterUser getUser(String username, TwitterUserType type) {
		return getUser(username, type, null);
	}

	/**
	 * Returns the {@link TwitterUser} or requests tokens for a new user.
	 * 
	 * @param username
	 *            the username of the user
	 * @param type
	 *            the type of the user (if a new one needs to be created)
	 * @param owner
	 *            the UUID of the player to recieve messages - can be null. (if
	 *            a new one needs to be created)
	 * @return a {@link TwitterUser} if found or null if a new one is being
	 *         made.
	 */
	public static final TwitterUser getUser(String username, TwitterUserType type, UUID owner) {
		TwitterUser user = UserLoader.getUser(username);
		if (user == null) {
			user = new TwitterUser(type, username, owner);
			try {
				getUserAuthorizer().requestTokens(user);
			} catch (Exception e) {
				getLogger().log("Could not create new user of type {" + type + "} with id {" + username + "}",
						Log.Level.WARNING, e);
				e.printStackTrace();
			}
			return null;
		}
		return user;
	}

	/**
	 * Returns the temporary {@link TwitterUser} or requests tokens for a new
	 * temporary user.
	 * 
	 * @param username
	 *            the username of the user
	 * @return a TwitterUSer if found or null if a new one is being made.
	 */
	@Deprecated
	public static final TwitterUser getTempUser(String username) {
		TwitterUser user = UserLoader.getTempUser(username);
		if (user == null) {
			user = new TwitterUser(TwitterUserType.DISPOSABLE_PLAYER, username, null);
			try {
				getUserAuthorizer().requestTokens(user);
			} catch (Exception e) {
				getLogger().log("Could not create new temporary user of type {" + TwitterUserType.DISPOSABLE_PLAYER
						+ "} with id {" + username + "}", Log.Level.WARNING, e);
				e.printStackTrace();
			}
		}
		return UserLoader.getTempUser(username);
	}

}
