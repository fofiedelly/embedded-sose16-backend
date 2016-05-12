package de.hhn.se.embedded.zigbee.backend;

import javax.servlet.Filter;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CharacterEncodingFilter;

import de.hhn.se.embedded.zigbee.backend.device.Device;
import de.hhn.se.embedded.zigbee.backend.device.DeviceRepository;
import de.hhn.se.embedded.zigbee.backend.room.Room;
import de.hhn.se.embedded.zigbee.backend.room.RoomRepository;
import de.hhn.se.embedded.zigbee.backend.security.User;
import de.hhn.se.embedded.zigbee.backend.security.UserRepository;
import de.hhn.se.embedded.zigbee.backend.security.UserRole;

@RestController
@SpringBootApplication
public class Application {

	final static String queueName = "spring-boot";

	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	DeviceRepository deviceRepository;

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("ha-exchange");
	}

	@Bean
	SimpleMessageListenerContainer container(
			ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	Receiver receiver() {
		return new Receiver();
	}

	@Bean
	RabbitAdmin admin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public InitializingBean insertDefaultUsers() {
		return new InitializingBean() {
			@Autowired
			private UserRepository userRepository;

			@Override
			public void afterPropertiesSet() {
				addUser("admin", "admin");
				addUser("user", "user");
				
			}

			private void addUser(String username, String password) {
				
				User user = new User();
				user.setUsername(username);
				user.setPassword(new BCryptPasswordEncoder().encode(password));
				user.grantRole(username.equals("admin") ? UserRole.ADMIN
						: UserRole.USER);
				user = userRepository.save(user);
				

				String[][] rooms = { { "Wohzimmer", "001" }, { "Schlafzimmer", "002" }, { "Küche", "003" } };
				String[][] devices = { { "Heizung", "001", "20", "21", "HEATING" }, { "Licht", "002", "0", "0", "SWITCH" }, { "Nachttischlampe", "003", "0", "0", "SWITCH" } };

				for (int i = 0; i < rooms.length; i++) {
					for (int j = 0; j < rooms[i].length; j++) {

						Room room = new Room();
						room.setRoomId(rooms[i][1]);
						room.setName(rooms[i][0]);
						room.setUser(user);

						room = roomRepository.save(room);

						for (int k = 0; k < devices.length; k++) {
							for (int l = 0; l < devices[k].length; l++) {

								Float value = Float.parseFloat(devices[k][2]);
								Float tValue = Float.parseFloat(devices[k][3]);
								
								Device device = new Device();
								device.setDeviceId(room.getRoomId() + devices[k][1]);
								device.setName(devices[k][0]);
								device.setRoom(room);
								device.setTargetValue(tValue);
								device.setValue(value);
								device.setType(devices[k][4]);
								

								deviceRepository.save(device);

							}

						}

					}

				}
			}
		};
	}

	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}

}