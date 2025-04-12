package nikita.math.exception.construct.integral;

import nikita.math.construct.calculus.Integral;
import nikita.math.exception.construct.NMathException;

public class IntegrationException extends NMathException {
	private static final long serialVersionUID = 1L;

	public IntegrationException(Integral integral, String method, String reason) {
		super(String.format("%s could not be evaluated using '%s' due to: %s.", integral.toBeautifulString(), method, reason));
	}
}
