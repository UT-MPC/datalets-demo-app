package edu.utexas.colin.chaacapp.model.datalets;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import edu.utexas.colin.chaacapp.model.datalets.conditions.Condition;

public class CriteriaDeserializer extends JsonDeserializer<Policy> {

	@Override
	public Policy deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		Policy policy = new Policy();

		JsonNode treeNode = p.readValueAsTree();

		JsonNode childrenNode = treeNode.get("children");
		JsonNode conditionNode = treeNode.get("condition");
		JsonNode typeNode = treeNode.get("type");

		policy.setType(typeNode.asText());

		if (childrenNode != null) {
			List<Policy> children = mapper.convertValue(childrenNode, new TypeReference<List<Policy>>(){});
			policy.setChildren(children);
		}

		if (conditionNode != null) {
			Condition condition = mapper.convertValue(conditionNode, Condition.class);
			policy.setCondition(condition);
		}

		return policy;
	}
}
