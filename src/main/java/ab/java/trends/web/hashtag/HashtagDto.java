package ab.java.trends.web.hashtag;

public class HashtagDto {

	private final String name;
	private final Integer count;
	
	public HashtagDto(String name, Integer count) {
		super();
		this.name = name;
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public Integer getCount() {
		return count;
	}
	
	
	
}
