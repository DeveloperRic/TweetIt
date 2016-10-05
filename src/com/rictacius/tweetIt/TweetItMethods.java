package com.rictacius.tweetIt;

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
	 * Returns the {@link TwitterUser} or requests tokens for a new user.
	 * 
	 * @param ID
	 *            the ID of the user
	 * @param type
	 *            the type of the user (if a new one needs to be created)
	 * @return a TwitterUSer if found or null if a new one is being made.
	 */
	public static final TwitterUser getUser(String ID, TwitterUserType type) {
		TwitterUser user = UserLoader.getUser(ID);
		if (user == null) {
			user = new TwitterUser(type, ID, null);
			try {
				getUserAuthorizer().requestTokens(user);
			} catch (Exception e) {
				getLogger().log("Could not create new user of type {" + type + "} with id {" + ID + "}",
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
	 * @param ID
	 *            the ID of the user
	 * @param type
	 *            the type of the user (if a new one needs to be created)
	 * @return a TwitterUSer if found or null if a new one is being made.
	 */
	public static final TwitterUser getTempUser(String ID) {
		TwitterUser user = UserLoader.getTempUser(ID);
		if (user == null) {
			user = new TwitterUser(TwitterUserType.DISPOSABLE_PLAYER, ID, null);
			try {
				getUserAuthorizer().requestTokens(user);
			} catch (Exception e) {
				getLogger().log("Could not create new temporary user of type {" + TwitterUserType.DISPOSABLE_PLAYER
						+ "} with id {" + ID + "}", Log.Level.WARNING, e);
				e.printStackTrace();
			}
		}
		return UserLoader.getTempUser(ID);
	}

}
