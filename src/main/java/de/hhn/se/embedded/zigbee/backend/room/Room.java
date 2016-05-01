package de.hhn.se.embedded.zigbee.backend.room;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.hhn.se.embedded.zigbee.backend.security.User;

@Entity
@Table(name = "Room"/*
					 * , uniqueConstraints = @UniqueConstraint(columnNames = {
					 * "roomId" })
					 */)
public class Room {

	@Id
	private String roomId;

	private String name;

	@ManyToOne
	@JoinColumn(name = "myUser", nullable = false)
	@JsonIgnore
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
