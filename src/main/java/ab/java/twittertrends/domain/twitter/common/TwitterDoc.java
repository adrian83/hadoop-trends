package ab.java.twittertrends.domain.twitter.common;

import java.time.LocalDateTime;

public class TwitterDoc {

	public static final String LAST_UPDATE_LABEL = "lastUpdate";
	
	private LocalDateTime lastUpdate;

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
	
}
