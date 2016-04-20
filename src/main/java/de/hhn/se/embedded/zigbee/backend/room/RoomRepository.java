package de.hhn.se.embedded.zigbee.backend.room;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.hhn.se.embedded.zigbee.backend.security.User;

public interface RoomRepository extends JpaRepository<Room, Long> {

	List<Room> findByUser(User user);

}
