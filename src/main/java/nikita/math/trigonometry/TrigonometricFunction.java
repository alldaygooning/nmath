package nikita.math.trigonometry;

enum TrigonometricFunction {
	SIN("sin", "2*pi"), COS("cos", "2*pi"), TAN("tan", "pi"), CSC("csc", "2*pi"), SEC("sec", "2*pi"), COT("cot", "pi");

	private final String functionName;
	private final String periodString;

	TrigonometricFunction(String functionName, String periodString) {
		this.functionName = functionName;
		this.periodString = periodString;
	}

	public String getFunctionName() {
		return functionName;
	}

	public String getPeriodString() {
		return periodString;
	}

	public static String getPeriodByName(String name) {
		for (TrigonometricFunction function : values()) {
			if (function.functionName.equalsIgnoreCase(name)) {
				return function.periodString;
			}
		}
		return null;
	}
}