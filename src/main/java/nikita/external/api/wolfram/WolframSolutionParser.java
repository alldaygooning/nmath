package nikita.external.api.wolfram;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nikita.math.exception.external.api.wolfram.WolframSolutionParsingException;

public class WolframSolutionParser {

	public static WolframSolution parse(String stringSolution) {
		String hsSeparator;
		if (stringSolution.contains("=")) {
			hsSeparator = "=";
		} else if (stringSolution.contains("≈")) {
			hsSeparator = "≈";
		} else {
			throw new WolframSolutionParsingException(
					String.format("Unable to locate hand side separator in Wolfram Solution '%s'", stringSolution));
		}

		String regex = "(?<=\\s)" + Pattern.quote(hsSeparator) + "(?=\\s)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(stringSolution);

		int count = 0;
		while (matcher.find()) {
			count++;
		}

		if (count > 1) {
			throw new WolframSolutionParsingException(
					String.format("Unable to parse Wolfram Solution '%s' because it contains more that one hand side separator ('%s')",
							stringSolution, hsSeparator));
		}

		String[] sides = stringSolution.split(hsSeparator, 2);
		String lhs = sides[0];
		String rhs = sides[1];

		if (rhs.contains(" i ")) {
			return null;
		}

		String n;
		if (rhs.contains("and")) {
			sides = rhs.split("and");
			rhs = sides[0];
			n = sides[1];
			return new WolframSolution(lhs, rhs, n);
		}
		return new WolframSolution(lhs, rhs);
	}
}
