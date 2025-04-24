package nikita.math.construct.expression.util;

import java.util.List;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;

public class ExpressionUtils {

	public static void searchDenominators(IExpr expr, List<Expression> denominators) {
		if (expr == null)
			return;

		// Деление реализовано через отрицательные степени
		if (expr.head().equals(F.Power) && expr.getAt(2).isNegative()) {
			IExpr base = expr.getAt(1);
			IExpr power = expr.getAt(2).abs();
			Expression denominator = new Expression(String.format("%s%s", base, power.isOne() ? "" : String.format("^%s", power)));
			denominators.add(denominator);
		}

		for (int i = 1; i <= expr.argSize(); i++) {
			searchDenominators(expr.getAt(i), denominators);
		}
	}

	public static boolean containsSymbol(IExpr expr, Variable variable) {
		if (expr.isSymbol()) {
			ISymbol symbol = (ISymbol) expr;
			return variable.getName().equals(symbol.getSymbolName());
		}

		for (int i = 1; i <= expr.argSize(); i++) {
			if (containsSymbol(expr.getAt(i), variable)) {
				return true;
			}
		}

		return false;
	}

	public static void searchTan(IExpr expr, List<Expression> tanParams) {
		if (expr == null)
			return;

		if (expr.head().equals(F.Tan)) {
			IExpr param = expr.getAt(1);
			Expression tanParam = new Expression(param.toString());
			tanParams.add(tanParam);
		}

		for (int i = 1; i <= expr.argSize(); i++) {
			searchTan(expr.getAt(i), tanParams);
		}
	}

	public static void searchCot(IExpr expr, List<Expression> cotParams) {
		if (expr == null)
			return;

		if (expr.head().equals(F.Cot)) {
			IExpr param = expr.getAt(1);
			Expression cotParam = new Expression(param.toString());
			cotParams.add(cotParam);
		}

		for (int i = 1; i <= expr.argSize(); i++) {
			searchCot(expr.getAt(i), cotParams);
		}
	}

	public static void searchLogBases(IExpr expr, List<Expression> logBases) {
		if (expr == null) {
			return;
		}

		if (expr.head().equals(F.Log)) {
			if (expr.argSize() == 2) {
				IExpr base = expr.getAt(1);
//				IExpr value = expr.getAt(2);
				Expression logBase = new Expression(base.toString());
				logBases.add(logBase);
			}
		}

		for (int i = 1; i <= expr.argSize(); i++) {
			searchLogBases(expr.getAt(i), logBases);
		}
	}

	public static void searchLogValues(IExpr expr, List<Expression> logValues) {
		if (expr == null) {
			return;
		}

		if (expr.head().equals(F.Log)) {
			IExpr value = null;
			if (expr.argSize() == 1) {
				value = expr.getAt(1);
			}
			if (expr.argSize() == 2) {
				value = expr.getAt(2);
			}
			Expression logValue = new Expression(value.toString());
			logValues.add(logValue);
		}

		for (int i = 1; i <= expr.argSize(); i++) {
			searchLogValues(expr.getAt(i), logValues);
		}
	}

	public static void searchEvenRootParams(IExpr expr, List<Expression> evenRootParams) {
		if (expr == null) {
			return;
		}

		if (expr.head().equals(F.Power)) {
			IExpr param = expr.getAt(1).eval();
			IExpr power = expr.getAt(2).eval();
			if (!power.isNumber()) {
				return;
			}
			double powerValue = power.evalf();
			if (powerValue <= 0 || powerValue >= 1) {
				return;
			}
			if (!power.head().equals(F.Rational)) {
				return;
			}
			String[] rational = power.toString().split("/");
			Integer numerator = Integer.valueOf(rational[0]);
			Integer denominator = Integer.valueOf(rational[1]);
			if (denominator % 2 != 0) {
				return;
			}
			Expression evenRootParam = new Expression(String.format("(%s)^%s", param.toString(), numerator.toString()));
			evenRootParams.add(evenRootParam);
		}

		for (int i = 1; i <= expr.argSize(); i++) {
			searchEvenRootParams(expr.getAt(i), evenRootParams);
		}
	}
}
