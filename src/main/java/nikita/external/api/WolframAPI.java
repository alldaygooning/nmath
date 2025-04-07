package nikita.external.api;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import nikita.math.construct.Precision;

public class WolframAPI {
	private static final String APP_ID = "R3GKXU-9LQ3PWYPVG";
	private static final String BASE_URL = "http://api.wolframalpha.com/v2/query";

	static final int significantDigitsMax = 20;

	public static String query(String input, Precision precision) throws IOException, InterruptedException {
		String encodedInput = URLEncoder.encode(input, StandardCharsets.UTF_8);

		int digits = Integer.min(precision.getPrecision().intValue(), significantDigitsMax); // Пока не работает, надо разобраться!

		String url = String.format("%s?appid=%s&input=%s&output=XML&sigfigs=%d", BASE_URL, APP_ID, encodedInput, digits);

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body();
	}

	public static List<String> getSolutions(String xmlString)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		List<String> solutions = new ArrayList<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "//pod[@title='Results']/subpod/plaintext";
		NodeList nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

		for (int i = 0; i < nodeList.getLength(); i++) {
			String textContent = nodeList.item(i).getTextContent().trim();
			if (!textContent.isEmpty()) {
				solutions.add(textContent);
			}
		}
		return solutions;
	}

	public static List<IExpr> getRules(String solution) {
		List<IExpr> rules = new ArrayList<>();
		ExprEvaluator evaluator = new ExprEvaluator();

		solution = solution.trim();
		if (solution.endsWith(".")) {
			solution = solution.substring(0, solution.length() - 1).trim();
		}

		int andIndex = solution.toLowerCase().indexOf(" and ");
		if (andIndex != -1) {
			solution = solution.substring(0, andIndex).trim();
		}

		String[] parts = new String[0];
		if (solution.contains("=")) {
			parts = solution.split("=");
		} else if (solution.contains("≈")) {
			parts = solution.split("≈");
		}
		if (parts.length != 2) {
			System.out.println("ERROR");
			return rules;
		}

		String variable = parts[0].trim();
		String expression = parts[1].trim();

		expression = expression.replace("π", "Pi").replace("n", "k");

		List<String> ruleStrings = new ArrayList<>();

		if (expression.startsWith("±")) {
			String rhs = expression.substring(1).trim();
			if (rhs.startsWith("(") && rhs.endsWith(")")) {
				rhs = rhs.substring(1, rhs.length() - 1).trim();
			}
			rhs = rhs.replace("π", "Pi").replace("n", "k");

			String positiveRuleStr = variable + " -> " + rhs;
			String negativeRuleStr = variable + " -> -" + rhs;
			ruleStrings.add(positiveRuleStr);
			ruleStrings.add(negativeRuleStr);
		} else {
			String ruleStr = variable + " -> " + expression;
			ruleStrings.add(ruleStr);
		}

		for (String ruleStr : ruleStrings) {
			try {
				IExpr ruleExpr = evaluator.eval(ruleStr);
				rules.add(ruleExpr);
			} catch (Exception e) {
				System.out.println("ERROR");
			}
		}

		return rules;
	}
}
