package com.rictacius.tweetIt.events;

import com.rictacius.tweetIt.operations.TweetItFinal;
import com.rictacius.tweetIt.user.TwitterUser;

public class UserFollowedUserEvent extends TwinUserEvent {

	public UserFollowedUserEvent(TwitterUser primary, TwitterUser source) {
		super(TweetItEvent.EventType.USER_FOLLOWED_USER, primary, source);
		deliver();
	}

	@Override
	public void setCanceled(boolean toCancel) {
		addRespondent(toCancel);
	}

	@Override
	public void finalise() {
		TweetItFinal operation = new TweetItFinal(primary);
		operation.stopFollowingUser(source);
	}

}
