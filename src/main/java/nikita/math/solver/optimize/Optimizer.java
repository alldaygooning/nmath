package nikita.math.solver.optimize;

import nikita.math.construct.Precision;
import nikita.math.construct.expression.Expression;
import nikita.math.construct.point.Point;

public abstract class Optimizer {

	String name = "Generic Optimizer";

	public abstract void optimize(Expression expression, Point initialApproximation, Precision precision);
}
