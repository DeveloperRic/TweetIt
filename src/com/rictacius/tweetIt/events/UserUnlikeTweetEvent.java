package com.rictacius.tweetIt.events;

import com.rictacius.tweetIt.operations.TweetItFinal;
import com.rictacius.tweetIt.user.TwitterUser;

import winterwell.jtwitter.Status;

public class UserUnlikeTweetEvent extends Cancelable {
	private Status tweet;

	public UserUnlikeTweetEvent(TwitterUser primary, Status tweet) {
		super(EventType.USER_UNLIKE_TWEET, primary);
		this.tweet = tweet;
	}

	@Override
	public void setCanceled(boolean toCancel) {
		addRespondent(toCancel);
	}

	@Override
	public void finalise() {
		TweetItFinal op = new TweetItFinal(primary);
		op.setFavoriteTweet(tweet, true);
	}

	/**
	 * 
	 * @return the unliked tweet
	 */
	public Status getTweet() {
		return tweet;
	}

}
