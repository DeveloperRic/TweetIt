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

public class TokenRequesterChild implements Listener {
	private TwitterUser parent;
	private TwitterUser child;
	private OAuthSignpostClient client;
	private boolean listening;
	private String[] authCodes;

	/**
	 * Request tokens for an attachment user.
	 * 
	 * @param parent
	 * @param child
	 * @param oauthClient
	 */
	public TokenRequesterChild(TwitterUser parent, TwitterUser child, OAuthSignpostClient oauthClient) {
		this.child = child;
		this.parent = parent;
		this.client = oauthClient;
		try {
			parent.message(ChatColor.LIGHT_PURPLE + "Now Listening for your input, type @cancel to cancel");
		} catch (TweetItException e) {
			Main.logger.log("Could not initialise TokenRequester for (" + child.getId() + ") parent user is offline!",
					3);
		}
		listening = true;
	}

	@EventHandler
	public void listenForPin(AsyncPlayerChatEvent event) throws TweetItException {
		if (!event.isCancelled()) {
			if (!listening)
				return;
			String id = event.getPlayer().getUniqueId().toString();
			if (id.equals(parent.getId())) {
				event.setCancelled(true);
				if (event.getMessage().equals("@cancel")) {
					listening = false;
					parent.message(ChatColor.GOLD + "No longer listening for your input.");
					return;
				}
				parent.message(ChatColor.GOLD + "Validating Verification code...");
				try {
					client.setAuthorizationCode(event.getMessage());
				} catch (TwitterException e) {
					try {
						parent.message(
								ChatColor.RED + "Could not verifiy that verification code! See console for details!");
					} catch (TweetItException e1) {
					}
					throw new TweetItException.EAuthentication(child.getUsername(),
							"Could not verifiy that verification code of user (" + child.getId() + ")");
				}
				authCodes = client.getAccessToken();
				try {
					Main.auth.storeAccessToken(child.getId(), authCodes);
				} catch (GeneralSecurityException | IOException e) {
					throw new TweetItException.EAuthentication(child.getUsername(),
							"Could not save authentication info for user (" + child.getId() + ")");
				}
				listening = false;
				child.setAuthenticated(true);
				child.setClient(client);
				parent.message("");
				parent.message(ChatColor.GREEN + "Your twitter account has now been authenticated hooray! "
						+ "The following message is your pin, you must keep that pin safe so that you will be"
						+ " granted access to your account if a plugin requires it!");
				String pin = Main.auth.randomPin();
				parent.message(ChatColor.GOLD + "PIN: " + ChatColor.YELLOW + pin);
				parent.message("");
				child.setPin(pin);
				UserLoader.storeUser(child);
			}
		}
	}

	/**
	 * @return the authCodes <b>will return null if the user has not been
	 *         authorized yet.</b>
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
