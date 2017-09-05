package edu.utexas.colin.chaacapp.model.datalets.conditions;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.utexas.colin.chaacapp.model.shared.GPSPoint;
import edu.utexas.colin.chaacapp.model.shared.LocalDate;
import edu.utexas.colin.chaacapp.model.shared.Rectangle;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("location")
public class Location extends Condition {

	@JsonProperty("zone")
	private List<Rectangle> zone;
	@JsonProperty("point")
	private GPSPoint point;
	@JsonProperty("reference")
	private String reference;

	public Location() {
	}

	public Location(String operator, String operand) {
		this.form = "location";
		this.operator = operator;
		this.operand = operand;
	}

	public Location useZone(Rectangle... rectangles) {
		zone = new ArrayList<>(Arrays.asList(rectangles));
		return this;
	}

	public Location usePoint(GPSPoint point) {
		this.point = point;
		return this;
	}

	public Location useReference(String reference) {
		this.reference = reference;
		return this;
	}

	@Override
	@JsonIgnore
	public boolean isSubTypeValid() {
		if (zone == null && reference == null && point == null) {
			return false;
		} else if ((zone != null && reference != null)
				|| (zone != null && point != null)
				|| (point != null && reference != null)) {
			return false;
		}

		return true;
	}

	@JsonIgnore
	public boolean isReference() {
		return reference != null;
	}

	@JsonIgnore
	public boolean isPoint() {
		return point != null;
	}

	@JsonIgnore
	public boolean isZone() {
		return zone != null;
	}

	@JsonIgnore
	public LatLng getPointAsLatLng() {
		if (point != null) {
			return new LatLng(point.latitude, point.longitude);
		} else {
			return null;
		}
	}

	public GPSPoint getPoint() {
		return point;
	}

	public void setPoint(GPSPoint point) {
		this.point = point;
	}

	public List<Rectangle> getZone() {
		return zone;
	}

	public void setZone(List<Rectangle> zone) {
		this.zone = zone;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	@Override
	public String toString() {
		return "Location{" +
				"zone=" + zone +
				", reference='" + reference + '\'' +
				'}';
	}
}
