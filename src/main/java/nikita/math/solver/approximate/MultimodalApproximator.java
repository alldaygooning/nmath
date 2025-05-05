package nikita.math.solver.approximate;

public interface MultimodalApproximator {
	static final String LOGGER_PREFIX_TEMPLATE = "Power%s";
	static final String FULL_NAME_PREFIX_TEMPLATE = "Power-%s";

	public void setMode(int mode);

	public int getMode();

	public default String getLoggerPrefix(int mode) {
		return String.format(LOGGER_PREFIX_TEMPLATE, mode);
	}

	public default String getFullNamePrefix(int mode) {
		return String.format(FULL_NAME_PREFIX_TEMPLATE, mode);
	}
}
