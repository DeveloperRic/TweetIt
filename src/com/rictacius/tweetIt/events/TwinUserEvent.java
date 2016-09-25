package com.rictacius.tweetIt.events;

import com.rictacius.tweetIt.user.TwitterUser;

/**
 * Defines an event involving two twitter accounts
 * @author RictAcius
 *
 */
public abstract class TwinUserEvent extends Cancelable {
	protected TwitterUser source;

	/**
	 * Creates a new TwinUser event
	 * @param eventType the type of event
	 * @param primary the user that caused the event
	 * @param source the involved user
	 */
	public TwinUserEvent(EventType eventType, TwitterUser primary, TwitterUser source) {
		super(eventType, primary);
		this.source = source;
	}

	/**
	 * 
	 * @return the involved user
	 */
	public TwitterUser getSourceUser() {
		return source;
	}
}
