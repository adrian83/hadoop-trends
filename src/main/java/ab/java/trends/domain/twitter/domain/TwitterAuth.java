package ab.java.trends.domain.twitter.domain;

public class TwitterAuth {

	private final String token;
	private final String secret;
	private final String customerKey;
	private final String customerSecret;

	public TwitterAuth(String token, String secret, String customerKey, String customerSecret) {
		super();
		this.token = token;
		this.secret = secret;
		this.customerKey = customerKey;
		this.customerSecret = customerSecret;
	}

	public String getToken() {
		return token;
	}

	public String getSecret() {
		return secret;
	}

	public String getCustomerKey() {
		return customerKey;
	}

	public String getCustomerSecret() {
		return customerSecret;
	}

}
