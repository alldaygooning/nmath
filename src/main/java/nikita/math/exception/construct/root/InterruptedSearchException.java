package nikita.math.exception.construct.root;

import java.math.BigDecimal;
import java.util.List;

import nikita.math.construct.Variable;

public class InterruptedSearchException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InterruptedSearchException(BigDecimal x, String message) {
		super(String.format("Search was interrupted due to: %s Closest available approximation: %s.", message, x.toPlainString()));
	}

	public InterruptedSearchException(List<Variable> variables, String method, String message) {
		super(String.format("Search for root(s) by '%s' was interrupted at %s due to: %s", method, variablesToString(variables), message));
	}

	public InterruptedSearchException(String string) {
		super(string);
	}

	private static String variablesToString(List<Variable> variables) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < variables.size(); i++) {
			builder.append(variables.get(i).toString());
			if (i != variables.size() - 1) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}
}
