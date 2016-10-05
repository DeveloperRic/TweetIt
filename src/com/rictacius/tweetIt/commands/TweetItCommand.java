package com.rictacius.tweetIt.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rictacius.tweetIt.Main;
import com.rictacius.tweetIt.user.TwitterUser;
import com.rictacius.tweetIt.user.TwitterUserType;
import com.rictacius.tweetIt.utils.ErrorFile;
import com.rictacius.tweetIt.utils.Log;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TweetItCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!sender.hasPermission("tweetit.admin")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}
		if (args.length < 1) {
			sendHelp(sender);
			return true;
		}
		if (args[0].equalsIgnoreCase("reload")) {
			int errors = Main.pl.reloadPlugin();
			sender.sendMessage("");
			if (errors > 0) {
				sender.sendMessage(ChatColor.RED + "API reloaded with " + ChatColor.GOLD + errors + ChatColor.RED
						+ " errors! Please check console!");
			} else {
				sender.sendMessage(ChatColor.GREEN + "APi reloaded successfully!");
				sender.sendMessage(ChatColor.GRAY + "API created by RictAcius");
			}
		} else if (args[0].equalsIgnoreCase("dump")) {
			List<String> data = ErrorFile.writeError("Requested TweetIt Dump File");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.AQUA + "Error dump file written.");
			if (data != null) {
				sender.sendMessage(ChatColor.YELLOW + "A pastebin was created for the error dump file " + data.get(1));
				if (sender instanceof Player) {
					Player player = (Player) sender;
					TextComponent text = new TextComponent("Click here to open the file");
					ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_FILE, data.get(0));
					text.setClickEvent(event);
					text.setColor(ChatColor.AQUA);
					player.spigot().sendMessage(text);
				}
			}
			sender.sendMessage("");
		} else if (args[0].equalsIgnoreCase("testauth")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You cannot test authentication from console!");
				return true;
			}
			Player player = (Player) sender;
			TwitterUser user = new TwitterUser(TwitterUserType.PLAYER, player.getUniqueId().toString(), null);
			try {
				Main.auth.requestTokens(user);
			} catch (Exception e) {
				Main.logger.log("Could not test TweetIt Authentication while requesting tokens!", Log.Level.WARNING);
				e.printStackTrace();
			}
		} else if (args[0].equalsIgnoreCase("testauthchild")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You cannot test authentication from console!");
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "/tweetit testauthchild <childID>");
				return true;
			}
			if (Main.auth.isRegistered(args[1])) {
				sender.sendMessage(ChatColor.RED + "That ID is already in use by another TwitterUser!");
				return true;
			}
			Player player = (Player) sender;
			TwitterUser parent = new TwitterUser(TwitterUserType.PLAYER, player.getUniqueId().toString(), null);
			TwitterUser child = new TwitterUser(TwitterUserType.OTHER, args[1], null);
			try {
				Main.auth.requestTokens(parent, child);
			} catch (Exception e) {
				Main.logger.log("Could not test TweetIt Authentication while requesting tokens!", Log.Level.WARNING);
				e.printStackTrace();
			}
		} else {
			sendHelp(sender);
		}
		return true;
	}

	void sendHelp(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage(
				ChatColor.AQUA + "TweetIt API " + ChatColor.GOLD + "v" + Main.pl.getDescription().getVersion());
		sender.sendMessage(ChatColor.GREEN + "/tweetit reload" + ChatColor.YELLOW + " - Reloads the config");
		sender.sendMessage(ChatColor.GREEN + "/tweetit dump" + ChatColor.YELLOW + " - Prints debug info");
		sender.sendMessage(
				ChatColor.GREEN + "/tweetit testauth" + ChatColor.YELLOW + " - Tests Authentication (Players only!)");
		sender.sendMessage(ChatColor.GREEN + "/tweetit testauthchild" + ChatColor.YELLOW
				+ " - Tests Authentication for attachment users (Players only!)");
		sender.sendMessage("");
	}
}
