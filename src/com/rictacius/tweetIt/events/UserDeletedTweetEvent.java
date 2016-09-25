package com.rictacius.tweetIt.events;

import com.rictacius.tweetIt.user.TwitterUser;

import winterwell.jtwitter.Status;

public class UserDeletedTweetEvent extends TweetItEvent {
	private Status tweet;

	public UserDeletedTweetEvent(TwitterUser primary, Status tweet) {
		super(EventType.USER_DELETED_TWEET, primary);
		this.tweet = tweet;
	}

	/**
	 * 
	 * @return the deleted tweet
	 */
	public Status getTweet() {
		return tweet;
	}

}
