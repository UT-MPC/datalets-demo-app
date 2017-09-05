package edu.utexas.colin.chaacapp.model.datalets.conditions;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("group")
public class Group extends Condition {

	@Override
	boolean isSubTypeValid() {
		return true;
	}

	public Group() {
	}

	public Group(String operator, String operand) {
		this.form = "group";
		this.operator = operator;
		this.operand = operand;
	}

	@Override
	public String toString() {
		return "Group{}";
	}
}
