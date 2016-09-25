package com.rictacius.tweetIt.events;

import com.rictacius.tweetIt.operations.TweetItFinal;
import com.rictacius.tweetIt.user.TwitterUser;

import winterwell.jtwitter.Status;

public class UserRetweetEvent extends Cancelable {
	private Status original;
	private Status retweet;
	private boolean isQuote;

	public UserRetweetEvent(TwitterUser primary, Status original, Status retweet, boolean isQuote) {
		super(EventType.USER_RETWEET_TWEET, primary);
		this.original = original;
		this.retweet = retweet;
		this.isQuote = isQuote;
	}

	@Override
	public void setCanceled(boolean toCancel) {
		addRespondent(toCancel);
	}

	@Override
	public void finalise() {
		TweetItFinal op = new TweetItFinal(primary);
		op.deleteTweet(retweet.getId());
	}

	/**
	 * 
	 * @return the original tweet
	 */
	public Status getOriginalTweet() {
		return original;
	}

	/**
	 * 
	 * @return the retweet
	 */
	public Status getRetweetTweet() {
		return retweet;
	}

	/**
	 * 
	 * @return true if quote, false if retweet
	 */
	public boolean isQuote() {
		return isQuote;
	}

}
