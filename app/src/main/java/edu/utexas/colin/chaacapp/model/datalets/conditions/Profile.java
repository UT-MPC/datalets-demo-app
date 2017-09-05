package edu.utexas.colin.chaacapp.model.datalets.conditions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("profile")
public class Profile extends Condition {

	@JsonProperty("attribute")
	private String attribute;

	public Profile() {
	}

	public Profile(String operator, String operand, String attribute) {
		this.form = "profile";
		this.operator = operator;
		this.operand = operand;
		this.attribute = attribute;
	}

	@Override
	@JsonIgnore
	public boolean isSubTypeValid() {
		return attribute == null;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public String toString() {
		return "Profile{" +
				"attribute='" + attribute + '\'' +
				'}';
	}
}
