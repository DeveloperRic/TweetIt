package com.rictacius.tweetIt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public final class Methods {

	public static void sendColoredMessage(Main plugin, ChatColor ecolor, String message, ChatColor color) {
		ecolor = ChatColor.GREEN;
		ConsoleCommandSender c = plugin.getServer().getConsoleSender();
		c.sendMessage(ecolor + "" + ChatColor.BOLD + "[" + plugin.getDescription().getName() + "] " + color + message);

	}

	public static void sendColoredConsoleMessage(ChatColor ecolor, String message) {
		ConsoleCommandSender c = Bukkit.getServer().getConsoleSender();
		c.sendMessage(ecolor + message);

	}

	public static void errorMessage(String message) {
		ConsoleCommandSender c = Bukkit.getServer().getConsoleSender();
		c.sendMessage(ChatColor.RED + "[" + Main.pl.getDescription().getName() + "] " + ChatColor.WHITE + message);
	}
}
