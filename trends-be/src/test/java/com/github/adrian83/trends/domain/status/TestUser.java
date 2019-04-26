package com.github.adrian83.trends.domain.status;

import java.util.Date;

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;


public class TestUser implements User {

	private static final long serialVersionUID = -1369501263082707953L;
	
	private String screenName;

	@Override
	public int compareTo(User arg0) {
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
	public long getId() {
		return 0;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getEmail() {
		return null;
	}

	@Override
	public String getLocation() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public boolean isContributorsEnabled() {
		return false;
	}

	@Override
	public String getProfileImageURL() {
		return null;
	}

	@Override
	public String getBiggerProfileImageURL() {
		return null;
	}

	@Override
	public String getMiniProfileImageURL() {
		return null;
	}

	@Override
	public String getOriginalProfileImageURL() {
		return null;
	}

	@Override
	public String getProfileImageURLHttps() {
		return null;
	}

	@Override
	public String getBiggerProfileImageURLHttps() {
		return null;
	}

	@Override
	public String getMiniProfileImageURLHttps() {
		return null;
	}

	@Override
	public String getOriginalProfileImageURLHttps() {
		return null;
	}

	@Override
	public boolean isDefaultProfileImage() {
		return false;
	}

	@Override
	public String getURL() {
		return null;
	}

	@Override
	public boolean isProtected() {
		return false;
	}

	@Override
	public int getFollowersCount() {
		return 0;
	}

	@Override
	public Status getStatus() {
		return null;
	}

	@Override
	public String getProfileBackgroundColor() {
		return null;
	}

	@Override
	public String getProfileTextColor() {
		return null;
	}

	@Override
	public String getProfileLinkColor() {
		return null;
	}

	@Override
	public String getProfileSidebarFillColor() {
		return null;
	}

	@Override
	public String getProfileSidebarBorderColor() {
		return null;
	}

	@Override
	public boolean isProfileUseBackgroundImage() {
		return false;
	}

	@Override
	public boolean isDefaultProfile() {
		return false;
	}

	@Override
	public boolean isShowAllInlineMedia() {
		return false;
	}

	@Override
	public int getFriendsCount() {
		return 0;
	}

	@Override
	public Date getCreatedAt() {
		return null;
	}

	@Override
	public int getFavouritesCount() {
		return 0;
	}

	@Override
	public int getUtcOffset() {
		return 0;
	}

	@Override
	public String getTimeZone() {
		return null;
	}

	@Override
	public String getProfileBackgroundImageURL() {
		return null;
	}

	@Override
	public String getProfileBackgroundImageUrlHttps() {
		return null;
	}

	@Override
	public String getProfileBannerURL() {
		return null;
	}

	@Override
	public String getProfileBannerRetinaURL() {
		return null;
	}

	@Override
	public String getProfileBannerIPadURL() {
		return null;
	}

	@Override
	public String getProfileBannerIPadRetinaURL() {
		return null;
	}

	@Override
	public String getProfileBannerMobileURL() {
		return null;
	}

	@Override
	public String getProfileBannerMobileRetinaURL() {
		return null;
	}

	@Override
	public boolean isProfileBackgroundTiled() {
		return false;
	}

	@Override
	public String getLang() {
		return null;
	}

	@Override
	public int getStatusesCount() {
		return 0;
	}

	@Override
	public boolean isGeoEnabled() {
		return false;
	}

	@Override
	public boolean isVerified() {
		return false;
	}

	@Override
	public boolean isTranslator() {
		return false;
	}

	@Override
	public int getListedCount() {
		return 0;
	}

	@Override
	public boolean isFollowRequestSent() {
		return false;
	}

	@Override
	public URLEntity[] getDescriptionURLEntities() {
		return null;
	}

	@Override
	public URLEntity getURLEntity() {
		return null;
	}

	@Override
	public String[] getWithheldInCountries() {
		return null;
	}

	@Override
	public String getScreenName() {
		return screenName;
	}

}
