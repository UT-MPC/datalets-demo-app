package edu.utexas.colin.chaacapp.model.datalets;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.utexas.colin.chaacapp.model.datalets.conditions.Condition;


@JsonDeserialize(using = CriteriaDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Policy {

	public static final String AND = "and";
	public static final String OR = "or";
	public static final String CONDITION = "condition";

	public static Policy createAnd(Policy... policy) {
		return new Policy(AND, policy);
	}

	public static Policy createOr(Policy... policy) {
		return new Policy(OR, policy);
	}


	@JsonProperty("type")
	private String type;
	@JsonProperty("children")
	private List<Policy> children;
	@JsonProperty("condition")
	private Condition condition;

	public Policy() {
	}

	public Policy(String type, Policy... children) {
		this.type = type;
		this.children = new ArrayList<>(Arrays.asList(children));

		if (!isOperator()) {
			throw new IllegalArgumentException("incorrect type");
		}
	}

	public Policy(Condition condition) {
		type = CONDITION;
		this.condition = condition;
	}

	public Policy and(Policy... policy) {
		List<Policy> children = new ArrayList<>(Arrays.asList(policy));
		children.add(this);
		return new Policy(AND, children.toArray(new Policy[children.size()]));
	}

	public Policy or(Policy... policy) {
		List<Policy> children = new ArrayList<>(Arrays.asList(policy));
		children.add(this);
		return new Policy(OR, children.toArray(new Policy[children.size()]));
	}

	@JsonIgnore
	public boolean isOperator() {
		return OR.equals(type) || AND.equals(type);
	}

	@JsonIgnore
	public boolean isCondition() {
		return CONDITION.equals(type);
	}

	public void addCondition(Condition condition) {
		if (!isOperator()) {
			throw new IllegalArgumentException("Policy does not have children");
		}

		this.children.add(new Policy(condition));
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Policy> getChildren() {
		return children;
	}

	public void setChildren(List<Policy> children) {
		this.children = children;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	@Override
	public String toString() {
		return "Policy{" +
				"type='" + type + '\'' +
				", children=" + children +
				", condition=" + condition +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Policy policy = (Policy) o;

		if (type != null ? !type.equals(policy.type) : policy.type != null) return false;
		if (children != null ? !children.equals(policy.children) : policy.children != null) return false;
		return condition != null ? condition.equals(policy.condition) : policy.condition == null;

	}

	@Override
	public int hashCode() {
		int result = type != null ? type.hashCode() : 0;
		result = 31 * result + (children != null ? children.hashCode() : 0);
		result = 31 * result + (condition != null ? condition.hashCode() : 0);
		return result;
	}
}
