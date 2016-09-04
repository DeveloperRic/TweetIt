package com.rictacius.tweetIt;

import com.rictacius.tweetIt.auth.UserAuthorizer;
import com.rictacius.tweetIt.utils.Log;

public class TweetItMethods {
	public static String getConsumerKey() {
		return Main.consumerKey;
	}

	public static String getConsumerSecret() {
		return Main.consumerSecret;
	}

	public static UserAuthorizer getUserAuthorizer() {
		return Main.auth;
	}

	public static Log getLogger() {
		return Main.logger;
	}
}
