package com.rictacius.tweetIt.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.rictacius.tweetIt.Main;
import com.rictacius.tweetIt.user.TwitterUser;
import com.rictacius.tweetIt.utils.Log;
import com.rictacius.tweetIt.utils.TweetItException;
import com.rictacius.tweetIt.utils.TweetItException.EListener;

/**
 * Defines a TweetIt event
 * 
 * @author RictAcius
 *
 */
public abstract class TweetItEvent {

	/**
	 * Enum Constants for every available TweetItEvent
	 * 
	 * @author RictAcius
	 *
	 */
	public enum EventType {
		USER_SENT_TWEET, USER_RETWEET_TWEET, USER_SENT_DIRECT_MESSAGE, USER_FOLLOWED_USER, USER_UNFOLLOWED_USER, USER_DELETED_DIRECT_MESSAGE, USER_DELETED_TWEET, USER_SEARCH, USER_LIKE_TWEET, USER_UNLIKE_TWEET;

		private String type = "";

		private EventType() {
			this.type = type + "";
		}
	}

	private EventType eventType;
	protected TwitterUser primary;

	/**
	 * 
	 * @return the user that caused the event
	 */
	public TwitterUser getPrimaryUser() {
		return primary;
	}

	/**
	 * Creates a new TweetIt event
	 * 
	 * @param eventType
	 *            the type of event
	 * @param primary
	 *            the user that caused the event
	 */
	public TweetItEvent(EventType eventType, TwitterUser primary) {
		this.eventType = eventType;
		this.primary = primary;
		Main.logger.log("Created new TweetIt Event {event=" + eventType.toString() + "}", Log.Level.INFO);
	}

	/**
	 * 
	 * @return the type of TweetIt event
	 * @see #toString()
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * Returns the eventType as a string
	 * 
	 * @see #getEventType()
	 */
	@Override
	public String toString() {
		return eventType.toString();
	}

	/**
	 * Delivers the event to all listeners in order of priority
	 */
	public void deliver() {
		Main.logger.log("Started delivering event {event=" + eventType.toString() + "} ", Log.Level.INFO);
		deliverEvent(Priority.HIGH);
		deliverEvent(Priority.NORMAL);
		deliverEvent(Priority.LOW);
		deliverEvent(Priority.MONITOR);
		Main.logger.log("Event delivering sequence complete! {event=" + eventType.toString() + "} ", Log.Level.INFO);
	}

	private void deliverEvent(Priority listenerPriority) {
		for (Handler handler : getHandlers()) {
			for (Method method : handler.getDeclaredMethods().keySet()) {
				Priority priority = handler.getPriority(method);
				if (priority.equals(listenerPriority)) {
					EventType type = handler.getDeclaredMethods().get(method);
					if (type.equals(eventType)) {
						try {
							method.invoke(handler.getInstance(), this);
							Main.logger.log(
									"Delivered event {event=" + eventType.toString() + "} to "
											+ handler.getListeningClass().getName() + " (" + method.getName() + ")",
									Log.Level.INFO);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
							try {
								throw new TweetItException.EListener(handler.getListeningClass(), method,
										"Could not deliver event " + eventType.toString());
							} catch (EListener e1) {
								Main.logger.log(
										"Could not deliver event {event=" + eventType.toString() + "} to "
												+ handler.getListeningClass().getName() + " (" + method.getName() + ")",
										Log.Level.FATAL, e1);
								e1.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @return a list of handlers that listen for this event specifically
	 * @see #getHandlerList()
	 */
	public List<Handler> getHandlers() {
		List<Handler> handlers = new ArrayList<Handler>();
		for (Handler handler : getHandlerList()) {
			for (Method method : handler.getDeclaredMethods().keySet()) {
				if (handler.getDeclaredMethods().get(method).equals(eventType)) {
					handlers.add(handler);
				}
			}
		}
		return handlers;
	}

	/**
	 * 
	 * @return a list of all handlers
	 * @see #getHandlers()
	 */
	public static List<Handler> getHandlerList() {
		List<Handler> handlers = new ArrayList<Handler>();
		for (Object listener : ClassListener.listeners.keySet()) {
			Handler handler = new Handler(listener, ClassListener.listeners.get(listener),
					ClassListener.priorities.get(listener));
			handlers.add(handler);
		}
		return handlers;
	}
}
