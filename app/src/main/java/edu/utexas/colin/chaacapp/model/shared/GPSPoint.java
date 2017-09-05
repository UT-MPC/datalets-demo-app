package edu.utexas.colin.chaacapp.model.shared;


public class GPSPoint {
	public static final int R = 6371;

	public double latitude;
	public double longitude;

	public GPSPoint() {
		latitude = 0.0;
		longitude = 0.0;
	}

	public GPSPoint(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double distance(GPSPoint point) {
		Double latDistance = Math.toRadians(point.latitude - latitude);
		Double lonDistance = Math.toRadians(point.longitude - longitude);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(point.longitude))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000;

		return Math.abs(distance);
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "GPSPoint{" +
				"latitude=" + latitude +
				", longitude=" + longitude +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GPSPoint location = (GPSPoint) o;

		if (Double.compare(location.latitude, latitude) != 0) return false;
		return Double.compare(location.longitude, longitude) == 0;

	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
