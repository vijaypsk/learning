package hello;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class Receiver {
	public static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
	
	private final CountDownLatch latch;
	
	@Autowired
	public Receiver(final CountDownLatch latch){
		this.latch=latch;
	}
	
	public void receiveMessage(final String message){
		LOGGER.info("Received <" + message + ">");
		latch.countDown();
	}

}
