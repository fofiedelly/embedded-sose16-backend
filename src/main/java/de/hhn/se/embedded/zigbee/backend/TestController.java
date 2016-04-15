package de.hhn.se.embedded.zigbee.backend;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	RabbitAdmin rabbitAdmin;

	@RequestMapping("/test")
	public Test greeting() {

		Queue queue = new Queue("sample-queue");
		rabbitAdmin.declareQueue(queue);

		TopicExchange exchange = new TopicExchange("sample-topic-exchange");
		rabbitAdmin.declareExchange(exchange);

		rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange)
				.with("sample-key"));

		// rabbitTemplate.convertAndSend(queueName, "Hello from RabbitMQ!");
		rabbitTemplate.convertAndSend("sample-key", "Hello from RabbitMQ!");
		return new Test("message send!");
	}

}
