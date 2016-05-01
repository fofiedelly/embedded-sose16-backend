package de.hhn.se.embedded.zigbee.backend;

public class Command {

	public enum CommandType {
		GET, SET
	}

	private String type;

	private float value;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

}
