package com.rictacius.tweetIt.events;

import org.bukkit.Bukkit;

import com.rictacius.tweetIt.Main;
import com.rictacius.tweetIt.user.TwitterUser;

/**
 * Declares an event which can be canceled or at least reversed
 * 
 * @author RictAcius
 *
 */
public abstract class Cancelable extends TweetItEvent {
	protected boolean canceled;
	protected boolean accepting;
	protected boolean canChangeState;
	private int timer;
	private long timeout;
	private int count;
	private int max;

	/**
	 * Creates a new Canceleable event
	 * 
	 * @param eventType
	 *            the type of event this class holds
	 * @param primary
	 *            the {@link TwitterUser} that caused this event to be fired
	 */
	public Cancelable(EventType eventType, TwitterUser primary) {
		super(eventType, primary);
		canChangeState = true;
		accepting = true;
		canceled = false;
		timeout = Long.parseLong(Main.pl.getConfig().getString("event.timeout"));
		for (Handler handler : getHandlers()) {
			max += handler.getDeclaredMethods().size();
		}
		runTimeout();
	}

	/**
	 * Cancels/reverses the operation
	 * 
	 * @param toCancel
	 */
	public abstract void setCanceled(boolean toCancel);

	/**
	 * The operation to be run once a cancellation is determined
	 */
	public abstract void finalise();

	/**
	 * 
	 * @return true if current state is canceled, false if not canceled
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * Logs the response from a listening method
	 * 
	 * @param value
	 */
	public void addRespondent(boolean value) {
		if (!canChangeState) {
			return;
		}
		if (accepting) {
			if (count >= max) {
				forceFinalise();
			} else {
				canceled = value;
				count++;
			}
		}
		Main.logger.log("[EVENT RESPONSE] canceled=" + value + " state=" + canceled, 1);
	}

	/**
	 * Sets whether this event can be canceled
	 * 
	 * @param accept
	 */
	public void setAccepting(boolean accept) {
		accepting = accept;
		if (!accept && !canChangeState) {
			if (canceled) {
				finalise();
			}
		}
	}

	/**
	 * Forces the event to finalise regardles of the timeout state.
	 */
	public void forceFinalise() {
		if (!canChangeState) {
			return;
		}
		cancelTimeout();
		canChangeState = false;
		setAccepting(false);
	}

	public void runTimeout() {
		cancelTimeout();
		timer = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.pl, new Runnable() {
			@Override
			public void run() {
				forceFinalise();
			}
		}, timeout);
	}

	public void cancelTimeout() {
		Bukkit.getScheduler().cancelTask(timer);
	}

	/**
	 * 
	 * @return true if event has been finalised, false if active.
	 */
	public boolean finalised() {
		return !canChangeState;
	}

}
