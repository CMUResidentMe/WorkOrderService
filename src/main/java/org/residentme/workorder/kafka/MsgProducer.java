package org.residentme.workorder.kafka;

import org.residentme.workorder.dto.RmNotification;
import org.residentme.workorder.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MsgProducer {
	private static final Logger logger = LoggerFactory.getLogger(MsgProducer.class);
	
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
	
	public void sendWorkOrderCreated(RmNotification woInfo) {
		String msg = JsonUtil.convert2Str(woInfo);
		kafkaTemplate.send(kafkaTopic, msg);
		logger.info(msg);
	}
	
	public void sendWorkOrderChanged(RmNotification woInfo) {
		String msg = JsonUtil.convert2Str(woInfo);
		kafkaTemplate.send(kafkaTopic, msg);
		logger.info(msg);
	}
	
	public void sendWorkOrderDeleted(RmNotification woInfo) {
		String msg = JsonUtil.convert2Str(woInfo);
		kafkaTemplate.send(kafkaTopic, msg);
		logger.info(msg);
	}
}
