package com.rictacius.tweetIt.utils;

import java.lang.reflect.Method;

import com.rictacius.tweetIt.events.TweetItEvent;
import com.rictacius.tweetIt.user.TwitterUser;

public class TweetItException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3091062228741719278L;

	public TweetItException() {
		super();
	}

	public TweetItException(String message) {
		super(message);
	}

	public TweetItException(String message, Throwable cause) {
		super(message, cause);
	}

	public TweetItException(Throwable cause) {
		super(cause);
	}

	public static class EAuthentication extends TweetItException {
		private static final long serialVersionUID = 1L;

		public EAuthentication(String failedUsername, String message) {
			super("AuthError=" + failedUsername + "] " + message);
		}
	}

	public static class EUser extends TweetItException {
		private static final long serialVersionUID = 1L;

		public EUser(TwitterUser failedUser, String message) {
			super("User=" + ((failedUser.getId().length() > 20) ? failedUser.getUsername() : failedUser.getId()) + "] "
					+ message);
		}
	}

	public static class ERate extends TweetItException {
		private static final long serialVersionUID = 1L;

		public ERate(String message) {
			super("[RateLimit] " + message);
		}
	}

	public static class EOperation extends TweetItException {
		private static final long serialVersionUID = 1L;

		public EOperation(com.rictacius.tweetIt.operations.Operational instance, String message) {
			super("[Operational {User=" + instance.getUser().getId() + ",Task=" + instance.getClass().getSimpleName()
					+ "} ] " + message);
		}
	}

	public static class EListener extends TweetItException {
		private static final long serialVersionUID = 1L;

		public EListener(Class<?> listener, Method method, String message) {
			super("[Listener {Class=" + listener.getName() + ",Method=" + method.getName() + "} ] " + message);
		}
	}

	public static class EEvent extends TweetItException {
		private static final long serialVersionUID = 1L;

		public EEvent(TweetItEvent.EventType type, String message) {
			super("[Event {Type=" + type.toString() + "} ] " + message);
		}
	}

}
