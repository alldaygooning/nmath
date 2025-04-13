package nikita.math.construct.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import nikita.math.construct.Interval;
import nikita.math.construct.Precision;
import nikita.math.construct.Variable;

public class EquationTest {

	private Equation equation;
	private Precision precision;
	private Variable xVar;

	@BeforeEach
	public void setUp() {
		xVar = new Variable("x");
		equation = new Equation(new Expression("3*x^2"), new Expression("7"));
		precision = new Precision("0.000000001");
	}

	@Test
	@DisplayName("Test solving for all roots in full interval (all real numbers)")
	public void testSolveFullInterval() {
		List<Expression> roots = equation.solve(xVar, precision);
		List<Expression> expectedRoots = Arrays.asList(new Expression("-1.5275252316519466"), new Expression("1.5275252316519466"));

		assertEquals(expectedRoots.size(), roots.size(),
				String.format("Expected %s roots but found %s roots.", expectedRoots.size(), roots.size()));

		for (Expression expectedRoot : expectedRoots) {
			boolean contains = roots.stream().anyMatch(root -> root.equals(expectedRoot));
			assertTrue(contains, "Expected root " + expectedRoot + " was not found among the computed roots.");
		}
	}

	@Test
	@DisplayName("Test solving within a positive interval where only the positive root exists")
	public void testSolvePositiveInterval() {
		Interval interval = new Interval(BigDecimal.ZERO, BigDecimal.valueOf(5));
		List<Expression> roots = equation.solve(interval, xVar, precision);

		List<Expression> expectedRoots = Arrays.asList(new Expression("1.5275252316519466"));

		assertEquals(expectedRoots.size(), roots.size(), String.format("Expected %s roots, but found %s roots in interval [%s, %s].",
				expectedRoots.size(), roots.size(), interval.getLeft(), interval.getRight()));

		for (Expression expectedRoot : expectedRoots) {
			boolean contains = roots.stream().anyMatch(root -> root.equals(expectedRoot));
			assertTrue(contains, "Expected positive root " + expectedRoot + " was not found in the computed roots.");
		}
	}

	@Test
	@DisplayName("Test solving within a negative interval where only the negative root exists")
	public void testSolveNegativeInterval() {
		Interval interval = new Interval(BigDecimal.valueOf(-5), BigDecimal.ZERO);
		List<Expression> roots = equation.solve(interval, xVar, precision);

		List<Expression> expectedRoots = Arrays.asList(new Expression("-1.5275252316519466"));

		assertEquals(expectedRoots.size(), roots.size(), String.format("Expected %s roots, but found %s roots in interval [%s, %s].",
				expectedRoots.size(), roots.size(), interval.getLeft(), interval.getRight()));

		for (Expression expectedRoot : expectedRoots) {
			boolean contains = roots.stream().anyMatch(root -> root.equals(expectedRoot));
			assertTrue(contains, "Expected negative root " + expectedRoot + " was not found in the computed roots.");
		}
	}

	@ParameterizedTest(name = "Interval [{0}, {1}] should have {2} root(s)")
	@MethodSource("intervalTestData")
	public void testSolveVariousIntervals(BigDecimal leftBound, BigDecimal rightBound, int expectedRootCount) {
		Interval interval = new Interval(leftBound, rightBound);
		List<Expression> roots = equation.solve(interval, xVar, precision);

		assertEquals(expectedRootCount, roots.size(), String.format("Expected %s roots in interval [%s, %s] but found %s.",
				expectedRootCount, leftBound, rightBound, roots.size()));
	}
	
	public static List<Object[]> intervalTestData() {
		return Arrays.asList(new Object[][] {
				{ BigDecimal.valueOf(-10), BigDecimal.valueOf(10), 2 },
				{ BigDecimal.ZERO, BigDecimal.valueOf(10), 1 },
				{ BigDecimal.valueOf(-10), BigDecimal.ZERO, 1 },
				{ BigDecimal.valueOf(2), BigDecimal.valueOf(3), 0 },
				{ BigDecimal.valueOf(1), BigDecimal.valueOf(2), 1 } });
	}

}
