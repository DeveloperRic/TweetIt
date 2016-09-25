package com.rictacius.tweetIt.events;

import com.rictacius.tweetIt.operations.TweetItFinal;
import com.rictacius.tweetIt.user.TwitterUser;

import winterwell.jtwitter.Message;

public class UserSentDirectMessageEvent extends Cancelable {
	private Message message;

	public UserSentDirectMessageEvent(TwitterUser primary, Message message) {
		super(EventType.USER_SENT_DIRECT_MESSAGE, primary);
		this.message = message;
		deliver();
	}

	@Override
	public void setCanceled(boolean toCancel) {
		addRespondent(toCancel);
	}

	@Override
	public void finalise() {
		TweetItFinal operation = new TweetItFinal(primary);
		operation.deleteDirectMessage(message.getId());
	}

	/**
	 * 
	 * @return the direct message
	 */
	public Message getMessage() {
		return message;
	}

}
