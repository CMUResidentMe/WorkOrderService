import kafka from '../../config/kafkaConfig.js';

const producer = kafka.producer();

// Connects the producer when the API Gateway starts.
const connectProducer = async () => {
  try {
    await producer.connect();
    console.log('Kafka Producer connected');
  } catch (error) {
    console.error('Error connecting Kafka Producer:', error);
  }
};

// Sends a message when a new work order is created.
const sendWorkOrderEvent = async (workOrderDetails) => {
  try {
    await producer.send({
      topic: 'work-order-events',
      messages: [{ value: JSON.stringify(workOrderDetails) }],
    });
    console.log('Work order event sent to Kafka topic');
    console.log(workOrderDetails);
  } catch (error) {
    console.error('Error sending work order event:', error);
  }
};

export { connectProducer, sendWorkOrderEvent };