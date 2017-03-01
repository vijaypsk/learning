package hello;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@SpringBootApplication
public class Application {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
	
	@Bean
	RedisMessageListenerContainer container(final RedisConnectionFactory factory,final MessageListenerAdapter listenerAdapter){
		final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(factory);
		container.addMessageListener(listenerAdapter, new PatternTopic("chat"));
		
		return container;
	}
	
	
	@Bean
	MessageListenerAdapter listenerAdapter(final Receiver receiver){
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
	
	@Bean
	Receiver receiver(final CountDownLatch latch){
		return new Receiver(latch);
	}
	
	@Bean
	CountDownLatch latch(){
		return new CountDownLatch(1);
	}
	
	@Bean
	StringRedisTemplate template(final RedisConnectionFactory factory){
		return new StringRedisTemplate(factory);
	}
	
	
	public static void main(final String[] args)  throws InterruptedException{
		final ApplicationContext ctx = SpringApplication.run(Application.class, args);
		
		
		final StringRedisTemplate template = ctx.getBean(StringRedisTemplate.class);
		final CountDownLatch latch = ctx.getBean(CountDownLatch.class);
		
		LOGGER.info("Sending message...");
		template.convertAndSend("chat", "Hello from Redis!!!");
		
		latch.await();
		
		System.exit(0);
		
	}

}
