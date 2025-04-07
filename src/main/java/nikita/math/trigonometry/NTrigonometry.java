package nikita.math.trigonometry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class NTrigonometry {
	private static final Set<String> TRIGONOMETRIC_FUNCTIONS = new HashSet<>();

	static {
		Arrays.stream(TrigonometricFunction.values()).forEach(tfp -> TRIGONOMETRIC_FUNCTIONS.add(tfp.getFunctionName().toLowerCase()));
	}

	public static boolean containsTrigFunction(IExpr expr) {
		if (expr.isAST()) {
			IAST ast = (IAST) expr;
			IExpr head = ast.head();
			if (head.isSymbol()) {
				String funcName = head.toString().toLowerCase();
				if (TRIGONOMETRIC_FUNCTIONS.contains(funcName)) {
					return true;
				}
			}
			for (int i = 1; i < ast.size(); i++) {
				if (containsTrigFunction(ast.get(i))) {
					return true;
				}
			}
		}
		return false;
	}
}
