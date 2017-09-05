package edu.utexas.colin.chaacapp.model.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.model.LatLng;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rectangle {

	public final GPSPoint min ;
	public final GPSPoint max;

	public Rectangle() {
		min = new GPSPoint(0, 0);
		max = new GPSPoint(0, 0);
	}

	public Rectangle(GPSPoint min, GPSPoint max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public String toString() {
		return "Rectangle{" +
				"min=" + min +
				", max=" + max +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Rectangle rectangle = (Rectangle) o;

		if (min != null ? !min.equals(rectangle.min) : rectangle.min != null) return false;
		return max != null ? max.equals(rectangle.max) : rectangle.max == null;

	}

	@Override
	public int hashCode() {
		int result = min != null ? min.hashCode() : 0;
		result = 31 * result + (max != null ? max.hashCode() : 0);
		return result;
	}
}
