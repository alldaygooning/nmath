package nikita.external.api.wolfram.query.param;

public enum WolframQueryPod implements WolframQueryParam {
	RESULT("Result");

	private final String value;
	private static final String NAME = "includepodid";

	private WolframQueryPod(String value) {
		this.value = value;
	}

	@Override
	public String getString() {
		return this.getString(NAME, value);
	}
}
