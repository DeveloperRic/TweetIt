package com.rictacius.tweetIt.events;

import com.rictacius.tweetIt.operations.TweetItFinal;
import com.rictacius.tweetIt.user.TwitterUser;

import winterwell.jtwitter.Status;

public class UserSentTweetEvent extends Cancelable {
	private Status tweet;

	public UserSentTweetEvent(TwitterUser primary, Status tweet) {
		super(EventType.USER_SENT_TWEET, primary);
		this.tweet = tweet;
		deliver();
	}

	/**
	 * 
	 * @return the new tweet
	 */
	public Status getTweet() {
		return tweet;
	}

	@Override
	public void finalise() {
		TweetItFinal operation = new TweetItFinal(primary);
		operation.deleteTweet(tweet.getId());
	}

	@Override
	public void setCanceled(boolean toCancel) {
		addRespondent(toCancel);
	}

}
