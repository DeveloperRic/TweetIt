package com.rictacius.tweetIt;

import com.rictacius.tweetIt.auth.UserAuthorizer;
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
	
}
