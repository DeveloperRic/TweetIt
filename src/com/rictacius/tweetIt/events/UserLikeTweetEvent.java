package com.rictacius.tweetIt.events;

import com.rictacius.tweetIt.operations.TweetItFinal;
import com.rictacius.tweetIt.user.TwitterUser;

import winterwell.jtwitter.Status;

public class UserLikeTweetEvent extends Cancelable {
	private Status tweet;

	public UserLikeTweetEvent(TwitterUser primary, Status tweet) {
		super(EventType.USER_LIKE_TWEET, primary);
		this.tweet = tweet;
	}

	@Override
	public void setCanceled(boolean toCancel) {
		addRespondent(toCancel);
	}

	@Override
	public void finalise() {
		TweetItFinal op = new TweetItFinal(primary);
		op.setFavoriteTweet(tweet, false);
	}

	/**
	 * 
	 * @return the liked tweet
	 */
	public Status getTweet() {
		return tweet;
	}

}
