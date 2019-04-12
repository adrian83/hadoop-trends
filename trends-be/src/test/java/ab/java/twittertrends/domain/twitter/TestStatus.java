package ab.java.twittertrends.domain.twitter;

import java.util.Date;

import twitter4j.ExtendedMediaEntity;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;



public class TestStatus implements Status {

	private static final long serialVersionUID = 8460125005753152565L;
	
	private long id;
	private String text;
	private int favoriteCount;
	private TestUser user;
	private TestStatus retweetedStatus;

	@Override
	public int compareTo(Status o) {
		return 0;
	}

	@Override
	public RateLimitStatus getRateLimitStatus() {
		return null;
	}

	@Override
	public int getAccessLevel() {
		return 0;
	}

	@Override
	public UserMentionEntity[] getUserMentionEntities() {
		return null;
	}

	@Override
	public URLEntity[] getURLEntities() {
		return null;
	}

	@Override
	public HashtagEntity[] getHashtagEntities() {
		return null;
	}

	@Override
	public MediaEntity[] getMediaEntities() {
		return null;
	}

	@Override
	public ExtendedMediaEntity[] getExtendedMediaEntities() {
		return null;
	}

	@Override
	public SymbolEntity[] getSymbolEntities() {
		return null;
	}

	@Override
	public Date getCreatedAt() {
		return null;
	}

	@Override
	public String getSource() {
		return null;
	}

	@Override
	public boolean isTruncated() {
		return false;
	}

	@Override
	public long getInReplyToStatusId() {
		return 0;
	}

	@Override
	public long getInReplyToUserId() {
		return 0;
	}

	@Override
	public String getInReplyToScreenName() {
		return null;
	}

	@Override
	public GeoLocation getGeoLocation() {
		return null;
	}

	@Override
	public Place getPlace() {
		return null;
	}

	@Override
	public boolean isFavorited() {
		return false;
	}

	@Override
	public boolean isRetweeted() {
		return false;
	}

	@Override
	public boolean isRetweet() {
		return false;
	}

	@Override
	public long[] getContributors() {
		return null;
	}

	@Override
	public int getRetweetCount() {
		return 0;
	}

	@Override
	public boolean isRetweetedByMe() {
		return false;
	}

	@Override
	public long getCurrentUserRetweetId() {
		return 0;
	}

	@Override
	public boolean isPossiblySensitive() {
		return false;
	}

	@Override
	public String getLang() {
		return null;
	}

	@Override
	public Scopes getScopes() {
		return null;
	}

	@Override
	public String[] getWithheldInCountries() {
		return null;
	}

	@Override
	public long getQuotedStatusId() {
		return 0;
	}

	@Override
	public Status getQuotedStatus() {
		return null;
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFavoriteCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public User getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status getRetweetedStatus() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
