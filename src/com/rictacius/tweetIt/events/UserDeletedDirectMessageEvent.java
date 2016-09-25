package com.rictacius.tweetIt.events;

import com.rictacius.tweetIt.user.TwitterUser;

import winterwell.jtwitter.Message;

public class UserDeletedDirectMessageEvent extends TweetItEvent {
	private Message message;

	public UserDeletedDirectMessageEvent(TwitterUser primary, Message message) {
		super(EventType.USER_DELETED_DIRECT_MESSAGE, primary);
		this.message = message;
	}

	/**
	 * 
	 * @return the deleted direct message
	 */
	public Message getMessage() {
		return message;
	}

}
