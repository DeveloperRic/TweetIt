package com.rictacius.tweetIt.utils;

public class TweetItException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3091062228741719278L;

	public TweetItException() {
		super();
	}

	public TweetItException(String message) {
		super(message);
	}

	public TweetItException(String message, Throwable cause) {
		super(message, cause);
	}

	public TweetItException(Throwable cause) {
		super(cause);
	}

}
