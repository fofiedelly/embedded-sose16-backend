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

@RestController
public class TestController {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	RabbitAdmin rabbitAdmin;

	@Autowired
	TopicExchange exchange;

	@RequestMapping("/test")
	public Test test(
			@RequestParam(value = "name", defaultValue = "unknown") String name) {

		Queue queue = new Queue(name);
		rabbitAdmin.declareQueue(queue);

		rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange)
				.with(name));

		rabbitTemplate.convertAndSend(name, "Hello from RabbitMQ!");
		return new Test("message send to queue " + name + "!");
	}

}
