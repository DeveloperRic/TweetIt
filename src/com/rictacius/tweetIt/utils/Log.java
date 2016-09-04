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

	public void log(String message, int lvl) {
		if (enabled) {
			if (timeline.size() >= Integer.parseInt(Main.pl.getConfig().getString("logger.size")))
				timeline.clear();
			String send = "";
			switch (lvl) {
			case 1:
				send = (prefix() + ChatColor.WHITE + message);
			case 2:
				send = (prefix() + ChatColor.YELLOW + message);
			case 3:
				send = (prefix() + ChatColor.RED + message);
			default:
				send = (prefix() + ChatColor.AQUA + message);
			}
			console.sendMessage(send);
			SimpleDateFormat formatter = new SimpleDateFormat("d, m, yyyy HH:mm");
			String dateString = formatter.format(new Date());
			timeline.add(dateString + send);
		}
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
