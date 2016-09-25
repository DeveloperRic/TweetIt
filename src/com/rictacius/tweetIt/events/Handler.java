package com.rictacius.tweetIt.events;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.rictacius.tweetIt.events.TweetItEvent.EventType;

/**
 * Class representing an external listener and its instance
 * 
 * @author RictAcius
 *
 */
public class Handler {
	private Class<?> listener;
	private Object instance;
	private HashMap<Method, TweetItEvent.EventType> methods;
	private HashMap<Method, Priority> priority;

	public Handler(Object listener, HashMap<Method, TweetItEvent.EventType> methods,
			HashMap<Method, Priority> priority) {
		this.listener = listener.getClass();
		this.methods = methods;
		this.instance = listener;
		this.priority = priority;
	}

	/**
	 * 
	 * @return the class the listener is from
	 */
	public Class<?> getListeningClass() {
		return listener;
	}

	/**
	 * 
	 * @return the methods with their respective {@link EventType} this class
	 *         entails
	 */
	public HashMap<Method, TweetItEvent.EventType> getDeclaredMethods() {
		return methods;
	}

	/**
	 * 
	 * @return the object instance of this class that is used to deliver events
	 */
	public Object getInstance() {
		return instance;
	}

	/**
	 *
	 * @return the priority of the method
	 */
	public Priority getPriority(Method method) {
		return priority.get(method);
	}

}
