package de.hhn.se.embedded.zigbee.backend.device;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.hhn.se.embedded.zigbee.backend.room.Room;

@Entity
@Table(name = "Device")
public class Device {
	
	public enum Type {
		HEATING, LIGHT
	}
	
	@Id
	private String deviceId;
	
	private String name;
	
	private String type;
	
	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public Float getTargetValue() {
		return targetValue;
	}

	public void setTargetValue(Float targetValue) {
		this.targetValue = targetValue;
	}

	private Float value;
	
	private Float targetValue;
	
    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@ManyToOne
    @JoinColumn(name = "myRoom", nullable = false)
    @JsonIgnore
	private Room room;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

}
