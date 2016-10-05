package com.rictacius.tweetIt.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import com.rictacius.tweetIt.Main;

import net.md_5.bungee.api.ChatColor;

public class Log {
	private boolean enabled;
	private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	private ArrayList<String> timeline = new ArrayList<String>();;

	public Log() {
		enabled = true;
	}

	String prefix() {
		return ChatColor.translateAlternateColorCodes('&', "&7[&aTweetIt&7] &r");
	}

	/**
	 * Defines the level of a Log entry.
	 * @author RictAcius
	 *
	 */
	public enum Level {
		FATAL(3), WARNING(2), INFO(1);

		private Level(int lvl) {
			this.lvl = lvl;
		}

		private int lvl;

		public int getLevel() {
			return lvl;
		}
	}

	/**
	 * Logs an event or process
	 * 
	 * @param message
	 *            the event/process to log
	 * @param lvl
	 *            the level of the message
	 * @see Level Log Levels
	 */
	public void log(String message, Level lvl) {
		log(message, lvl.getLevel());
	}

	/**
	 * Logs an error arising from an event/process
	 * 
	 * @param message
	 *            the event/process to log
	 * @param lvl
	 *            the level of the message
	 * @param e
	 *            the error that occured
	 * @see Level Log Levels
	 */
	public void log(String message, Level lvl, Exception e) {
		log(message, lvl.getLevel(), e);
	}

	/**
	 * Logs an event or process
	 * 
	 * @param message
	 *            the event/process to log
	 * @param lvl
	 *            the level of the message
	 * @see #log(String, Level)
	 */
	@Deprecated
	public void log(String message, int lvl) {
		if (enabled) {
			if (timeline.size() >= Integer.parseInt(Main.pl.getConfig().getString("logger.size")))
				timeline.clear();
			String send = "";
			String raw = "| > |" + message;
			switch (lvl) {
			case 1:
				send = (prefix() + ChatColor.WHITE + message);
				break;
			case 2:
				send = (prefix() + ChatColor.YELLOW + message);
				break;
			case 3:
				send = (prefix() + ChatColor.RED + message);
				break;
			default:
				send = (prefix() + ChatColor.AQUA + message);
				break;
			}
			if (Boolean.parseBoolean(Main.pl.getConfig().getString("debug"))) {
				console.sendMessage(send);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("d/m/yyyy HH:mm");
			String dateString = formatter.format(new Date());
			timeline.add(dateString + ChatColor.stripColor(raw));
		}
	}

	/**
	 * Logs an error arising from an event/process
	 * 
	 * @param message
	 *            the event/process to log
	 * @param lvl
	 *            the level of the message
	 * @param e
	 *            the error that occured
	 * @see #log(String, Level, Exception)
	 */
	@Deprecated
	public void log(String message, int lvl, Exception e) {
		log("--------------------------------------------------------", lvl);
		log(message, lvl);
		log(" E: " + e.getClass().getSimpleName() + ", Trace: ", lvl);
		for (StackTraceElement el : e.getStackTrace()) {
			log("  Class=" + el.getClassName() + " , Method=" + el.getMethodName() + " , Loc=" + el.getLineNumber(),
					lvl);
		}
		log("--------------------------------------------------------", lvl);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the timeline
	 */
	public ArrayList<String> getTimeline() {
		return timeline;
	}
}
