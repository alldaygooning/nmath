package nikita.external.api.wolfram.query.param;

public enum WolframQueryOutput implements WolframQueryParam {
	JSON("json"),
	XML("xml");

	private final String value;
	private static final String NAME = "output";

	WolframQueryOutput(String value) {
		this.value = value;
	}

	public String getString() {
		return this.getString(NAME, value);
	}
}
