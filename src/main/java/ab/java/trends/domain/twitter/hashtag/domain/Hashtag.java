package ab.java.trends.domain.twitter.hashtag.domain;

public class Hashtag {

	private final String name;
	private final Integer count;
	
	public Hashtag(String name, Integer count) {
		super();
		this.name = name;
		this.count = count;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Integer getCount() {
		return this.count;
	}
}
