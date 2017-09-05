package edu.utexas.colin.chaacapp.model.datalets;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utexas.colin.chaacapp.client.DataletsRestClient;
import edu.utexas.colin.chaacapp.model.datalets.conditions.Condition;
import edu.utexas.colin.chaacapp.model.datalets.conditions.Location;
import edu.utexas.colin.chaacapp.model.datalets.conditions.Schedule;
import edu.utexas.colin.chaacapp.model.shared.GPSPoint;
import edu.utexas.colin.chaacapp.model.shared.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Datalet {

	public static Datalet testChildDatalet() {
		Policy precondition = Policy.createAnd(
				Condition.schedule("<", "60", "7:00 AM", new LocalDate(11, 7, 2016)).repeatsWeekly(Schedule.weekdays(), new LocalDate(6, 1, 2017)).finish(),
				Condition.location(">", "50").useReference("TEACHER_ID").finish()
		);

		Policy availability = Policy.createOr(
				Policy.createAnd(
						Condition.group("=", "TRUSTED_INDIVIDUAL").finish(),
						Condition.location("<", "50").finish()
				),
				Condition.profile("=", "CHILD_ID", "id").finish(),
				Condition.profile("=", "PARENT_ID", "id").finish()
		);

		Map<String, String> data = new HashMap<>();
		data.put("message", "Johnny has wandered away from his teacher");

		return new Datalet("CHILD_ID", null, data, precondition, availability);
	}

	public static void createMonster(String duration, String time, LocalDate begins, LocalDate ends,
										 String distance, String range, String name, String description,
										 GPSPoint location) {
		Policy precondition = Condition.schedule("<", duration, time, begins)
				.repeatsDaily(ends)
				.finish();

		Policy availability = Policy.createAnd(
				Condition.location("<", distance).finish(),
				Condition.profile("range", range, "application.beasthunter.level").finish()
		);

		Map<String, String> data = new HashMap<>();
		data.put("name", name);
		data.put("description", description);

		DataletsRestClient.createDatalet(new Datalet("BEASTHUNTER_ID", location, data, precondition, availability),
				new DataletsRestClient.ObjectCallback<Datalet>(){
					@Override
					public void onSuccess(Datalet monster) {
						/** Update UI with new monster */
					}
					@Override
					public void onFailure() {
						/** Display error message */
					}
		});
	}

	public static void getDataletsForUser(String userID) {
		DataletsRestClient.getAvailableDatalets(userID, new DataletsRestClient.ListCallback<Datalet>() {
			@Override
			public void onSuccess(List<Datalet> list) {
				/** Show the map of the datalets and the user */
			}

			@Override
			public void onFailure() {
				/** Show the error */
			}
		});
	}

	private String id;
	private String ownerID;
	private String title;
	private String description;
	private GPSPoint location;
	private boolean useOwnerLocation = true;
	private Long timeAvailable;
	private Map<String, String> data;
	private Policy precondition;
	private Policy availability;

	public Datalet() {

	}

	public Datalet(String ownerID, GPSPoint location, Map<String, String> data, Policy precondition, Policy availability) {
		this(ownerID, location, false, data, precondition, availability);
	}

	public Datalet(String ownerID, GPSPoint location, boolean useOwnerLocation, Map<String, String> data, Policy precondition, Policy availability) {
		this.ownerID = ownerID;
		this.data = data;
		this.precondition = precondition;
		this.availability = availability;
		this.location = location;
		this.useOwnerLocation = useOwnerLocation;
	}

	public Location checkHasLocationAccess() {
		if (availability.isOperator()) {
			for (Policy policy : availability.getChildren()) {
				if (policy.getCondition() instanceof Location) {
					return (Location) policy.getCondition();
				}
			}
		} else {
			if (availability.getCondition() instanceof Location) {
				return (Location)availability.getCondition();
			}
		}

		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public GPSPoint getLocation() {
		return location;
	}

	public void setLocation(GPSPoint location) {
		this.location = location;
	}

	public boolean isUseOwnerLocation() {
		return useOwnerLocation;
	}

	public void setUseOwnerLocation(boolean useOwnerLocation) {
		this.useOwnerLocation = useOwnerLocation;
	}

	public Long getTimeAvailable() {
		return timeAvailable;
	}

	public void setTimeAvailable(Long timeAvailable) {
		this.timeAvailable = timeAvailable;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public Policy getPrecondition() {
		return precondition;
	}

	public void setPrecondition(Policy precondition) {
		this.precondition = precondition;
	}

	public Policy getAvailability() {
		return availability;
	}

	public void setAvailability(Policy availability) {
		this.availability = availability;
	}

	@Override
	public String toString() {
		return "Datalet{" +
				"id='" + id + '\'' +
				", ownerID='" + ownerID + '\'' +
				", location=" + location +
				", timeAvailable=" + timeAvailable +
				", data=" + data +
				", precondition=" + precondition +
				", availability=" + availability +
				'}';
	}

	public void test() {
		createMonster(null, null, null, null, null, null, null, null, null);
	}
}
