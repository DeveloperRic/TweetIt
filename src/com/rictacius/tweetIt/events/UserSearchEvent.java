package com.rictacius.tweetIt.events;

import java.util.List;

import com.rictacius.tweetIt.user.TwitterUser;

import winterwell.jtwitter.Status;

public class UserSearchEvent extends TweetItEvent {
	private List<Status> result;

	public UserSearchEvent(TwitterUser primary, List<Status> result) {
		super(EventType.USER_SEARCH, primary);
		this.result = result;
	}

	/**
	 * 
	 * @return the result of the search
	 */
	public List<Status> getResult() {
		return result;
	}

}
