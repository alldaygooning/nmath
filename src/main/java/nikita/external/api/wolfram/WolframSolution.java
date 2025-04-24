package nikita.external.api.wolfram;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nikita.math.construct.Interval;
import nikita.math.construct.Variable;
import nikita.math.construct.expression.Expression;
import nikita.math.exception.external.api.wolfram.WolframSolutionDecoupleException;

public class WolframSolution {

	List<Expression> expressions = new ArrayList<Expression>();
	Variable variable;
	Interval n = new Interval();

	public WolframSolution(String lhs, String rhs, String n) {
		this(lhs, rhs);

		this.n = new Interval();
		String leftReg = "^\\s*(-?\\d+)\\s*<=\\s*n\\b";
		Pattern leftPattern = Pattern.compile(leftReg);
		Matcher leftMatcher = leftPattern.matcher(n);
		if (leftMatcher.find()) {
			this.n.setLeft(new BigDecimal(leftMatcher.group(1)));
		}

		String rightReg = "\\bn\\s*<=\\s*(-?\\d+)\\s*$";
		Pattern rightPatter = Pattern.compile(rightReg);
		Matcher rightMatcher = rightPatter.matcher(n);
		if (rightMatcher.find()) {
			this.n.setRight(new BigDecimal(rightMatcher.group(1)));
		}
	}

	public WolframSolution(String lhs, String rhs) {
		lhs = lhs.replace(" ", "");
		this.variable = new Variable(lhs);

		rhs = normalize(rhs);
		if (rhs.contains("±")) {
			rhs = rhs.replace("±", "");
			this.expressions.add(new Expression(String.format("-(%s)", rhs)));
		}
		this.expressions.add(new Expression(rhs));
	}

	private String normalize(String string) {
		string = string.replaceAll("(?<![A-Za-z])e(?![A-Za-z])", "E");
		string = string.replace("π", " Pi ");
		return string;
	}

	public List<Expression> decouple() {
		List<Expression> decoupledExpressions = new ArrayList<Expression>();
		for (Expression expression : expressions) {
			if (n.isFinite()) {
				BigDecimal x = n.getLeft();
				while (x.compareTo(n.getRight()) <= 0) {
					Variable nVar = new Variable("n", x);
					Expression decoupledExpression = expression.evaluateAt(nVar);

					x = x.add(BigDecimal.ONE);
					if (decoupledExpression.isComplex()) {
						continue;
					}

					decoupledExpressions.add(decoupledExpression);

				}
			} else if (n.getLeft() == null && n.getRight() == null) {
				Expression decoupledExpression = expression;
				decoupledExpressions.add(decoupledExpression);
			} else {
				throw new WolframSolutionDecoupleException(String.format(
						"Unable to decouple Wolfram Solution Expression '%s' because it has infinite number of solutions on the Interval",
						expression));
			}
		}
		return decoupledExpressions;
	}
}
