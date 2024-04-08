package org.residentme.workorder.kafka;

import org.residentme.workorder.entity.WorkOrder;
import org.residentme.workorder.kafka.msg.WorkOrderMsg;
import org.residentme.workorder.util.JsonCovert;
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
	
	public void sendWorkOrderCreated(WorkOrder wk) {
		String msg = JsonCovert.convert2Str(new WorkOrderMsg(workorderCreated, wk));
		kafkaTemplate.send(kafkaTopic, msg);
		logger.info(msg);
	}
	
	public void sendWorkOrderChanged(WorkOrder wk) {
		kafkaTemplate.send(kafkaTopic, JsonCovert.convert2Str(new WorkOrderMsg(workorderChanged, wk)));
	}
}
