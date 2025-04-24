package nikita.external.api.wolfram.query;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nikita.external.api.wolfram.WolframAPI;
import nikita.external.api.wolfram.query.param.WolframQueryFormat;
import nikita.external.api.wolfram.query.param.WolframQueryOutput;
import nikita.external.api.wolfram.query.param.WolframQueryPod;
import nikita.external.api.wolfram.query.param.WolframQueryPodState;

public class WolframQuery {

	String query;
	String encodedQuery;

	WolframQueryOutput outputParam;
	WolframQueryFormat formatParam;
	List<WolframQueryPod> includedPods;
	Map<WolframQueryPodState, Integer> podStates;

	public WolframQuery(WolframQueryBuilder builder) {
		this.query = builder.query;
		this.encodedQuery = builder.encodedQuery;
		this.outputParam = builder.outputParam;
		this.formatParam = builder.formatParam;
		this.includedPods = builder.includedPods;
		this.podStates = builder.podStates;
	}

	public String getUrl() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%s?appid=%s", WolframAPI.BASE_URL, WolframAPI.APP_ID));
		builder.append(String.format("&input=%s", encodedQuery));

		if (outputParam != null) {
			builder.append(outputParam.getString());
		}

		if (formatParam != null) {
			builder.append(formatParam.getString());
		}

		if (includedPods != null) {
			includedPods.forEach(pod -> builder.append(pod.getString()));
		}

		if (podStates != null) {
			for (Entry<WolframQueryPodState, Integer> entry : podStates.entrySet()) {
				String string = entry.getKey().getString();
				Integer count = entry.getValue();
				if (count > 1) {
					string = string.replace("=", String.format("=%s@", count));
				}
				builder.append(string);
			}
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return getUrl();
	}
}
