package com.rictacius.tweetIt.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.rictacius.tweetIt.Main;
import com.rictacius.tweetIt.utils.Log;
import com.rictacius.tweetIt.utils.TweetItException;
import com.rictacius.tweetIt.utils.TweetItException.EListener;

public class ClassListener implements Listener {
	protected static HashMap<Object, HashMap<Method, TweetItEvent.EventType>> listeners = new HashMap<Object, HashMap<Method, TweetItEvent.EventType>>();
	protected static HashMap<Object, HashMap<Method, Priority>> priorities = new HashMap<Object, HashMap<Method, Priority>>();

	@EventHandler
	public void onEnable(PluginEnableEvent event) {
		Plugin plugin = event.getPlugin();
		String name = Main.pl.getDescription().getName();
		List<String> dependencies = plugin.getDescription().getDepend();
		dependencies = (dependencies == null) ? dependencies = new ArrayList<String>() : dependencies;
		if (dependencies.contains(name)) {
		}
	}

	/**
	 * Registers a new class and its event methods.
	 * 
	 * @param listener
	 * @return
	 * @throws EListener
	 */
	public static <T extends TweetItListener> int registerEvents(Object listener) throws EListener {
		HashMap<Method, TweetItEvent.EventType> registered = new HashMap<Method, TweetItEvent.EventType>();
		HashMap<Method, Priority> priority = new HashMap<Method, Priority>();
		Method[] methods = listener.getClass().getMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(TweetItEventHandler.class)) {
				TweetItEventHandler annotInstance = method.getAnnotation(TweetItEventHandler.class);
				TweetItEvent.EventType type = annotInstance.event();
				Priority p = annotInstance.priority();
				if (type == null) {
					throw new TweetItException.EListener(listener.getClass(), method,
							"Could not find EventType parameter in @TweetItListener annotation!");
				}
				registered.put(method, type);
				priority.put(method, p);
			}
		}
		if (registered.size() > 0) {
			ClassListener.listeners.put(listener, registered);
			ClassListener.priorities.put(listener, priority);
			Main.logger.log("Registered events in listener " + listener.getClass().getName(), Log.Level.INFO);
		}
		return registered.size();
	}
}
