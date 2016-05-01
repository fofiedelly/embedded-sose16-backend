package de.hhn.se.embedded.zigbee.backend;

public class RoomServerCommand extends Command {

	private String targetDevice;

	public String getTargetDevice() {
		return targetDevice;
	}

	public void setTargetDevice(String targetDevice) {
		this.targetDevice = targetDevice;
	}

	public RoomServerCommand(Command command, String targetDevice) {
		this.targetDevice = targetDevice;
		this.setType(command.getType());
		this.setValue(command.getValue());

	}

}
