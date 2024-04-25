package org.residentme.workorder.kafka;

import org.residentme.workorder.dto.RmNotification;
import org.residentme.workorder.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Producer class for the Kafka messaging system.
 */
@Service
public class MsgProducer {
	private static final Logger logger = LoggerFactory.getLogger(MsgProducer.class);

	/**
	 * The Kafka template.
	 */
	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

    @Value("${workorderServer.kafkaTopic}")
    private String kafkaTopic;
    
    @Value("${workorderServer.workorderCreated}")
    private String workorderCreated;
    
    @Value("${workorderServer.workorderChanged}")
    private String workorderChanged;
    
    @Value("${workorderServer.workorderDeleted}")
    private String workorderDeleted;

	/**
	 * Sends a message about work order created to the Kafka topic.
	 * @param woInfo The work order information.
	 */
	public void sendWorkOrderCreated(RmNotification woInfo) {
		String msg = JsonUtil.convert2Str(woInfo);
		kafkaTemplate.send(kafkaTopic, msg);
		logger.info(msg);
	}

	/**
	 * Sends a message about work order changed to the Kafka topic.
	 * @param woInfo The work order information.
	 */
	public void sendWorkOrderChanged(RmNotification woInfo) {
		String msg = JsonUtil.convert2Str(woInfo);
		kafkaTemplate.send(kafkaTopic, msg);
		logger.info(msg);
	}

	/**
	 * Sends a message about work order deleted to the Kafka topic.
	 * @param woInfo
	 */
	public void sendWorkOrderDeleted(RmNotification woInfo) {
		String msg = JsonUtil.convert2Str(woInfo);
		kafkaTemplate.send(kafkaTopic, msg);
		logger.info(msg);
	}
}
