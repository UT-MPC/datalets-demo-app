package edu.utexas.colin.chaacapp.model.users;


public class ApplicationItem {

	private String key;
	private String value;

	public ApplicationItem(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ApplicationItem{" +
				"key='" + key + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
