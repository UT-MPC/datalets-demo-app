package edu.utexas.colin.chaacapp.model.datalets.conditions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.utexas.colin.chaacapp.model.datalets.Policy;
import edu.utexas.colin.chaacapp.model.shared.LocalDate;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "@form")
@JsonSubTypes({
		@JsonSubTypes.Type(value = Schedule.class, name = "schedule"),
		@JsonSubTypes.Type(value = Location.class, name = "location"),
		@JsonSubTypes.Type(value = Profile.class, name = "profile"),
		@JsonSubTypes.Type(value = Group.class, name = "group")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Condition {

	public static Location location(String operator, String distance) {
		return new Location(operator, distance);
	}

	public static Schedule schedule(String operator, String duration,
									String time, LocalDate begins) {
		return new Schedule(operator, duration, time, begins);
	}

	public static Group group(String operator, String groupName) {
		return new Group(operator, groupName);
	}

	public static Profile profile(String operator, String value,
								  String field) {
		return new Profile(operator, value, field);
	}

	@JsonProperty("form")
	protected String form;
	@JsonProperty("operator")
	protected String operator;
	@JsonProperty("operand")
	protected String operand;

	public Policy finish() {
		return new Policy(this);
	}

	public boolean isValid() {
		if (form == null || operator == null || operand == null) {
			return false;
		}

		return isSubTypeValid();
	}

	abstract boolean isSubTypeValid();

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperand() {
		return operand;
	}

	public void setOperand(String operand) {
		this.operand = operand;
	}
}
