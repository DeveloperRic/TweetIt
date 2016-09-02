package com.rictacius.tweetIt.commands;

import java.net.URL;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rictacius.tweetIt.Main;
import com.rictacius.tweetIt.user.TwitterUser;
import com.rictacius.tweetIt.user.TwitterUserType;
import com.rictacius.tweetIt.utils.ErrorFile;

import net.md_5.bungee.api.ChatColor;

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
			URL link = ErrorFile.writeError("Requested TweetIt Dump File");
			sender.sendMessage(ChatColor.AQUA + "Error dump file written.");
			if (link != null)
				sender.sendMessage(ChatColor.YELLOW + "A pastebin was created for the error dump file " + link);
		} else if (args[0].equalsIgnoreCase("testauth")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You cannot test authentication from console!");
				return true;
			}
			Player player = (Player) sender;
			TwitterUser user = new TwitterUser(TwitterUserType.PLAYER, player.getUniqueId().toString());
			try {
				Main.auth.requestTokens(user);
			} catch (Exception e) {
				Main.logger.log("Could not test TweetIt Authentication while requesting tokens!", 2);
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
		sender.sendMessage("");
	}
}
