package de.hhn.se.embedded.zigbee.backend.room;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.hhn.se.embedded.zigbee.backend.device.Device;
import de.hhn.se.embedded.zigbee.backend.device.DeviceRepository;
import de.hhn.se.embedded.zigbee.backend.security.User;
import de.hhn.se.embedded.zigbee.backend.security.UserRepository;

@RestController
public class RoomController {

	@Autowired
	RoomRepository roomRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	DeviceRepository deviceRepository;

	// @RequestMapping(value = "/api/rooms", method = RequestMethod.POST)
	// public Room addRoom(@RequestBody final Room room, HttpServletRequest req,
	// HttpServletResponse res, Principal currentUser) {
	//
	// User user = this.userRepository.findByUsername(currentUser.getName());
	// room.setUser(user);
	// this.roomRepository.save(room);
	// return room;
	// }

	@RequestMapping(value = "/api/rooms/{id}", method = RequestMethod.PUT)
	public ResponseEntity<String> addRoomPut(@PathVariable("id") String id,
			@RequestBody final Room room, HttpServletRequest req,
			HttpServletResponse res, Principal currentUser) {

		User user = this.userRepository.findByUsername(currentUser.getName());
		room.setUser(user);
		room.setRoomId(id);
		this.roomRepository.save(room);
		return new ResponseEntity<String>("room successfully registered",
				HttpStatus.OK);
	}

	@RequestMapping(value = "/api/rooms/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteRoom(@PathVariable("id") String id,
			HttpServletRequest req, HttpServletResponse res,
			Principal currentUser) {

		User user = this.userRepository.findByUsername(currentUser.getName());
		Room toDelete = this.roomRepository.findByRoomId(id);

		if (toDelete == null) {
			return new ResponseEntity<String>("room not found",
					HttpStatus.NOT_FOUND);
		}

		if (!toDelete.getUser().equals(user)) {
			return new ResponseEntity<String>("not a users room",
					HttpStatus.UNAUTHORIZED);
		}
		
		List<Device> devices = this.deviceRepository.findByRoom(toDelete);
		if(devices != null && !devices.isEmpty()){
			for(Device d : devices){
				this.deviceRepository.delete(d);
			}
		}

		this.roomRepository.delete(id);
		return new ResponseEntity<String>("room successfully removed",
				HttpStatus.OK);
	}
	
	@RequestMapping(value = "/api/rooms/{roomId}/devices/{deviceId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteDevice(@PathVariable("roomId") String roomId,@PathVariable("deviceId") String deviceId,
			HttpServletRequest req, HttpServletResponse res,
			Principal currentUser) {

		User user = this.userRepository.findByUsername(currentUser.getName());
		Room toDelete = this.roomRepository.findByRoomId(roomId);

		if (toDelete == null) {
			return new ResponseEntity<String>("room not found",
					HttpStatus.NOT_FOUND);
		}

		if (!toDelete.getUser().equals(user)) {
			return new ResponseEntity<String>("not a users room",
					HttpStatus.UNAUTHORIZED);
		}
		
		Device d = this.deviceRepository.findOne(deviceId);
		if(d == null){
			return new ResponseEntity<String>("device not found",
					HttpStatus.NOT_FOUND);
		}
		this.deviceRepository.delete(deviceId);


		return new ResponseEntity<String>("device successfully removed",
				HttpStatus.OK);
	}

	@RequestMapping(value = "/api/rooms/{id}", method = RequestMethod.GET)
	public ResponseEntity<Room> getRoom(@PathVariable("id") String id,
			HttpServletRequest req, HttpServletResponse res,
			Principal currentUser) {

		User user = this.userRepository.findByUsername(currentUser.getName());
		Room room = this.roomRepository.findByRoomId(id);

		if (room == null) {
			return new ResponseEntity<Room>(HttpStatus.NOT_FOUND);
		}

		if (!room.getUser().equals(user)) {
			return new ResponseEntity<Room>(HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Room>(room, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/rooms", method = RequestMethod.GET)
	public List<Room> getAllRooms(Principal currentUser) {

		User user = this.userRepository.findByUsername(currentUser.getName());
		return this.roomRepository.findByUser(user);

	}

	// @RequestMapping(value = "/api/rooms/{id}", method = RequestMethod.GET)
	// public Room getRoom(@PathVariable("id") String id, Principal currentUser)
	// {
	// return this.roomRepository.findByRoomId(id);
	//
	// }

	@RequestMapping(value = "/api/rooms/{id}/devices", method = RequestMethod.GET)
	public ResponseEntity<List<Device>> getDevices(@PathVariable("id") String id,
			Principal currentUser) {
		Room room = this.roomRepository.findByRoomId(id);
		User user = this.userRepository.findByUsername(currentUser.getName());
		
		if (room == null) {
			return new ResponseEntity<List<Device>>(HttpStatus.NOT_FOUND);
		}

		if (!room.getUser().equals(user)) {
			return new ResponseEntity<List<Device>>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<Device>>(this.deviceRepository.findByRoom(room),HttpStatus.OK);

	}
	
	@RequestMapping(value = "/api/rooms/{roomId}/devices/{deviceId}", method = RequestMethod.GET)
	public ResponseEntity<Device> getDevice(@PathVariable("roomId") String roomId,@PathVariable("deviceId") String deviceId,
			Principal currentUser) {
		
		Room room = this.roomRepository.findByRoomId(roomId);
		User user = this.userRepository.findByUsername(currentUser.getName());
		
		if (room == null) {
			return new ResponseEntity<Device>(HttpStatus.NOT_FOUND);
		}

		if (!room.getUser().equals(user)) {
			return new ResponseEntity<Device>(HttpStatus.UNAUTHORIZED);
		}		
		
		Device device = this.deviceRepository.findOne(deviceId);
		
		if(device == null){
			return new ResponseEntity<Device>(HttpStatus.NOT_FOUND);
		}

		return  new ResponseEntity<Device>(device, HttpStatus.OK);

	}

	@RequestMapping(value = "/api/rooms/{roomId}/devices/{deviceId}", method = RequestMethod.PUT)
	public ResponseEntity<String> postDevice(@PathVariable("roomId") String roomId,
			@PathVariable("deviceId") String deviceId,
			@RequestBody final Device device, Principal currentUser) {
		
		Room room = this.roomRepository.findByRoomId(roomId);
		User user = this.userRepository.findByUsername(currentUser.getName());
		
		if (room == null) {
			return new ResponseEntity<String>("room not found",HttpStatus.NOT_FOUND);
		}

		if (!room.getUser().equals(user)) {
			return new ResponseEntity<String>("not a users room", HttpStatus.UNAUTHORIZED);
		}		
		
		device.setRoom(room);
		device.setDeviceId(deviceId);
		this.deviceRepository.save(device);
		return  new ResponseEntity<String>("device registered", HttpStatus.OK);

	}

}
