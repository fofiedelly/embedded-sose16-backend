package de.hhn.se.embedded.zigbee.backend.timestamp;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TimestampRepository extends JpaRepository<Timestamp, String> {

//	List<Timestamp> findByDeviceDeviceIdAndTimestampBetween(String deviceId,
//			Date start, Date end);
	
	List<Timestamp> findByDeviceDeviceId(String deviceId);

}
