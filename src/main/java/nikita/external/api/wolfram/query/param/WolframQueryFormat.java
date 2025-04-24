package nikita.external.api.wolfram.query.param;

public enum WolframQueryFormat implements WolframQueryParam {
	PLAINTEXT("plaintext"),
	IMAGE("image");
	
	private final String value;
	private static final String NAME = "format";

	private WolframQueryFormat(String value) {
		this.value = value;
	}

	public String getString() {
		return this.getString(NAME, value);
	}
}
