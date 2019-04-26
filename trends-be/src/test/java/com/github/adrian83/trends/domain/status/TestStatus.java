package com.github.adrian83.trends.domain.status;

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

  public long getId() {
    return id;
  }

  public String getText() {
    return text;
  }

  public int getFavoriteCount() {
    return favoriteCount;
  }

  public User getUser() {
    return user;
  }

  public TestStatus getRetweetedStatus() {
    return retweetedStatus;
  }

@Override public int compareTo(Status arg0){// TODO Auto-generated method stub
return 0;}

@Override public RateLimitStatus getRateLimitStatus(){// TODO Auto-generated method stub
return null;}

@Override public int getAccessLevel(){// TODO Auto-generated method stub
return 0;}

@Override public UserMentionEntity[] getUserMentionEntities(){// TODO Auto-generated method stub
return null;}

@Override public URLEntity[] getURLEntities(){// TODO Auto-generated method stub
return null;}

@Override public HashtagEntity[] getHashtagEntities(){// TODO Auto-generated method stub
return null;}

@Override public MediaEntity[] getMediaEntities(){// TODO Auto-generated method stub
return null;}

@Override public ExtendedMediaEntity[] getExtendedMediaEntities(){// TODO Auto-generated method stub
return null;}

@Override public SymbolEntity[] getSymbolEntities(){// TODO Auto-generated method stub
return null;}

@Override public Date getCreatedAt(){// TODO Auto-generated method stub
return null;}

@Override public String getSource(){// TODO Auto-generated method stub
return null;}

@Override public boolean isTruncated(){// TODO Auto-generated method stub
return false;}

@Override public long getInReplyToStatusId(){// TODO Auto-generated method stub
return 0;}

@Override public long getInReplyToUserId(){// TODO Auto-generated method stub
return 0;}

@Override public String getInReplyToScreenName(){// TODO Auto-generated method stub
return null;}

@Override public GeoLocation getGeoLocation(){// TODO Auto-generated method stub
return null;}

@Override public Place getPlace(){// TODO Auto-generated method stub
return null;}

@Override public boolean isFavorited(){// TODO Auto-generated method stub
return false;}

@Override public boolean isRetweeted(){// TODO Auto-generated method stub
return false;}

@Override public boolean isRetweet(){// TODO Auto-generated method stub
return false;}

@Override public long[] getContributors(){// TODO Auto-generated method stub
return null;}

@Override public int getRetweetCount(){// TODO Auto-generated method stub
return 0;}

@Override public boolean isRetweetedByMe(){// TODO Auto-generated method stub
return false;}

@Override public long getCurrentUserRetweetId(){// TODO Auto-generated method stub
return 0;}

@Override public boolean isPossiblySensitive(){// TODO Auto-generated method stub
return false;}

@Override public String getLang(){// TODO Auto-generated method stub
return null;}

@Override public Scopes getScopes(){// TODO Auto-generated method stub
return null;}

@Override public String[] getWithheldInCountries(){// TODO Auto-generated method stub
return null;}

@Override public long getQuotedStatusId(){// TODO Auto-generated method stub
return 0;}

@Override public Status getQuotedStatus(){// TODO Auto-generated method stub
return null;}
  
  
  
}
