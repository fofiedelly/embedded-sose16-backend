package de.hhn.se.embedded.zigbee.backend;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hhn.se.embedded.zigbee.backend.device.Device;
import de.hhn.se.embedded.zigbee.backend.device.DeviceRepository;
import de.hhn.se.embedded.zigbee.backend.room.Room;
import de.hhn.se.embedded.zigbee.backend.room.RoomRepository;
import de.hhn.se.embedded.zigbee.backend.security.User;
import de.hhn.se.embedded.zigbee.backend.security.UserRepository;

@RestController
public class CommandController {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	RoomRepository roomRepository;

	@Autowired
	DeviceRepository deviceRepository;

	@Autowired
	RabbitAdmin rabbitAdmin;

	@Autowired
	TopicExchange exchange;

	ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	UserRepository userRepository;

	@RequestMapping("/api/rooms/{roomId}/devices/{deviceId}/command")
	public ResponseEntity<String> test(@PathVariable("roomId") String roomId,
			@PathVariable("deviceId") String deviceId,
			@RequestBody Command command, HttpServletRequest req,
			HttpServletResponse res, Principal currentUser) {

		Room room = this.roomRepository.findByRoomId(roomId);
		User user = this.userRepository.findByUsername(currentUser.getName());

		if (room == null) {
			return new ResponseEntity<String>("room not found",
					HttpStatus.NOT_FOUND);
		}

		if (!room.getUser().equals(user)) {
			return new ResponseEntity<String>("not a users room",
					HttpStatus.UNAUTHORIZED);
		}

		Device d = deviceRepository.findOne(deviceId);

		if (d == null) {
			return new ResponseEntity<String>("device not found",
					HttpStatus.NOT_FOUND);
		}

		Queue queue = new Queue(roomId, false);
		rabbitAdmin.declareQueue(queue);

		rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange)
				.with(roomId));

		String json = null;
		try {
			json = objectMapper.writeValueAsString(new RoomServerCommand(command,
					deviceId));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		rabbitTemplate.convertAndSend(roomId, json);
		return new ResponseEntity<String>("command successfully sent",
				HttpStatus.OK);
	}

}
