package nikita.external.api.wolfram.query;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nikita.external.api.wolfram.query.param.WolframQueryFormat;
import nikita.external.api.wolfram.query.param.WolframQueryOutput;
import nikita.external.api.wolfram.query.param.WolframQueryPod;
import nikita.external.api.wolfram.query.param.WolframQueryPodState;

public class WolframQueryBuilder {

	String query;
	String encodedQuery;

	WolframQueryOutput outputParam;
	WolframQueryFormat formatParam;
	List<WolframQueryPod> includedPods;
	Map<WolframQueryPodState, Integer> podStates;

	public WolframQueryBuilder(String query) {
		this.query = query;
		this.encodedQuery = WolframQueryBuilder.encode(query);
	}

	public WolframQueryBuilder output(WolframQueryOutput outputParam) {
		this.outputParam = outputParam;
		return this;
	}

	public WolframQueryBuilder format(WolframQueryFormat formatParam) {
		this.formatParam = formatParam;
		return this;
	}

	public WolframQueryBuilder include(WolframQueryPod pod) {
		if (includedPods == null) {
			includedPods = new ArrayList<WolframQueryPod>();
		}
		if (!includedPods.contains(pod)) {
			includedPods.add(pod);
		}
		return this;
	}

	public WolframQueryBuilder add(WolframQueryPodState state, int count) {
		if(podStates == null) {
			podStates = new HashMap<WolframQueryPodState, Integer>();
		}

		if (!podStates.containsKey(state)) {
			podStates.put(state, count);
			return this;
		}

		podStates.put(state, podStates.get(state) + count);
		return this;
	}


	public WolframQuery toQuery() {
		return new WolframQuery(this);
	}

	public static String encode(String query) {
		return URLEncoder.encode(query, StandardCharsets.UTF_8);
	}
}
