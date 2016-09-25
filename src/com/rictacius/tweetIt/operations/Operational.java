package com.rictacius.tweetIt.operations;

import com.rictacius.tweetIt.user.TwitterUser;
import com.rictacius.tweetIt.utils.TweetItException;

import winterwell.jtwitter.Twitter;

public class Operational {
	protected Twitter twitter;
	protected TwitterUser user;
	protected boolean isProtected;

	public Operational(TwitterUser user) {
		this.user = user;
		this.isProtected = false;
		twitter = new Twitter(user.getUsername(), user.getClient());
	}

	public Operational(TwitterUser user, boolean isProtected) {
		this.user = user;
		this.isProtected = isProtected;
		twitter = new Twitter(user.getUsername(), user.getClient());
	}

	/**
	 * 
	 * @return the user who owns the operatons
	 */
	public TwitterUser getUser() {
		return user;
	}

	/**
	 * 
	 * @return the {@link Twitter} API instance used in this operation
	 */
	public Twitter getAPI() {
		return twitter;
	}

	protected boolean isProtected() {
		return isProtected;
	}

	protected void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}

	public void exit() throws TweetItException {
		if (!isProtected) {
			throw new TweetItException.EOperation(this,
					"Cannot exit a TweetIt instance if the instance is not set to protected!");
		}

	}

}
