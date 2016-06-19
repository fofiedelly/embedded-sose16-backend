package de.hhn.se.embedded.zigbee.backend.timestamp;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.hhn.se.embedded.zigbee.backend.device.Device;

@Entity
@Table(name = "Timestamp")
public class Timestamp {

	@Id
	private String deviceId;

	@ManyToOne
	@JoinColumn(name = "myDevice", nullable = false)
	@JsonIgnore
	private Device device;

	private Date timestamp;

	private float value;

	public Timestamp(Date time, float value) {
		this.timestamp = time;
		this.value = value;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

}
