package de.hhn.se.embedded.zigbee.backend.device;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.hhn.se.embedded.zigbee.backend.room.Room;

public interface DeviceRepository extends JpaRepository<Device, String> {
	
	List<Device> findByRoom(Room room);

}
