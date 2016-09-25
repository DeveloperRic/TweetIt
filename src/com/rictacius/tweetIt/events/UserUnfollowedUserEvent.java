package com.rictacius.tweetIt.events;

import com.rictacius.tweetIt.operations.TweetItFinal;
import com.rictacius.tweetIt.user.TwitterUser;

public class UserUnfollowedUserEvent extends TwinUserEvent {

	public UserUnfollowedUserEvent(TwitterUser primary, TwitterUser source) {
		super(EventType.USER_UNFOLLOWED_USER, primary, source);
	}

	@Override
	public void setCanceled(boolean toCancel) {
		addRespondent(toCancel);
	}

	@Override
	public void finalise() {
		TweetItFinal op = new TweetItFinal(primary);
		op.followUser(source);
	}

}
