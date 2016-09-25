package com.rictacius.tweetIt.operations;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

import javax.imageio.ImageIO;

import com.rictacius.tweetIt.events.TweetItEvent.EventType;
import com.rictacius.tweetIt.user.TwitterUser;
import com.rictacius.tweetIt.user.TwitterUserType;

import winterwell.jtwitter.Message;
import winterwell.jtwitter.Status;
import winterwell.jtwitter.TwitterException;
import winterwell.jtwitter.TwitterList;
import winterwell.jtwitter.Twitter_Geo;
import winterwell.jtwitter.Twitter_Users;
import winterwell.jtwitter.User;

/**
 * <p>
 * <strong>WARNING:</strong> This class does not fire any events!
 * </p>
 * 
 * @author RictAcius
 *
 */
public final class TweetItFinal extends Operational {
	/**
	 * <p>
	 * Creates a new set of operations for the TwitterUser
	 * </p>
	 * 
	 * @param user
	 */
	public TweetItFinal(TwitterUser user) {
		super(user);
	}

	/**
	 * Sends a tweet through the player's twitter account;
	 * 
	 * @param message
	 * @return the newly sent tweet
	 * @throws TwitterException
	 */
	public Status sendTweet(String message) throws TwitterException {
		Status status = twitter.updateStatus(message);
		return status;
	}

	/**
	 * 
	 * @return a list of Statuses from the user's timeline
	 * @throws TwitterException
	 */
	public List<Status> getTimeline() throws TwitterException {
		return twitter.getHomeTimeline();
	}

	/**
	 * Send a direct messageto a user
	 * 
	 * @param target
	 *            - the username of the target user
	 * @param message
	 *            - the message to send
	 * @return the direct message sent
	 * @throws TwitterException
	 */
	public Message sendDirectMessage(String target, String message) throws TwitterException {
		Message dmessage = twitter.sendMessage(target, message);
		return dmessage;
	}

	/**
	 * 
	 * @return a list of directmessages of the user
	 * @throws TwitterException
	 */
	public List<Message> getDirectMessages() throws TwitterException {
		return twitter.getDirectMessages();
	}

	/**
	 * Searches through twitter.
	 * 
	 * @param searchQuery
	 *            - what to search for
	 * @return a list of statuses from the search
	 */
	public List<Status> searchTwitter(String searchQuery) {
		List<Status> result = twitter.search(searchQuery);
		return result;
	}

	/**
	 * Makes the TwitterUser follow another user
	 * 
	 * @param username
	 *            - username of the user to follow
	 * @return the User followed (will return null if the TwitterUser already
	 *         follows that user or if a reqest to follow has already been sent)
	 */
	public TwitterUser followUser(String username) {
		Twitter_Users users = twitter.users();
		users.follow(username);
		TwitterUser tuser = new TwitterUser(EventType.USER_FOLLOWED_USER, TwitterUserType.EVENT, username);
		return tuser;
	}

	/**
	 * Makes the TwitterUser follow another user
	 * 
	 * @param userToFollow
	 * @return the User followed (will return null if the TwitterUser already
	 *         follows that user or if a reqest to follow has already been sent)
	 */
	public TwitterUser followUser(TwitterUser userToFollow) {
		Twitter_Users users = twitter.users();
		users.follow(userToFollow.getUsername());
		TwitterUser tuser = new TwitterUser(EventType.USER_FOLLOWED_USER, TwitterUserType.EVENT,
				userToFollow.getUsername());
		return tuser;
	}

	@Deprecated
	public User followUser(User userToFollow) {
		Twitter_Users users = twitter.users();
		return users.follow(userToFollow);
	}

	/**
	 * Checks whether the owner of this operation is following another user
	 * 
	 * @param userB
	 * @return true if following, false if not following
	 * @see #followedBy(String)
	 */
	public boolean followsUser(String userB) {
		Twitter_Users users = twitter.users();
		return users.isFollowing(userB);
	}

	/**
	 * Checks whether the owner of this operation is following another user
	 * 
	 * @param userB
	 * @return true if following, false if not following
	 * @see #followedBy(User)
	 */
	public boolean followsUser(TwitterUser userB) {
		Twitter_Users users = twitter.users();
		return users.isFollowing(userB.generateUser());
	}

	@Deprecated
	public boolean followsUser(User userB) {
		Twitter_Users users = twitter.users();
		return users.isFollowing(userB);
	}

	/**
	 * Checks whether the owner of this operation is followed by another user
	 * 
	 * @param userB
	 * @return true if followed by, false if not followed by
	 * @see #followsUser(String)
	 */
	public boolean followedBy(String userB) {
		Twitter_Users users = twitter.users();
		return users.isFollower(userB);
	}

	/**
	 * Checks whether the owner of this operation is followed by another user
	 * 
	 * @param userB
	 * @return true if followed by, false if not followed by
	 * @see #followsUser(User)
	 */
	public boolean followedBy(TwitterUser userB) {
		Twitter_Users users = twitter.users();
		return users.isFollower(userB.generateUser().getScreenName());
	}

	@Deprecated
	public boolean followedBy(User userB) {
		Twitter_Users users = twitter.users();
		return users.isFollower(userB.getScreenName());
	}

	/**
	 * Makes the TwitterUser unfollow another user
	 * 
	 * @param username
	 *            - username of user to unfollow
	 * @return the User unfollowed (will return null if the TwitterUser does not
	 *         follow that user)
	 */
	public TwitterUser stopFollowingUser(String username) {
		Twitter_Users users = twitter.users();
		users.stopFollowing(username);
		TwitterUser tuser = new TwitterUser(EventType.USER_UNFOLLOWED_USER, TwitterUserType.EVENT, username);
		return tuser;
	}

	/**
	 * Makes the TwitterUser unfollow another user
	 * 
	 * @param userToStopFollowing
	 * @return the User unfollowed (will return null if the TwitterUser does not
	 *         follow that user)
	 */
	public TwitterUser stopFollowingUser(TwitterUser userToStopFollowing) {
		Twitter_Users users = twitter.users();
		users.stopFollowing(userToStopFollowing.generateUser());
		TwitterUser tuser = new TwitterUser(EventType.USER_UNFOLLOWED_USER, TwitterUserType.EVENT,
				userToStopFollowing.getUsername());
		return tuser;
	}

	@Deprecated
	public User stopFollowingUser(User userToStopFollowing) {
		Twitter_Users users = twitter.users();
		return users.stopFollowing(userToStopFollowing);
	}

	/**
	 * Deletes a direct message
	 * 
	 * @param id
	 *            - the id of the direct message to delete
	 */
	public void deleteDirectMessage(Number id) {
		twitter.getDirectMessage(id);
		twitter.destroyMessage(id);
	}

	/**
	 * Deletes a tweet (will only delete if the tweet is from the TwitterUser)
	 * 
	 * @param id
	 *            - the id of the tweet to delete
	 */
	public void deleteTweet(Number id) {
		twitter.getStatus(id);
		twitter.destroyStatus(id);
	}

	/**
	 * 
	 * @param directMessage
	 *            - the message to extract image from
	 * @return an InputStream of the image contained in a direct message
	 * @deprecated in favour of getDirectMessageBufferedImage
	 */
	@Deprecated
	public InputStream getDirectMessageImage(Message directMessage) {
		return twitter.getDMImage(directMessage);
	}

	/**
	 * Updated version of getDirectMessageImage
	 * 
	 * @param directMessage
	 * @return a Buffered image, making it easy to convert using spigot's API
	 * @throws IOException
	 */
	public BufferedImage getDirectMessageBufferedImage(Message directMessage) throws IOException {
		return ImageIO.read(twitter.getDMImage(directMessage));
	}

	/**
	 * 
	 * @return a Class with methods for Geographical modification/collection.
	 */
	public Twitter_Geo getGeographicalMethods() {
		return twitter.geo();
	}

	/**
	 * Converts an direct message's id to a direct message
	 * 
	 * @param id
	 *            - the id of the diret message
	 * @return the DirectMessage to which the id belongs
	 */
	public Message getDirectMessage(Number id) {
		return twitter.getDirectMessage(id);
	}

	/**
	 * 
	 * @return a list of TwitterLists created by the TwitterUser
	 */
	public List<TwitterList> getUsersOwnLists() {
		return twitter.getLists();
	}

	/**
	 * 
	 * @param user
	 *            - the user to locate
	 * @return a list of TwitterLists subscribed to by the specified user,
	 *         including the specified user's own TwitterLists
	 */
	public List<TwitterList> getSubscribedLists(TwitterUser user) {
		return twitter.getListsAll(user.generateUser());
	}

	@Deprecated
	public List<TwitterList> getSubscribedLists(User user) {
		return twitter.getListsAll(user);
	}

	/**
	 * 
	 * @param tweet
	 *            - the tweet to locate
	 * @return a list of Users who retweeted a tweet
	 */
	public List<User> getRetweeters(Status tweet) {
		return twitter.getRetweeters(tweet);
	}

	/**
	 * 
	 * @return a list of latest topics trending on twitter
	 */
	public List<String> getTrends() {
		return twitter.getTrends();
	}

	/**
	 * the User instance of the TwitterUser
	 * 
	 * @return the User
	 * @deprecated infavour of generateUser()
	 * @see TwitterUser#generateUser()
	 */
	@Deprecated
	public User getUserInstance() {
		return twitter.getSelf();
	}

	/**
	 * 
	 * @return a list of tweets which mentioned the TwitterUser
	 */
	public List<Status> getMentions() {
		return twitter.getMentions();
	}

	/**
	 * 
	 * @return a list of tweets which the TwitterUser favorited
	 */
	public List<Status> getFavorites() {
		return twitter.getFavorites();
	}

	/**
	 * 
	 * @param username
	 *            - username of user to locate
	 * @return a list of which the user favorited
	 */
	public List<Status> getFavorites(String username) {
		return twitter.getFavorites(username);
	}

	/**
	 * Reports a user for spam
	 * 
	 * @param username
	 */
	public void reportSpam(String username) {
		twitter.reportSpam(username);
	}

	/**
	 * Retweets a tweet
	 * 
	 * @param tweet
	 *            - the original tweet
	 * @return the retweet
	 */
	public Status retweet(Status tweet) {
		Status retweet = twitter.retweet(tweet);
		return retweet;
	}

	/**
	 * Quotes a tweet
	 * 
	 * @param tweet
	 *            - the original tweet
	 * @param message
	 *            - the message to add
	 * @return the quoting tweet
	 */
	public Status quoteTweet(Status tweet, String message) {
		Status quote = twitter.retweetWithComment(tweet, message);
		return quote;
	}

	/**
	 * Modifies the TwitterUser's favourite setting on a tweet
	 * 
	 * @param tweet
	 * @param favourite
	 *            - the favourite toggle to set
	 * @return the tweet of which's favourite setting was modified
	 */
	public Status setFavoriteTweet(Status tweet, boolean favourite) {
		Status s = twitter.setFavorite(tweet, favourite);
		return s;
	}

	/**
	 * 
	 * @param message
	 * @return the new status
	 */
	public Status updateStatus(String message) {
		Status tweet = twitter.updateStatus(message);
		return tweet;
	}

	/**
	 * Updates the TwitterUser's status with a media file
	 * 
	 * @param message
	 * @param tweetID
	 *            - can be null
	 * @param mediaFile
	 * @return the new status
	 */
	public Status updateStatus(String message, BigInteger tweetID, File mediaFile) {
		Status tweet = twitter.updateStatusWithMedia(message, tweetID, mediaFile);
		return tweet;
	}

	/**
	 * Updates the TwitterUser's status with a list of media files
	 * 
	 * @param message
	 * @param tweetID
	 *            - can be null
	 * @param mediaFiles
	 * @return the new status
	 */
	public Status updateStatus(String message, BigInteger tweetID, List<File> mediaFiles) {
		Status tweet = twitter.updateStatusWithMedia(message, tweetID, mediaFiles);
		return tweet;
	}

	/**
	 * Makes the TwitterUser reply to a tweet
	 * 
	 * @param message
	 * @param tweetID
	 *            - the ID of the tweet to reply to
	 * @return the replying tweet
	 */
	public Status replyToTweet(String message, Number tweetID) {
		Status tweet = twitter.updateStatus(message, tweetID);
		return tweet;
	}

	/**
	 * Gets the latest tweet of a user. <b>Will return null if the user has
	 * never tweeted of if the user's last 6 tweet were 'new-style'
	 * retweets!</b>
	 * 
	 * @return status of user
	 */
	public Status getLatestTweet() {
		return twitter.getStatus();
	}
}
