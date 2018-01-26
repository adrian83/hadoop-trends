package ab.java.twittertrends.common;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class Time {
	
	private Time() {}
	
	public static final long utcNow() {
		return ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
	}

	public static final long utcNowMinus(long amount, TimeUnit unit) {
		long seconds = unit.toSeconds(amount);
		return ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(seconds).toEpochSecond();
	}
	
	
}
