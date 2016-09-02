package com.rictacius.tweetIt.auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.rictacius.tweetIt.Main;
import com.rictacius.tweetIt.Methods;
import com.rictacius.tweetIt.user.TwitterUser;
import com.rictacius.tweetIt.user.UserLoader;
import com.rictacius.tweetIt.utils.PinParser;
import com.rictacius.tweetIt.utils.TweetItException;

import winterwell.jtwitter.OAuthSignpostClient;

/**
 * <p>
 * Class that handles user oauth info
 * </p>
 * <p>
 * <strong><u>Make sure you have setup your twitter app properly and added the
 * consumer key and consumer secret to the config</u></strong>
 * </p>
 * 
 * @author RictAcius
 *
 */
public class UserAuthorizer {
	private File accessKeysFile;
	private FileConfiguration accessKeysConfig;
	private List<String> pins = new ArrayList<String>();
	private int maxpins;
	private int pinTimeout;
	private int pinLength;

	public UserAuthorizer(int pinLength, int pinTimeout) {
		accessKeysFile = new File(Main.pl.getDataFolder(), "accesskeys.yml");
		if (!accessKeysFile.exists()) {
			accessKeysFile.getParentFile().mkdirs();
			Main.pl.saveResource("accesskeys.yml", false);
		}
		accessKeysConfig = new YamlConfiguration();
		try {
			accessKeysConfig.load(accessKeysFile);
		} catch (FileNotFoundException e) {
			Methods.errorMessage("AccessKeysFile was not found, please report this!");
			e.printStackTrace();
		} catch (IOException e) {
			Methods.errorMessage("Could not load AccessKeysFile, please report this!");
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			Methods.errorMessage("Could not load AccessKeysFile, make sure you do not tamper with this file!");
			e.printStackTrace();
		}
		@SuppressWarnings("unused")
		String function = "((" + PinParser.getKeys().size() + "+" + pinLength + "-1)!)/(" + pinLength + "!("
				+ (PinParser.getKeys().size() - 1) + "))";
		this.pinTimeout = pinTimeout;
		this.pinLength = pinLength;
	}

	/**
	 * Generates a random pin based on the UserAuthorizer pin settings
	 * 
	 * @return
	 */
	public String randomPin() {
		if (pins.size() > maxpins) {
			pins.clear();
		}
		String pin = "";
		Random random = new Random();
		int tries = 0;
		while (tries <= pinTimeout) {
			tries++;
			pin = "";
			for (int i = 1; i <= pinLength; i++) {
				int reference = random.nextInt(PinParser.getKeys().size());
				pin = pin + PinParser.getKeys().get(reference);
			}
			boolean unique = false;
			for (TwitterUser user : UserLoader.getUsers().values()) {
				if (user.getPin() == pin) {
					unique = false;
				} else {
					unique = true;
				}
			}
			if (unique) {
				break;
			}
		}
		pins.add(pin);
		return pin;
	}

	/**
	 * Request auth data for a specific player's twitter account.
	 * 
	 * @param user does not need to be a verified user.
	 * @throws Exception
	 */
	public void requestTokens(TwitterUser user) throws Exception {
		OAuthSignpostClient oauthClient = new OAuthSignpostClient(Main.consumerKey, Main.consumerSecret, "oob");
		user.message("");
		user.message(ChatColor.GREEN + "Contacting Twitter...");
		try {
			String link = oauthClient.authorizeUrl().toString();
			user.message("");
			user.message(ChatColor.GREEN + "Open the following URL and grant TweetIt access to your account:");
			user.message(ChatColor.YELLOW + link);
			user.message("");
		} catch (Exception e) {
			e.printStackTrace();
			user.message(ChatColor.RED
					+ "Failed to get oath URL, please try again later and if the problem persits report to the server admin.");
			if (Main.consumerKey.equals("") || Main.consumerSecret.equals("")) {
				user.message(ChatColor.RED + "Detected defective ConsumerKey and/or ConsumerSecret. "
						+ "If you are an admin, please configure TweetIt properly to use its API!");
			}
		}
		user.message(ChatColor.GREEN + "When done, type in the verification PIN from Twitter in chat.");
		Main.pl.registerTokenEvents(new TokenRequester(user, oauthClient));
	}

	/**
	 * <p>
	 * Store access tokens of a player's twitter account
	 * </p>
	 * <p>
	 * <strong>The access tokens are encrypted with the API's own encryption
	 * system</strong>
	 * </p>
	 * 
	 * @param playerID
	 * @param accessToken
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public void storeAccessToken(String userID, String[] codes) throws GeneralSecurityException, IOException {
		// store accessToken.getToken()
		// store accessToken.getTokenSecret()
		String token;
		token = TweetItEncrypter.encrypt(codes[0]);
		String secret = TweetItEncrypter.encrypt(codes[1]);
		accessKeysConfig.set("keys." + userID + ".token", token);
		accessKeysConfig.set("keys." + userID + ".tokenSecret", secret);
		accessKeysConfig.save(accessKeysFile);
	}

	/**
	 * Checks if the player's twitter account is registered
	 * 
	 * @param playerID
	 * @return
	 */
	public boolean isRegistered(String userID) {
		return (accessKeysConfig.getString("keys." + userID + ".token") != null);
	}

	/**
	 * <p>
	 * Gets the token of the player's twitter account
	 * </p>
	 * <p>
	 * <strong>Will return null if the player's twitter account is not
	 * registered</strong>
	 * </p>
	 * 
	 * @param playerID
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public String getAccessToken(String userID) throws GeneralSecurityException, IOException, TweetItException {
		if (!isRegistered(userID)) {
			throw new TweetItException("The user's credentials have not yet been registered! ID:" + userID);
		}
		String token = accessKeysConfig.getString("keys." + userID + ".token");
		token = TweetItEncrypter.decrypt(token);
		return token;
	}

	public String getAccessTokenSecret(String userID) throws GeneralSecurityException, IOException, TweetItException {
		if (!isRegistered(userID)) {
			throw new TweetItException("The user's credentials have not yet been registered! ID:" + userID);
		}
		String tokenSecret = accessKeysConfig.getString("keys." + userID + ".tokenSecret");
		tokenSecret = TweetItEncrypter.decrypt(tokenSecret);
		return tokenSecret;
	}
}
