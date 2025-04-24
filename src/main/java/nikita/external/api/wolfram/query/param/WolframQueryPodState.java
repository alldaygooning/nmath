package nikita.external.api.wolfram.query.param;

public enum WolframQueryPodState implements WolframQueryParam {
	MORE_SOLUTIONS("Result__More+solutions");

	private static final String NAME = "podstate";
	private final String value;

	WolframQueryPodState(String value) {
		this.value = value;
	}

	@Override
	public String getString() {
		return getString(NAME, value);
	}
}
