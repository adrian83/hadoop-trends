package ab.java.trends.domain.twitter.hashtag.domain;

import java.util.Optional;

public class Hashtag {

	private final String name;
	private final Integer count;
	
	public Hashtag(String name, Integer count) {
		super();
		this.name = name;
		this.count = count;
	}
	
	public Optional<String> getName() {
		return Optional.ofNullable(this.name);
	}
	
	public Optional<Integer> getCount() {
		return Optional.ofNullable(this.count);
	}
}
