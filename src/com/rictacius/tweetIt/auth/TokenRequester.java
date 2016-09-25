package com.rictacius.tweetIt.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.rictacius.tweetIt.Main;
import com.rictacius.tweetIt.user.TwitterUser;
import com.rictacius.tweetIt.user.UserLoader;
import com.rictacius.tweetIt.utils.TweetItException;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.TwitterException;

/**
 * A class to recieve authentication details from twitter
 * 
 * @author RictAcius
 *
 */
public class TokenRequester implements Listener {
	private TwitterUser user;
	private OAuthSignpostClient client;
	private boolean listening;
	private String[] authCodes;

	/**
	 * Request tokens for a singular user
	 * 
	 * @param user
	 * @param oauthClient
	 */
	public TokenRequester(TwitterUser user, OAuthSignpostClient oauthClient) {
		this.user = user;
		this.client = oauthClient;
		try {
			user.message(ChatColor.LIGHT_PURPLE + "Now Listening for your input, type @cancel to cancel");
		} catch (TweetItException e) {
			Main.logger.log("Could not initialise TokenRequester for (" + user.getId() + ") user is offline!", 3);
		}
		listening = true;
	}

	@EventHandler
	public void listenForPin(AsyncPlayerChatEvent event) throws TweetItException {
		if (!event.isCancelled()) {
			if (!listening)
				return;
			String id = event.getPlayer().getUniqueId().toString();
			if (id.equals(user.getId())) {
				event.setCancelled(true);
				if (event.getMessage().equals("@cancel")) {
					listening = false;
					user.message(ChatColor.GOLD + "No longer listening for your input.");
					return;
				}
				user.message(ChatColor.GOLD + "Validating Verification code...");
				try {
					client.setAuthorizationCode(event.getMessage());
				} catch (TwitterException e) {
					try {
						user.message(
								ChatColor.RED + "Could not verifiy that verification code! See console for details!");
					} catch (TweetItException e1) {
					}
					throw new TweetItException.EAuthentication(user.getUsername(),
							"Could not verifiy that verification code of user (" + user.getId() + ")");
				}
				authCodes = client.getAccessToken();
				try {
					Main.auth.storeAccessToken(user.getId(), authCodes);
				} catch (GeneralSecurityException | IOException e) {
					throw new TweetItException.EAuthentication(user.getUsername(),
							"Could not save authentication info for user (" + user.getId() + ")");
				}
				listening = false;
				user.setAuthenticated(true);
				user.setClient(client);
				user.message("");
				user.message(ChatColor.GREEN + "Your twitter account has now been authenticated hooray! "
						+ "The following message is your pin, you must keep that pin safe so that you will be"
						+ " granted access to your account if a plugin requires it!");
				String pin = Main.auth.randomPin();
				user.message(ChatColor.GOLD + "PIN: " + ChatColor.YELLOW + pin);
				user.message("");
				user.setPin(pin);
				UserLoader.storeUser(user);
			}
		}
	}

	/**
	 * @return the authCodes
	 */
	public String[] getAuthCodes() {
		return authCodes;
	}

	/**
	 * <strong>DO NOT MODIFY THE AUTHCODES UNLESS YOU KNOW WHAT YOU ARE
	 * DOING!</strong>
	 * 
	 * @param authCodes
	 *            the authCodes to set
	 */
	public void setAuthCodes(String[] authCodes) {
		this.authCodes = authCodes;
	}
}
