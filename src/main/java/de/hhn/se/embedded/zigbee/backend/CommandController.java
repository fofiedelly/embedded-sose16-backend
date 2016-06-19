package de.hhn.se.embedded.zigbee.backend;

import java.security.Principal;
import java.util.Date;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hhn.se.embedded.zigbee.backend.device.Device;
import de.hhn.se.embedded.zigbee.backend.device.DeviceRepository;
import de.hhn.se.embedded.zigbee.backend.room.Room;
import de.hhn.se.embedded.zigbee.backend.room.RoomRepository;
import de.hhn.se.embedded.zigbee.backend.security.User;
import de.hhn.se.embedded.zigbee.backend.security.UserRepository;
import de.hhn.se.embedded.zigbee.backend.timestamp.Timestamp;
import de.hhn.se.embedded.zigbee.backend.timestamp.TimestampRepository;

@RestController
@Controller
public class CommandController {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	RoomRepository roomRepository;

	@Autowired
	TimestampRepository timestampRepository;

	@Autowired
	DeviceRepository deviceRepository;

	@Autowired
	RabbitAdmin rabbitAdmin;

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	TopicExchange exchange;

	ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	UserRepository userRepository;

	// @RequestMapping("/api/rooms/{roomId}/devices/{deviceId}/command")
	// public ResponseEntity<String> test(@PathVariable("roomId") String roomId,
	// @PathVariable("deviceId") String deviceId,
	// @RequestBody Command command, HttpServletRequest req,
	// HttpServletResponse res, Principal currentUser) {
	//
	// Room room = this.roomRepository.findByRoomId(roomId);
	// User user = this.userRepository.findByUsername(currentUser.getName());
	//
	// if (room == null) {
	// return new ResponseEntity<String>("room not found",
	// HttpStatus.NOT_FOUND);
	// }
	//
	// if (!room.getUser().equals(user)) {
	// return new ResponseEntity<String>("not a users room",
	// HttpStatus.UNAUTHORIZED);
	// }
	//
	// Device d = deviceRepository.findOne(deviceId);
	//
	// if (d == null) {
	// return new ResponseEntity<String>("device not found",
	// HttpStatus.NOT_FOUND);
	// }
	//
	// Queue queue = new Queue(roomId, false);
	// rabbitAdmin.declareQueue(queue);
	//
	// rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange)
	// .with(roomId));
	//
	// String json = null;
	// try {
	// json = objectMapper.writeValueAsString(new RoomServerCommand(
	// command, deviceId));
	// } catch (JsonProcessingException e) {
	// e.printStackTrace();
	// }
	//
	// rabbitTemplate.convertAndSend(roomId, json);
	// return new ResponseEntity<String>("command successfully sent",
	// HttpStatus.OK);
	// }
	// @MessageMapping("/hello1")
	// @SendTo("/rooms/{roomId}/devices/{deviceId}")
	// public Device greeting() throws Exception {
	// Thread.sleep(3000); // simulated delay
	// return new Device();
	// }

	// @MessageMapping("/hello")
	// @SendTo("/rooms/{roomId}/devices/{deviceId}")
	@RequestMapping(value = "/api/rooms/{roomId}/devices/{deviceId}", method = RequestMethod.PATCH)
	public ResponseEntity updateDevice(@PathVariable("roomId") String roomId,
			@PathVariable("deviceId") String deviceId,
			@RequestBody final Device device, Principal currentUser) {

		Room room = this.roomRepository.findByRoomId(roomId);
		User user = this.userRepository.findByUsername(currentUser.getName());

		if (room == null) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage(
					"room not found"), HttpStatus.NOT_FOUND);
		}

		if (!room.getUser().equals(user)) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage(
					"not a users room"), HttpStatus.UNAUTHORIZED);
		}

		device.setRoom(room);
		device.setDeviceId(deviceId);

		Device fromDb = this.deviceRepository.findOne(deviceId);
		if (fromDb == null) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage(
					"device not found"), HttpStatus.NOT_FOUND);
		}

		if (device.getName() != null) {
			fromDb.setName(device.getName());
		}

		if (device.getValue() != null) {
			fromDb.setValue(device.getValue());
			this.timestampRepository.save(new Timestamp(new Date(), device
					.getValue(), device));
		}

		if (device.getTargetValueOnDevice() != null) {
			fromDb.setTargetValueOnDevice(device.getTargetValueOnDevice());
		}

		if (device.getTargetValue() != null) {
			fromDb.setTargetValue(device.getTargetValue());
			handleDeviceTargetValueChange(fromDb);
		}

		if (device.getName() != null) {
			fromDb.setName(device.getName());
		}

		this.deviceRepository.save(fromDb);

		String sendTo = "/rooms/" + roomId + "/devices/" + deviceId;
		// fromDb.setName("Fucking awesome!!!");
		this.template.convertAndSend(sendTo, fromDb);
		return new ResponseEntity<Device>(fromDb, HttpStatus.OK);

	}

	private void handleDeviceTargetValueChange(Device device) {
		String roomId = device.getRoom().getRoomId();

		Queue queue = new Queue(roomId, false);
		rabbitAdmin.declareQueue(queue);

		rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange)
				.with(roomId));

		String json = null;
		try {
			json = objectMapper.writeValueAsString(device);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		rabbitTemplate.convertAndSend(roomId, json);

	}

}
