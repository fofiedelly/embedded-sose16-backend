package de.hhn.se.embedded.zigbee.backend;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
	public String test(@RequestParam(value = "targetServer") String targetServer,
			@RequestParam(value = "targetDevice") String targetDevice,
			@RequestParam(value = "type") String type,
			@RequestParam(value = "value") float value) {

		Queue queue = new Queue(targetServer, false);
		rabbitAdmin.declareQueue(queue);

		rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange)
				.with(targetServer));
		
		Command c = new Command();
		c.setTarget(targetDevice);
		c.setValue(value);
		c.setType(type);
		
		
		String json = null;
		try {
			json = objectMapper.writeValueAsString(c);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		rabbitTemplate.convertAndSend(targetServer, json);
		return "command send";
	}

}
