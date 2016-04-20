package de.hhn.se.embedded.zigbee.backend;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class CommandController {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	RabbitAdmin rabbitAdmin;

	@Autowired
	TopicExchange exchange;
	
	ObjectMapper objectMapper = new ObjectMapper();

	@RequestMapping("/command")
	public String test(@RequestBody Command command) {

		Queue queue = new Queue(command.getTargetServer(), false);
		rabbitAdmin.declareQueue(queue);

		rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange)
				.with(command.getTargetServer()));
		
	
		
		String json = null;
		try {
			json = objectMapper.writeValueAsString(command);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		rabbitTemplate.convertAndSend(command.getTargetServer(), json);
		return "command send";
	}

}
