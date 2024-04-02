import kafka from '../../config/kafkaConfig.js'; 

const consumer = kafka.consumer({ groupId: 'staff-service' });

// Function to handle incoming work order events
const handleWorkOrderEvent = async (eventData) => {
  // Notify staff with the eventData, perhaps through an internal API or messaging system
};

const consumeWorkOrderEvents = async () => {
  await consumer.connect();
  await consumer.subscribe({ topic: 'work-order-events', fromBeginning: true });

  await consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      const eventData = JSON.parse(message.value.toString());
      await handleWorkOrderEvent(eventData);
    },
  });
};

consumeWorkOrderEvents().catch(console.error);
