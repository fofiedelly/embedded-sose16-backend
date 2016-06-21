package de.hhn.se.embedded.zigbee.backend.timestamp;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.hhn.se.embedded.zigbee.backend.device.Device;

@Entity
@Table(name = "Timestamp")
public class Timestamp {

	@ManyToOne
	@JoinColumn(name = "myDevice", nullable = false)
	@JsonIgnore
	private Device device;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String Id;

	private float targetValue;

	private Date timestamp;

	private float value;

	public Timestamp() {

	}

	public Timestamp(Date time, float value, float targetValue, Device device) {
		this.timestamp = time;
		this.value = value;
		this.device = device;
		this.targetValue = targetValue;
	}
	
	

	public Device getDevice() {
		return device;
	}

	public String getId() {
		return Id;
	}

	public float getTargetValue() {
		return targetValue;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public float getValue() {
		return value;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public void setId(String id) {
		Id = id;
	}

	public void setTargetValue(float targetValue) {
		this.targetValue = targetValue;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public void setValue(float value) {
		this.value = value;
	}

}
