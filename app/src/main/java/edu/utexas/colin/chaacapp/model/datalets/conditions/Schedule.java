package edu.utexas.colin.chaacapp.model.datalets.conditions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.colin.chaacapp.model.shared.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("schedule")
public class Schedule extends Condition {

	public static final String DAILY = "daily";
	public static final String WEEKLY = "weekly";
	public static final String MONTHLY = "monthly";

	public static List<String> weekdays() {
		List<String> days = new ArrayList<>();
		days.add("1");
		days.add("2");
		days.add("3");
		days.add("4");
		days.add("5");

		return days;
	}

	public static List<String> weekend() {
		List<String> days = new ArrayList<>();
		days.add("6");
		days.add("7");

		return days;
	}

	@JsonProperty("time")
	private String time;
	@JsonProperty("begins")
	private LocalDate begins;
	@JsonProperty("repeats")
	private boolean repeats;
	@JsonProperty("ends")
	private LocalDate ends;
	@JsonProperty("frequency")
	private String frequency;
	@JsonProperty("days")
	private List<String> days;

	public Schedule() {

	}

	public Schedule(String operator, String operand, String time, LocalDate begins) {
		this.form = "schedule";
		this.operator = operator;
		this.operand = operand;
		this.time = time;
		this.begins = begins;
	}

	public Schedule repeatsDaily() {
		return repeatsDaily(null);
	}

	public Schedule repeatsDaily(LocalDate ends) {
		repeats(DAILY, ends);
		return this;
	}

	public Schedule repeatsWeekly(List<String> days) {
		return repeatsWeekly(days, null);
	}

	public Schedule repeatsWeekly(List<String> days, LocalDate ends) {
		repeats(WEEKLY, ends);
		this.days = days;
		return this;
	}

	public Schedule repeatsMonthly(List<String> days) {
		return repeatsMonthly(days, null);
	}

	public Schedule repeatsMonthly(List<String> days, LocalDate ends) {
		repeats(MONTHLY, ends);
		this.days = days;
		return this;
	}

	@Override
	@JsonIgnore
	boolean isSubTypeValid() {
		if (time == null || begins == null) {
			return false;
		} if (repeats) {
			if (DAILY.equals(frequency)) {
				// DO NOTHING
			} else if (WEEKLY.equals(frequency)) {
				if (days == null || days.isEmpty()) {
					return false;
				}
			} else if (MONTHLY.equals(frequency)) {
				if (days == null || days.isEmpty()) {
					return false;
				}
			} else {
				return false;
			}
		}

		return true;
	}

	private void repeats(String frequency, LocalDate ends) {
		this.repeats = true;
		this.frequency = frequency;
		if (ends != null) {
			this.ends = ends;
		}
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public LocalDate getBegins() {
		return begins;
	}

	public void setBegins(LocalDate begins) {
		this.begins = begins;
	}

	public boolean isRepeats() {
		return repeats;
	}

	public void setRepeats(boolean repeats) {
		this.repeats = repeats;
	}

	public LocalDate getEnds() {
		return ends;
	}

	public void setEnds(LocalDate ends) {
		this.ends = ends;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public List<String> getDays() {
		return days;
	}

	public void setDays(List<String> days) {
		this.days = days;
	}

	@Override
	public String toString() {
		return "Schedule{" +
				"time='" + time + '\'' +
				", begins='" + begins + '\'' +
				", repeats=" + repeats +
				", ends='" + ends + '\'' +
				", frequency='" + frequency + '\'' +
				", days=" + days +
				'}';
	}
}
