package nikita.external.api.wolfram.query.param;

public interface WolframQueryParam {

	public String getString();

	public default String getString(String paramName, String paramValue) {
		return String.format("&%s=%s", paramName, paramValue);
	}
}
